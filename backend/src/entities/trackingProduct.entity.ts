import { BaseEntity, Column, Entity, ManyToOne, PrimaryColumn } from 'typeorm';
import { User } from './user.entity';
import { Product } from './product.entity';

@Entity()
export class TrackingProduct extends BaseEntity {
    @PrimaryColumn({ type: 'char', length: 36 })
    userId: string;

    @PrimaryColumn({ type: 'varchar', length: 36 })
    productId: string;

    @Column({ type: 'int' })
    targetPrice: number;

    @ManyToOne(() => User, (user) => user.id)
    user: User;

    @ManyToOne(() => Product, (product) => product.id)
    product: Product;
}
