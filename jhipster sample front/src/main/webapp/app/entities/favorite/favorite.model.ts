import { IUser } from 'app/entities/user/user.model';

export interface IFavorite {
  id: number;
  itemId?: number | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewFavorite = Omit<IFavorite, 'id'> & { id: null };
