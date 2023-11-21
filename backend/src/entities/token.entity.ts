import { Entity, BaseEntity, PrimaryColumn, Column } from 'typeorm';

@Entity()
export class Token extends BaseEntity {
    @PrimaryColumn('uuid')
    userId: string;

    @Column({ type: 'varchar', length: 256 })
    token: string;
}
