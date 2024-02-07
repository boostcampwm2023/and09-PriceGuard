import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductAddDto, ProductAddDtoV1 } from '../dto/product.add.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { TrackingProductDto } from 'src/dto/product.tracking.dto';
import { ProductInfoDto } from 'src/dto/product.info.dto';
import { TrackingProductRepository } from './trackingProduct.repository';
import { ProductRepository } from './product.repository';
import { ProductDetailsDto } from 'src/dto/product.details.dto';
import { InjectModel } from '@nestjs/mongoose';
import { ProductPrice } from 'src/schema/product.schema';
import { Model } from 'mongoose';
import { PriceDataDto } from 'src/dto/price.data.dto';
import { ADD_PRODUCT_LIMIT, MAX_TRACKING_RANK, NINETY_DAYS, THIRTY_DAYS } from 'src/constants';
import { ProductRankCacheDto } from 'src/dto/product.rank.cache.dto';
import Redis from 'ioredis';
import { InjectRedis } from '@songkeys/nestjs-redis';
import { CacheService } from 'src/cache/cache.service';
import { getProductInfo, identifyProductByUrl } from 'src/utils/product.info';
import { UsersRepository } from 'src/user/user.repository';

@Injectable()
export class ProductService {
    constructor(
        @InjectRepository(TrackingProductRepository)
        private trackingProductRepository: TrackingProductRepository,
        @InjectRepository(ProductRepository)
        private productRepository: ProductRepository,
        @InjectRepository(UsersRepository)
        private usersRepository: UsersRepository,
        @InjectModel(ProductPrice.name)
        private productPriceModel: Model<ProductPrice>,
        @InjectRedis() private readonly redis: Redis,
        private cacheService: CacheService,
    ) {}

    async verifyUrl(productUrlDto: ProductUrlDto, apiVersion: number): Promise<ProductInfoDto> {
        const { productUrl } = productUrlDto;
        const { shop, productCode } = identifyProductByUrl(productUrl, apiVersion);
        return await getProductInfo(shop, productCode);
    }

    async addProduct(userId: string, productAddDto: ProductAddDto) {
        const productAddDtoV1 = new ProductAddDtoV1();
        productAddDtoV1.productCode = productAddDto.productCode;
        productAddDtoV1.targetPrice = productAddDto.targetPrice;
        productAddDtoV1.shop = '11번가';
        await this.addProductV1(userId, productAddDtoV1);
    }

