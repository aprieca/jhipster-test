import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 2529,
  name: 'Namibia Salchichas',
};

export const sampleWithPartialData: ICategory = {
  id: 68611,
  name: 'navigate Morado Facilitador',
  description: 'virtual Silla navigating',
};

export const sampleWithFullData: ICategory = {
  id: 54514,
  name: 'bus',
  description: 'Cuentas Ordenador a',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithNewData: NewCategory = {
  name: 'generating',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
