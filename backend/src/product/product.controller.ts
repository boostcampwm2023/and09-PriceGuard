import { Controller, Get, Post, Body, Patch, Param, Delete } from '@nestjs/common';
import { ProductService } from './product.service';
import { ProductUrlDto } from '../dto/product.url.dto';
import { UpdateProductDto } from '../dto/update.product.dto';
import { AddProductDto } from 'src/dto/add.product.dto';
import {
    ApiBadRequestResponse,
    ApiBearerAuth,
    ApiBody,
    ApiOkResponse,
    ApiOperation,
    ApiTags,
    ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { VerifyUrlSuccess, UrlError, UnauthorizedRequest, AddProductError } from 'src/dto/product.swagger.dto';

@ApiBearerAuth()
@ApiTags('상품 API')
@ApiUnauthorizedResponse({ type: UnauthorizedRequest, description: '승인되지 않은 요청' })
@Controller('product')
export class ProductController {
    constructor(private readonly productService: ProductService) {}

    @ApiOperation({ summary: '상품 URL 검증 API', description: '상품 URL을 검증한다' })
    @ApiBody({ type: ProductUrlDto })
    @ApiOkResponse({ type: VerifyUrlSuccess, description: '상품 URL 검증 성공' })
    @ApiBadRequestResponse({ type: UrlError, description: '유효하지 않은 링크' })
    @Post('/verify')
    verifyUrl(@Body() productUrlDto: ProductUrlDto) {
        return this.productService.verifyUrl(productUrlDto);
    }

    @ApiOperation({ summary: '상품 추가 API', description: '상품을 추가한다' })
    @ApiBody({ type: AddProductDto })
    @ApiOkResponse({ type: VerifyUrlSuccess, description: '상품 추가 성공' })
    @ApiBadRequestResponse({ type: AddProductError, description: '잘못된 요청' })
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