    async addProductV1(userId: string, productAddDto: ProductAddDtoV1) {
        const { shop, productCode, targetPrice } = productAddDto;
        const existProduct = await this.productRepository.findOne({
            where: { productCode: productCode, shop: shop },
        });
        const product = existProduct ?? (await this.firstAddProduct(shop, productCode));
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { productId: product.id, userId: userId },
        });
        if (trackingProduct) {
            throw new HttpException('이미 등록된 상품입니다.', HttpStatus.CONFLICT);
        }
        await this.checkProductLimit(userId);
        const userCount = parseInt(await this.redis.zincrby('userCount', 1, product.id));
        const newProductRank = {
            id: product.id,
            productName: product.productName,
            productCode: product.productCode,
            shop: product.shop,
            imageUrl: product.imageUrl,
            userCount: userCount,
        };
        const newTrackingProduct = await this.trackingProductRepository.saveTrackingProduct(
            userId,
            product.id,
            targetPrice,
        );
        const trackingProductList = this.cacheService.getTrackingProduct(userId);
        if (trackingProductList) {
            newTrackingProduct.product = product;
            this.cacheService.addValueTrackingProduct(userId, newTrackingProduct);
        }
        this.cacheService.updateProductRank(newProductRank);
    }

    async getTrackingList(userId: string): Promise<TrackingProductDto[]> {
        const trackingProductList = await this.getTrackingProduct(userId);
        if (trackingProductList.length === 0) return [];
        const trackingListInfo = trackingProductList.map(async ({ product, targetPrice, isAlert }) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
            const { price } = priceData[priceData.length - 1];
            return {
                productName,
                productCode,
                shop,
                imageUrl,
                targetPrice: targetPrice,
                price,
                isAlert,
                priceData,
            };
        });
        const result = await Promise.all(trackingListInfo);
        return result;
    }

    async getRecommendList() {
        const recommendList = this.cacheService.getAllProductRank();
        const recommendListInfo = recommendList.map(async (product, index) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
            const { price } = priceData[priceData.length - 1];
            return {
                productName,
                productCode,
                shop,
                imageUrl,
                price,
                rank: index + 1,
                priceData,
            };
        });
        const result = await Promise.all(recommendListInfo);
        return result;
    }

    async getProductDetails(userId: string, productCode: string): Promise<ProductDetailsDto> {
        const selectProduct = await this.productRepository.findOne({
            where: { productCode: productCode },
        });
        if (!selectProduct) {
            throw new HttpException('상품 정보가 존재하지 않습니다.', HttpStatus.NOT_FOUND);
        }
        return await this.getProductDetailsV1(userId, '11번가', productCode);
    }

    async getProductDetailsV1(userId: string, shop: string, productCode: string): Promise<ProductDetailsDto> {
        const selectProduct = await this.productRepository.findOne({
            where: { productCode: productCode, shop: shop },
        });
        if (!selectProduct) {
            throw new HttpException('상품 정보가 존재하지 않습니다.', HttpStatus.NOT_FOUND);
        }
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { userId: userId, productId: selectProduct.id },
        });
        await this.trackingProductRepository.getUserCount(selectProduct.id);
        const idx = this.cacheService.findIndexProductRank(selectProduct.id);
        const rank = idx === -1 ? idx : idx + 1;
        const priceData = await this.getPriceData(selectProduct.id, NINETY_DAYS);
        const { price } = priceData[priceData.length - 1];
        const lowestPrice = Math.min(...priceData.map((item) => item.price));
        return {
            productName: selectProduct.productName,
            shop: selectProduct.shop,
            imageUrl: selectProduct.imageUrl,
            rank: rank,
            shopUrl: selectProduct.shopUrl,
            targetPrice: trackingProduct ? trackingProduct.targetPrice : -1,
            lowestPrice: lowestPrice,
            price: price,
            priceData: priceData,
        };
    }

    async updateTargetPrice(userId: string, productAddDto: ProductAddDto) {
        const productAddDtoV1 = new ProductAddDtoV1();
        productAddDtoV1.productCode = productAddDto.productCode;
        productAddDtoV1.targetPrice = productAddDto.targetPrice;
        productAddDtoV1.shop = '11번가';
        await this.updateTargetPriceV1(userId, productAddDtoV1);
    }

    async updateTargetPriceV1(userId: string, productAddDto: ProductAddDtoV1) {
        const product = await this.findTrackingProductByCode(userId, productAddDto.shop, productAddDto.productCode);
        product.targetPrice = productAddDto.targetPrice;
        product.isFirst = true;
        this.cacheService.updateValueTrackingProdcut(userId, product);
        await this.trackingProductRepository.save(product);
    }

    async deleteProduct(userId: string, productCode: string) {
        await this.deleteProductV1(userId, '11번가', productCode);
    }

    async deleteProductV1(userId: string, shop: string, productCode: string) {
        const product = await this.findTrackingProductByCode(userId, shop, productCode);
        const currentProduct = this.cacheService.getProductRank(product.productId);
        await this.redis.zincrby('userCount', -1, product.productId);
        if (currentProduct) {
            currentProduct.userCount--;
            const productCount = await this.redis.zcard('userCount');
            if (productCount > MAX_TRACKING_RANK) {
                await this.deleteUpdateCache(currentProduct);
            } else {
                this.cacheService.updateProductRank(currentProduct);
            }
        }
        if (product) {
            this.cacheService.deleteValueTrackingProdcut(userId, product);
        }
        await this.trackingProductRepository.remove(product);
    }

    async deleteUpdateCache(currentProduct: ProductRankCacheDto) {
        const nextProductData = await this.redis.zrevrange(
            'userCount',
            MAX_TRACKING_RANK,
            MAX_TRACKING_RANK,
            'WITHSCORES',
        );
        const [nextDataId, userCount] = [nextProductData[0], parseInt(nextProductData[1])];
        if (userCount < currentProduct.userCount) {
            this.cacheService.updateProductRank(currentProduct);
            return;
        }
        if (userCount === currentProduct.userCount && nextDataId < currentProduct.id) {
            this.cacheService.updateProductRank(currentProduct);
            return;
        }
        const newProduct = await this.productRepository.findOne({
            where: { id: nextDataId },
        });
        if (!newProduct) {
            throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        const newProductRank = {
            id: newProduct.id,
            productName: newProduct.productName,
            productCode: newProduct.productCode,
            shop: newProduct.shop,
            imageUrl: newProduct.imageUrl,
            userCount: userCount,
        };
        this.cacheService.updateProductRank(currentProduct, newProductRank);
    }

    async findTrackingProductByCode(userId: string, shop: string, productCode: string) {
        const existProduct = await this.productRepository.findOne({
            where: { productCode: productCode, shop: shop },
        });
        if (!existProduct) {
            throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { userId: userId, productId: existProduct.id },
        });
        if (!trackingProduct) {
            throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        trackingProduct.product = existProduct;
        return trackingProduct;
    }

    async getPriceData(productId: string, days: number): Promise<PriceDataDto[]> {
        const endDate = new Date();
        const startDate = new Date(endDate);
        startDate.setDate(endDate.getDate() - days);
        let dataInfo = await this.productPriceModel
            .find({
                productId: productId,
                time: {
                    $gte: startDate,
                    $lte: endDate,
                },
            })
            .exec();
        if (dataInfo.length === 0) {
            dataInfo = await this.productPriceModel.find({ productId: productId }).sort({ time: -1 }).limit(1).exec();
        }
        return dataInfo.map(({ time, price, isSoldOut }) => {
            return { time: new Date(time).getTime(), price, isSoldOut };
        });
    }

    async firstAddProduct(shop: string, productCode: string) {
        const productInfo = await getProductInfo(shop, productCode);
        const product = await this.productRepository.saveProduct(productInfo);
        const updatedDataInfo = {
            productId: product.id,
            price: productInfo.productPrice,
            isSoldOut: productInfo.isSoldOut,
        };
        this.redis.set(
            `product:${product.id}`,
            JSON.stringify({
                price: productInfo.productPrice,
                isSoldOut: productInfo.isSoldOut,
                lowestPrice: productInfo.productPrice,
            }),
        );
        this.productPriceModel.create(updatedDataInfo);
        return product;
    }

    async toggleProductAlert(userId: string, productCode: string) {
        await this.toggleProductAlertV1(userId, '11번가', productCode);
    }

    async toggleProductAlertV1(userId: string, shop: string, productCode: string) {
        const product = await this.findTrackingProductByCode(userId, shop, productCode);
        product.isAlert = !product.isAlert;
        await this.trackingProductRepository.save(product);
        this.cacheService.updateValueTrackingProdcut(userId, product);
    }

    async getProductCurrentData(productId: string) {
        const cacheData = await this.redis.get(`product:${productId}`);
        if (cacheData) {
            const { price, lowestPrice } = JSON.parse(cacheData);
            return { price, lowestPrice };
        }
        const latestData = await this.productPriceModel
            .aggregate([
                {
                    $match: { productId: productId },
                },
                {
                    $sort: { time: -1 },
                },
                {
                    $group: {
                        _id: '$productId',
                        price: { $first: '$price' },
                        isSoldOut: { $first: '$isSoldOut' },
                        lowestPrice: { $min: '$price' },
                    },
                },
            ])
            .exec();
        const { price, lowestPrice } = latestData[0];
        return { price, lowestPrice };
    }

    private async getTrackingProduct(userId: string) {
        let trackingProductList = this.cacheService.getTrackingProduct(userId);
        if (!trackingProductList) {
            trackingProductList = await this.trackingProductRepository.find({
                where: { userId: userId },
                relations: ['product'],
            });
            this.cacheService.putTrackingProduct(userId, trackingProductList);
        }
        return trackingProductList;
    }

    private async checkProductLimit(userId: string) {
        const user = await this.usersRepository.findOne({ where: { id: userId } });
        if (!user) {
            throw new HttpException('사용자 정보가 존재하지 않습니다.', HttpStatus.NOT_FOUND);
        }
        const productCount = (await this.getTrackingProduct(userId)).length;
        const productLimit = ADD_PRODUCT_LIMIT[user.grade];
        if (productLimit <= productCount) {
            throw new HttpException('추가할 수 있는 상품 최대 개수를 초과하였습니다.', HttpStatus.FORBIDDEN);
        }
    }
}
