import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Token } from 'src/entities/token.entity';

@Injectable()
export class JWTRepository extends Repository<Token> {
    constructor(
        @InjectRepository(Token)
        private repository: Repository<Token>,
    ) {
        super(repository.target, repository.manager, repository.queryRunner);
    }

    async saveToken(userId: string, token: string): Promise<string> {
        const newToken = Token.create({ userId, token });
        await newToken.save();
        return newToken.token;
    }
}
