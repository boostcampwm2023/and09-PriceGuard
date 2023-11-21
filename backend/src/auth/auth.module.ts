import { Module, forwardRef } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UsersModule } from 'src/user/user.module';
import { JwtModule } from '@nestjs/jwt';
import { AccessJwtStrategy, RefreshJwtStrategy } from './jwt/jwt.strategy';
import { AuthController } from './auth.controller';
import { JWTRepository } from './jwt/jwt.repository';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Token } from 'src/entities/token.entity';
import { JWTService } from './jwt/jwt.service';

@Module({
    imports: [TypeOrmModule.forFeature([Token]), forwardRef(() => UsersModule), JwtModule.register({})],
    controllers: [AuthController],
    providers: [AuthService, JWTService, JWTRepository, AccessJwtStrategy, RefreshJwtStrategy],
    exports: [AuthService],
})
export class AuthModule {}
