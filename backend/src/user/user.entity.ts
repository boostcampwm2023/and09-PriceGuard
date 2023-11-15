import { Entity, PrimaryGeneratedColumn, Column, BaseEntity, Unique } from 'typeorm';

@Entity()
@Unique(['email'])
export class User extends BaseEntity {
    @PrimaryGeneratedColumn('uuid')
    id: number;

    @Column()
    email: string;

    @Column()
    username: string;

    @Column()
    password: string;
}
