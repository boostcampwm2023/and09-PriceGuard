import { Entity, PrimaryGeneratedColumn, Column, BaseEntity, Unique, OneToMany } from 'typeorm';
import { IsNotEmpty } from 'class-validator';
import { TrackingProduct } from './trackingProduct.entity';

@Entity()
@Unique(['email'])
export class User extends BaseEntity {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column()
    @IsNotEmpty()
    email: string;

    @Column()
    @IsNotEmpty()
    userName: string;

    @Column()
    @IsNotEmpty()
    password: string;

    @Column({ default: 'tier1', type: 'varchar', length: 36, nullable: false })
    grade: string;

    @Column({ default: false, type: 'boolean', nullable: false })
    verified: boolean;

    @OneToMany(() => TrackingProduct, (trackingProduct) => trackingProduct.userId)
    trackingProduct: TrackingProduct[];
}
