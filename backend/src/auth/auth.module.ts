import { Module, forwardRef } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UsersModule } from 'src/user/user.module';
import { JwtModule } from '@nestjs/jwt';

@Module({
    imports: [forwardRef(() => UsersModule), JwtModule.register({})],
    providers: [AuthService],
    exports: [AuthService],
})
export class AuthModule {}
