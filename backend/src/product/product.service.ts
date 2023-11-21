import { Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductDto } from '../dto/product.dto';

@Injectable()
export class ProductService {
    verifyUrl(productUrlDto: ProductUrlDto) {
        console.log(productUrlDto);
    }

    addProduct(productDto: ProductDto) {
        console.log(productDto);
    }

    getTrackingList() {}

    getRecommendList() {}

    getProductDetails(productCode: string) {
        console.log(productCode);
    }

    updateTargetPrice(productDto: ProductDto) {
        console.log(productDto);
    }

    deleteProduct(productCode: string) {
        console.log(productCode);
    }
}
