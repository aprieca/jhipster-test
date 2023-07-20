import { IItem } from 'app/entities/item/item.model';
import { IUser } from 'app/entities/user/user.model';

export interface ICartItem {
  id: number;
  name?: string | null;
  categoryName?: string | null;
  image?: string | null;
  imageContentType?: string | null;
  quantity?: number | null;
  price?: number | null;
  item?: Pick<IItem, 'id'> | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewCartItem = Omit<ICartItem, 'id'> & { id: null };
