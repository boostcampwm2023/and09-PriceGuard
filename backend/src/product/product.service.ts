import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductAddDto } from '../dto/product.add.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { TrackingProductDto } from 'src/dto/product.tracking.dto';
import { ProductInfoDto } from 'src/dto/product.info.dto';
import { TrackingProductRepository } from './trackingProduct.repository';
import { ProductRepository } from './product.repository';
import { getProductInfo11st } from 'src/utils/openapi.11st';
import { ProductDetailsDto } from 'src/dto/product.details.dto';
import { InjectModel } from '@nestjs/mongoose';
import { ProductPrice } from 'src/schema/product.schema';
import { Model } from 'mongoose';
import { ProductPriceDto } from 'src/dto/product.price.dto';
import { PriceDataDto } from 'src/dto/price.data.dto';
import { KR_OFFSET, NINETY_DAYS, NO_CACHE, THIRTY_DAYS } from 'src/constants';
import { Cron } from '@nestjs/schedule';

const REGEXP_11ST =
    /http[s]?:\/\/(?:www\.|m\.)?11st\.co\.kr\/products\/(?:ma\/|m\/|pa\/)?([1-9]\d*)(?:\?.*)?(?:\/share)?/;
@Injectable()
export class ProductService {
    private productDataCache = new Map();
    constructor(
        @InjectRepository(TrackingProductRepository)
        private trackingProductRepository: TrackingProductRepository,
        @InjectRepository(ProductRepository)
        private productRepository: ProductRepository,
        @InjectModel(ProductPrice.name)
        private productPriceModel: Model<ProductPrice>,
    ) {
        this.initCache();
    }

    async initCache() {
        const latestData = await this.productPriceModel
            .aggregate([
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
        latestData.forEach((data) => {
            this.productDataCache.set(data._id, {
                price: data.price,
                isSoldOut: data.isSoldOut,
                lowestPrice: data.lowestPrice,
            });
        });
    }

    async verifyUrl(productUrlDto: ProductUrlDto): Promise<ProductInfoDto> {
        const { productUrl } = productUrlDto;
        const matchList = productUrl.match(REGEXP_11ST);
        if (!matchList) {
            throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
        }
        const productCode = matchList[1];
        return await getProductInfo11st(productCode);
    }

    async addProduct(userId: string, productAddDto: ProductAddDto) {
        const { productCode, targetPrice } = productAddDto;
        const existProduct = await this.productRepository.findOne({
            where: { productCode: productCode },
        });
        const productInfo = existProduct ?? (await getProductInfo11st(productCode));
        const product = existProduct ?? (await this.productRepository.saveProduct(productInfo));
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { productId: product.id, userId: userId },
        });
        if (trackingProduct) {
            throw new HttpException('이미 등록된 상품입니다.', HttpStatus.CONFLICT);
        }
        await this.trackingProductRepository.saveTrackingProduct(userId, product.id, targetPrice);
    }

    async getTrackingList(userId: string): Promise<TrackingProductDto[]> {
        const trackingProductList = await this.trackingProductRepository.find({
            where: { userId: userId },
            relations: ['product'],
        });
        if (trackingProductList.length === 0) {
            throw new HttpException('상품 목록을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        const trackingListInfo = trackingProductList.map(async ({ product, targetPrice }) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const { price } = this.productDataCache.get(id) ?? { price: NO_CACHE };
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
            return {
                productName,
                productCode,
                shop,
                imageUrl,
                targetPrice: targetPrice,
                price,
                priceData,
            };
        });
        const result = await Promise.all(trackingListInfo);
        return result;
    }

    async getRecommendList() {
        const recommendList = await this.trackingProductRepository.getTotalInfoRankingList();
        const recommendListInfo = recommendList.map(async (product, index) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const { price } = this.productDataCache.get(id) ?? { price: NO_CACHE };
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
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
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { userId: userId, productId: selectProduct.id },
        });
        const ranklist = await this.trackingProductRepository.getRankingList();
        const idx = ranklist.findIndex(({ id }) => id === selectProduct.id);
        const rank = idx === -1 ? idx : idx + 1;
        const priceData = await this.getPriceData(selectProduct.id, NINETY_DAYS);
        const { price, lowestPrice } = this.productDataCache.get(selectProduct.id);
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
        const product = await this.findTrackingProductByCode(userId, productAddDto.productCode);
        product.targetPrice = productAddDto.targetPrice;
        await this.trackingProductRepository.save(product);
    }

    async deleteProduct(userId: string, productCode: string) {
        const product = await this.findTrackingProductByCode(userId, productCode);
        await this.trackingProductRepository.remove(product);
    }

    async findTrackingProductByCode(userId: string, productCode: string) {
        const existProduct = await this.productRepository.findOne({
            where: { productCode: productCode },
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
        return trackingProduct;
    }

    async mongo(productPriceDto: ProductPriceDto) {
        const newData = new this.productPriceModel(productPriceDto);
        return newData.save();
    }

    async getPriceData(productId: string, days: number): Promise<PriceDataDto[]> {
        const endDate = new Date(+new Date() + KR_OFFSET);
        const startDate = new Date(endDate);
        startDate.setDate(endDate.getDate() - days);
        const dataInfo = await this.productPriceModel
            .find({
                productId: productId,
                time: {
                    $gte: startDate,
                    $lte: endDate,
                },
            })
            .exec();
        return dataInfo.map(({ time, price, isSoldOut }) => {
            return { time: new Date(time).getTime(), price, isSoldOut };
        });
    }
    @Cron('* */10 * * * *')
    async cyclicPriceChecker() {
        const productList = await this.productRepository.find({ select: { id: true, productCode: true } });
        const productCodeList = productList.map(({ productCode, id }) => getProductInfo11st(productCode, id));
        const results = (await Promise.all(productCodeList)).map(({ productId, productPrice, isSoldOut }) => {
            return { productId, price: productPrice, isSoldOut };
        });
        const updatedDataInfo = results.filter(({ productId, price, isSoldOut }) => {
            const cache = this.productDataCache.get(productId);
            if (!cache || cache.isSoldOut !== isSoldOut || cache.price !== price) {
                const lowestPrice = cache ? Math.min(cache.lowestPrice, price) : price;
                this.productDataCache.set(productId, {
                    isSoldOut,
                    price,
                    lowestPrice,
                });
                return true;
            }
            return false;
        });
        await this.productPriceModel.insertMany(updatedDataInfo);
    }
}
