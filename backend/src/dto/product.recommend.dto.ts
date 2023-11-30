import { PriceDataDto } from './price.data.dto';

export class RecommendProductDto {
    productName: string;
    productCode: string;
    shop: string;
    imageUrl: string;
    price: number;
    priceData: PriceDataDto[];
}
