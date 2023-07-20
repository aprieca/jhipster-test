import { ICategory } from 'app/entities/category/category.model';

export interface IItem {
  id: number;
  name?: string | null;
  description?: string | null;
  price?: number | null;
  image?: string | null;
  imageContentType?: string | null;
  imageBg?: string | null;
  imageBgContentType?: string | null;
  stock?: number | null;
  discount?: number | null;
  category?: Pick<ICategory, 'id'> | null;
  //categoryName?: Pick<ICategory, 'name'> | null;
}

export type NewItem = Omit<IItem, 'id'> & { id: null };
