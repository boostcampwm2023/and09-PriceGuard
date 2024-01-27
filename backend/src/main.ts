import { NestFactory } from '@nestjs/core';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { GlobalValidationPipe } from './exceptions/validation.pipe';
import { VersioningType } from '@nestjs/common';

async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    app.useGlobalPipes(new GlobalValidationPipe());
    app.enableVersioning({
        type: VersioningType.URI,
    });
    const config = new DocumentBuilder()
        .setTitle('PriceGuard')
        .setDescription('PriceGuard API 문서')
        .setVersion('1.0.0')
        .build();
    const document = SwaggerModule.createDocument(app, config);
    SwaggerModule.setup('docs', app, document);
    await app.listen(3000);
}
bootstrap();
