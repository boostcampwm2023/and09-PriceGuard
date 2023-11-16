import { Module, forwardRef } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UsersModule } from 'src/user/user.module';

@Module({
    imports: [forwardRef(() => UsersModule)],
    providers: [AuthService],
    exports: [AuthService],
})
export class AuthModule {}
