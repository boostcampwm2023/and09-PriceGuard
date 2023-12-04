import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { FirebaseToken } from 'src/entities/firebase.token.entity';

@Injectable()
export class FirebaseRepository extends Repository<FirebaseToken> {
    constructor(
        @InjectRepository(FirebaseToken)
        private repository: Repository<FirebaseToken>,
    ) {
        super(repository.target, repository.manager, repository.queryRunner);
    }

    async saveToken(userId: string, token: string): Promise<string> {
        const newToken = FirebaseToken.create({ userId, token });
        await newToken.save();
        return newToken.token;
    }
}
