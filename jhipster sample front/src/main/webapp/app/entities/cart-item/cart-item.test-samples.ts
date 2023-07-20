import { ICartItem, NewCartItem } from './cart-item.model';

export const sampleWithRequiredData: ICartItem = {
  id: 44067,
  name: 'Muelle',
  categoryName: 'auxiliary',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  quantity: 42715,
  price: 23521,
};

export const sampleWithPartialData: ICartItem = {
  id: 34491,
  name: 'Canarias multi-byte Identidad',
  categoryName: 'Loan Panamá Peso',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  quantity: 40089,
  price: 49437,
};

export const sampleWithFullData: ICartItem = {
  id: 15105,
  name: 'virtual',
  categoryName: 'Asturias',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  quantity: 61797,
  price: 28714,
};

export const sampleWithNewData: NewCartItem = {
  name: 'Rua Informática',
  categoryName: 'Juguetería Polígono',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  quantity: 85447,
  price: 8223,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
