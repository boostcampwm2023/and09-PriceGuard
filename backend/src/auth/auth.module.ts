import { Module, forwardRef } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UsersModule } from 'src/user/user.module';
import { JwtModule } from '@nestjs/jwt';
import { AccessJwtStrategy, RefreshJwtStrategy } from './jwt/jwt.strategy';
import { AuthController } from './auth.controller';

@Module({
    imports: [forwardRef(() => UsersModule), JwtModule.register({})],
    controllers: [AuthController],
    providers: [AuthService, AccessJwtStrategy, RefreshJwtStrategy],
    exports: [AuthService],
})
export class AuthModule {}
