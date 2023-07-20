import { IItem, NewItem } from './item.model';

export const sampleWithRequiredData: IItem = {
  id: 89800,
  name: 'Bedfordshire input',
  price: 45749,
};

export const sampleWithPartialData: IItem = {
  id: 90297,
  name: 'Papelería grow',
  price: 9606,
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  imageBg: '../fake-data/blob/hipster.png',
  imageBgContentType: 'unknown',
  stock: 23302,
  discount: 64739,
};

export const sampleWithFullData: IItem = {
  id: 66899,
  name: 'basado next-generation Sabroso',
  description: 'web-enabled Algodón',
  price: 59519,
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  imageBg: '../fake-data/blob/hipster.png',
  imageBgContentType: 'unknown',
  stock: 68348,
  discount: 57081,
};

export const sampleWithNewData: NewItem = {
  name: 'móbil enterprise',
  price: 42231,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
