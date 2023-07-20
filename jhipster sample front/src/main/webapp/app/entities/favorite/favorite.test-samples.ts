import { IFavorite, NewFavorite } from './favorite.model';

export const sampleWithRequiredData: IFavorite = {
  id: 84905,
  itemId: 2360,
};

export const sampleWithPartialData: IFavorite = {
  id: 96517,
  itemId: 40068,
};

export const sampleWithFullData: IFavorite = {
  id: 5214,
  itemId: 66728,
};

export const sampleWithNewData: NewFavorite = {
  itemId: 383,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
