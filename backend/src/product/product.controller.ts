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
import { ProductAddDto } from 'src/dto/product.add.dto';
import {
    ApiBadRequestResponse,
    ApiBearerAuth,
    ApiBody,
    ApiConflictResponse,
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
    ProductCodeError,
    TrackingProductsNotFound,
    AddProductConflict,
    ProductDetailsSuccess,
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
    async verifyUrl(@Body() productUrlDto: ProductUrlDto): Promise<VerifyUrlSuccess> {
        const { productName, productCode, productPrice, shop, imageUrl } =
            await this.productService.verifyUrl(productUrlDto);
        return {
            statusCode: HttpStatus.OK,
            message: '상품 URL 검증 성공',
            productCode,
            productName,
            productPrice,
            shop,
            imageUrl,
        };
    }

    @ApiOperation({ summary: '상품 추가 API', description: '상품을 추가한다' })
    @ApiBody({ type: ProductAddDto })
    @ApiOkResponse({ type: VerifyUrlSuccess, description: '상품 추가 성공' })
    @ApiNotFoundResponse({ type: ProductCodeError, description: '상품 추가 실패' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @ApiConflictResponse({ type: AddProductConflict, description: '이미 등록된 상품 존재' })
    @Post()
    async addProduct(@Req() req: Request & { user: User }, @Body() productAddDto: ProductAddDto) {
        await this.productService.addProduct(req.user.id, productAddDto);
        return { statusCode: HttpStatus.OK, message: '상품 추가 성공' };
    }

    @ApiOperation({ summary: '트래킹 상품 목록 조회 API', description: '사용자가 추가한 상품 목록을 조회한다.' })
    @ApiOkResponse({ type: GetTrackingListSuccess, description: '상품 목록 조회 성공' })
    @ApiNotFoundResponse({ type: ProductNotFound, description: '추가한 상품이 없어서 상품 목록을 조회할 수 없습니다.' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @Get('/tracking')
    async getTrackingList(@Req() req: Request & { user: User }): Promise<GetTrackingListSuccess> {
        const trackingList = await this.productService.getTrackingList(req.user.id);
        return { statusCode: HttpStatus.OK, message: '상품 목록 조회 성공', trackingList: trackingList };
    }

    @ApiOperation({ summary: '추천 상품 목록 조회 API', description: '추천 상품 목록을 조회한다.' })
    @ApiOkResponse({ type: GetRecommendListSuccess, description: '추천 상품 목록 조회 성공' })
    @ApiNotFoundResponse({ type: ProductNotFound, description: '추천 상품 목록이 없습니다.' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @Get('/recommend')
    getRecommendList() {
        return this.productService.getRecommendList();
    }

    @ApiOperation({ summary: '상품 세부 정보 조회 API', description: '상품 세부 정보를 조회한다.' })
    @ApiOkResponse({ type: ProductDetailsSuccess, description: '상품 세부 정보 조회 성공' })
    @ApiNotFoundResponse({ type: ProductDetailsNotFound, description: '상품 정보가 존재하지 않습니다.' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @Get(':productCode')
    async getProductDetails(@Req() req: Request & { user: User }, @Param('productCode') productCode: string) {
        const { productName, shop, imageUrl, rank, shopUrl, targetPrice, price } =
            await this.productService.getProductDetails(req.user.id, productCode);
        return {
            statusCode: HttpStatus.OK,
            message: '상품 URL 검증 성공',
            productName,
            productCode,
            shop,
            imageUrl,
            rank,
            shopUrl,
            targetPrice,
            price,
        };
    }

    @ApiOperation({ summary: '상품 목표 가격 수정 API', description: '상품 목표 가격을 수정한다.' })
    @ApiOkResponse({ type: UpdateTargetPriceSuccess, description: '상품 목표 가격 수정 성공' })
    @ApiNotFoundResponse({ type: TrackingProductsNotFound, description: '추적 상품 찾을 수 없음' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @Patch('/targetPrice')
    async updateTargetPrice(@Req() req: Request & { user: User }, @Body() productAddDto: ProductAddDto) {
        await this.productService.updateTargetPrice(req.user.id, productAddDto);
        return { statusCode: HttpStatus.OK, message: '목표 가격 수정 성공' };
    }

    @ApiOperation({ summary: '추적 상품 삭제 API', description: '추적 상품을 삭제한다.' })
    @ApiOkResponse({ type: DeleteProductSuccess, description: '추적 상품 삭제 성공' })
    @ApiNotFoundResponse({ type: TrackingProductsNotFound, description: '추적 상품 찾을 수 없음' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @Delete(':productCode')
    async deleteProduct(@Req() req: Request & { user: User }, @Param('productCode') productCode: string) {
        await this.productService.deleteProduct(req.user.id, productCode);
        return { statusCode: HttpStatus.OK, message: '추적 상품 삭제 성공' };
    }
}
