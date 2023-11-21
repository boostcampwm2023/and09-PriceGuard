import { Controller, Get, Post, Body, Patch, Param, Delete } from '@nestjs/common';
import { ProductService } from './product.service';
import { ProductUrlDto } from '../dto/product.url.dto';
import { UpdateProductDto } from '../dto/update.product.dto';
import { AddProductDto } from 'src/dto/add.product.dto';

@Controller('product')
export class ProductController {
    constructor(private readonly productService: ProductService) {}

    @Post()
    verifyUrl(@Body() productUrlDto: ProductUrlDto) {
        return this.productService.verifyUrl(productUrlDto);
    }

    @Post()
    addProduct(@Body() addProductDto: AddProductDto) {
        return this.productService.addProduct(addProductDto);
    }

    @Get('/tracking')
    getTrackingList() {
        return this.productService.getTrackingList();
    }

    @Get('/recommend')
    getRecommendList() {
        return this.productService.getRecommendList();
    }

    @Get(':productCode')
    getProductDetails(@Param('productCode') productCode: string) {
        return this.productService.getProductDetails(productCode);
    }

    @Patch('/targetPrice')
    updateTargetPrice(@Body() updateProductDto: UpdateProductDto) {
        return this.productService.updateTargetPrice(updateProductDto);
    }

    @Delete(':productCode')
    deleteProduct(@Param('productCode') productCode: string) {
        return this.productService.deleteProduct(productCode);
    }
}
