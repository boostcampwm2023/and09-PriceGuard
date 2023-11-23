import { Entity, BaseEntity, PrimaryColumn, Column } from 'typeorm';

@Entity({ name: 'refresh_whitelist' })
export class Token extends BaseEntity {
    @PrimaryColumn('uuid')
    userId: string;

    @Column({ type: 'varchar', length: 256 })
    token: string;
}
