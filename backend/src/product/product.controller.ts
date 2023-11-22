import {
    Controller,
    Get,
    Post,
    Body,
    Patch,
    Param,
    Delete,
    Req,
    UseGuards,
    HttpStatus,
    UseFilters,
} from '@nestjs/common';
import { ProductService } from './product.service';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductDto } from 'src/dto/product.dto';
import {
    ApiBadRequestResponse,
    ApiBearerAuth,
    ApiBody,
    ApiHeader,
    ApiNotFoundResponse,
    ApiOkResponse,
    ApiOperation,
    ApiTags,
    ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import {
    VerifyUrlSuccess,
    UrlError,
    UnauthorizedRequest,
    RequestError,
    ProductNotFound,
    ProductDetailsNotFound,
    GetTrackingListSuccess,
    GetRecommendListSuccess,
    UpdateTargetPriceSuccess,
    DeleteProductSuccess,
} from 'src/dto/product.swagger.dto';
import { User } from 'src/entities/user.entity';
import { AuthGuard } from '@nestjs/passport';
import { HttpExceptionFilter } from 'src/exceptions/http.exception.filter';

@ApiBearerAuth()
@ApiHeader({
    name: 'Authorization',
    description: '사용자 인증을 위한 AccessToken이다. ex) Bearer [token]',
})
@ApiTags('상품 API')
@ApiUnauthorizedResponse({ type: UnauthorizedRequest, description: '승인되지 않은 요청' })
@Controller('product')
@UseFilters(HttpExceptionFilter)
@UseGuards(AuthGuard('access'))
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
    @ApiBody({ type: ProductDto })
    @ApiOkResponse({ type: VerifyUrlSuccess, description: '상품 추가 성공' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청' })
    @Post()
    addProduct(@Body() productDto: ProductDto) {
        return this.productService.addProduct(productDto);
    }

    @ApiOperation({ summary: '상품 목록 조회 API', description: '사용자가 추가한 상품 목록을 조회한다.' })
    @ApiOkResponse({ type: GetTrackingListSuccess, description: '상품 목록 조회 성공' })
    @ApiNotFoundResponse({ type: ProductNotFound, description: '추가한 상품이 없어서 상품 목록을 조회할 수 없습니다.' })
    @Get('/tracking')
    async getTrackingList(@Req() req: Request & { user: User }): Promise<GetTrackingListSuccess> {
        const trackingList = await this.productService.getTrackingList(req.user.id);
        return { statusCode: HttpStatus.OK, message: '상품 목록 조회 성공', trackingList: trackingList };
    }

    @ApiOperation({ summary: '추천 상품 목록 조회 API', description: '추천 상품 목록을 조회한다.' })
    @ApiOkResponse({ type: GetRecommendListSuccess, description: '추천 상품 목록 조회 성공' })
    @ApiNotFoundResponse({ type: ProductNotFound, description: '추천 상품 목록이 없습니다.' })
    @Get('/recommend')
    getRecommendList() {
        return this.productService.getRecommendList();
    }

    @ApiOperation({ summary: '상품 세부 정보 조회 API', description: '상품 세부 정보를 조회한다.' })
    @ApiOkResponse({ type: VerifyUrlSuccess, description: '상품 세부 정보 조회 성공' })
    @ApiNotFoundResponse({ type: ProductDetailsNotFound, description: '상품 정보가 존재하지 않습니다.' })
    @Get(':productCode')
    getProductDetails(@Param('productCode') productCode: string) {
        return this.productService.getProductDetails(productCode);
    }

    @ApiOperation({ summary: '상품 목표 가격 수정 API', description: '상품 목표 가격을 수정한다.' })
    @ApiOkResponse({ type: UpdateTargetPriceSuccess, description: '상품 목표 가격 수정 성공' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청' })
    @Patch('/targetPrice')
    updateTargetPrice(@Body() productDto: ProductDto) {
        return this.productService.updateTargetPrice(productDto);
    }
    @ApiOperation({ summary: '상품 삭제 API', description: '상품을 삭제한다.' })
    @ApiOkResponse({ type: DeleteProductSuccess, description: '상품 삭제 성공' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청' })
    @Delete(':productCode')
    deleteProduct(@Param('productCode') productCode: string) {
        return this.productService.deleteProduct(productCode);
    }
}
