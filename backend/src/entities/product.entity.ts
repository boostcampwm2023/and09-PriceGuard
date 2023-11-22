import { BaseEntity, Column, Entity, OneToMany, PrimaryColumn } from 'typeorm';
import { TrackingProduct } from './trackingProduct.entity';

@Entity()
export class Product extends BaseEntity {
    @PrimaryColumn({ type: 'char', length: 36 })
    id: string;

    @Column({ type: 'varchar', length: 255 })
    productName: string;

    @Column({ type: 'varchar', length: 16 })
    productCode: string;

    @Column({ type: 'varchar', length: 16 })
    shop: string;

    @Column({ type: 'varchar', length: 255 })
    shopUrl: string;

    @Column({ type: 'varchar', length: 255 })
    imageUrl: string;

    @OneToMany(() => TrackingProduct, (trackingProduct) => trackingProduct.productId)
    trackingProduct: TrackingProduct[];
}
