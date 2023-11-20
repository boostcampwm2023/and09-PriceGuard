import { Module, forwardRef } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UsersModule } from 'src/user/user.module';
import { JwtModule } from '@nestjs/jwt';
import { AccessJwtStrategy } from './jwt/jwt.strategy';

@Module({
    imports: [forwardRef(() => UsersModule), JwtModule.register({})],
    providers: [AuthService, AccessJwtStrategy],
    exports: [AuthService],
})
export class AuthModule {}
