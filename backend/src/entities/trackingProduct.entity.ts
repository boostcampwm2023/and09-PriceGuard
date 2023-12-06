import { BaseEntity, Column, Entity, ManyToOne, PrimaryColumn } from 'typeorm';
import { User } from './user.entity';
import { Product } from './product.entity';

@Entity('tracking_product')
export class TrackingProduct extends BaseEntity {
    @PrimaryColumn({ type: 'char', length: 36, nullable: false })
    userId: string;

    @PrimaryColumn({ type: 'varchar', length: 36, nullable: false })
    productId: string;

    @Column({ type: 'int', nullable: false })
    targetPrice: number;

    @Column({ type: 'boolean', nullable: false })
    isFirst: boolean = true;

    @Column({ type: 'boolean', nullable: false })
    isAlert: boolean = true;

    @ManyToOne(() => User, (user) => user.id)
    user: User;

    @ManyToOne(() => Product, (product) => product.id)
    product: Product;
}
