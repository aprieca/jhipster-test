export interface ICategory {
  id: number;
  name?: string | null;
  description?: string | null;
  image?: string | null;
  imageContentType?: string | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
