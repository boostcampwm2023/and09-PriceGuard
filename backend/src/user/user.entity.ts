import { Entity, PrimaryGeneratedColumn, Column, BaseEntity, Unique } from 'typeorm';
import { IsNotEmpty } from 'class-validator';

@Entity()
@Unique(['email'])
export class User extends BaseEntity {
    @PrimaryGeneratedColumn('uuid')
    id: number;

    @Column()
    @IsNotEmpty()
    email: string;

    @Column()
    @IsNotEmpty()
    userName: string;

    @Column()
    @IsNotEmpty()
    password: string;
}
