import { Prop, Schema, SchemaFactory } from "@nestjs/mongoose";
import mongoose, { HydratedDocument } from "mongoose";

export type CatDocument = HydratedDocument<ProductPrice>;

@Schema()
export class ProductPrice {
  @Prop()
  productId: string;

  @Prop({ default: new Date(), type: mongoose.Schema.Types.Date })
  time: string;

  @Prop()
  price: number;

  @Prop()
  isSoldOut: boolean;
}

export const ProductPriceSchema = SchemaFactory.createForClass(ProductPrice);