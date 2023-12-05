import { Entity, BaseEntity, PrimaryColumn, Column } from 'typeorm';

@Entity({ name: 'firebase' })
export class FirebaseToken extends BaseEntity {
    @PrimaryColumn('uuid')
    userId: string;

    @Column({ type: 'varchar', length: 256 })
    token: string;
}
