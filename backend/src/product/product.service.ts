import { Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { UpdateProductDto } from '../dto/update.product.dto';
import { AddProductDto } from '../dto/add.product.dto';

@Injectable()
export class ProductService {
    verifyUrl(productUrlDto: ProductUrlDto) {
        console.log(productUrlDto);
    }

    addProduct(addProductDto: AddProductDto) {
        console.log(addProductDto);
    }

    getTrackingList() {}

    getRecommendList() {}

    getProductDetails(productCode: string) {
        console.log(productCode);
    }

    updateTargetPrice(updateProductDto: UpdateProductDto) {
        console.log(updateProductDto);
    }

    deleteProduct(productCode: string) {
        console.log(productCode);
    }
}
