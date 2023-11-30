import { PriceDataDto } from './price.data.dto';

export class ProductDetailsDto {
    productName: string;
    shop: string;
    imageUrl: string;
    rank: number;
    shopUrl: string;
    targetPrice: number;
    lowestPrice: number;
    price: number;
    priceData: PriceDataDto[];
}
