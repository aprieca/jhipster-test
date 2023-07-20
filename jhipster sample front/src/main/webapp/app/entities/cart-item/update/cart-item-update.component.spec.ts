import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CartItemFormService } from './cart-item-form.service';
import { CartItemService } from '../service/cart-item.service';
import { ICartItem } from '../cart-item.model';
import { IItem } from 'app/entities/item/item.model';
import { ItemService } from 'app/entities/item/service/item.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { CartItemUpdateComponent } from './cart-item-update.component';

describe('CartItem Management Update Component', () => {
  let comp: CartItemUpdateComponent;
  let fixture: ComponentFixture<CartItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cartItemFormService: CartItemFormService;
  let cartItemService: CartItemService;
  let itemService: ItemService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CartItemUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CartItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CartItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cartItemFormService = TestBed.inject(CartItemFormService);
    cartItemService = TestBed.inject(CartItemService);
    itemService = TestBed.inject(ItemService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Item query and add missing value', () => {
      const cartItem: ICartItem = { id: 456 };
      const item: IItem = { id: 6517 };
      cartItem.item = item;

      const itemCollection: IItem[] = [{ id: 89410 }];
      jest.spyOn(itemService, 'query').mockReturnValue(of(new HttpResponse({ body: itemCollection })));
      const additionalItems = [item];
      const expectedCollection: IItem[] = [...additionalItems, ...itemCollection];
      jest.spyOn(itemService, 'addItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      expect(itemService.query).toHaveBeenCalled();
      expect(itemService.addItemToCollectionIfMissing).toHaveBeenCalledWith(
        itemCollection,
        ...additionalItems.map(expect.objectContaining)
      );
      expect(comp.itemsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const cartItem: ICartItem = { id: 456 };
      const user: IUser = { id: 25378 };
      cartItem.user = user;

      const userCollection: IUser[] = [{ id: 75714 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const cartItem: ICartItem = { id: 456 };
      const item: IItem = { id: 83645 };
      cartItem.item = item;
      const user: IUser = { id: 89023 };
      cartItem.user = user;

      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      expect(comp.itemsSharedCollection).toContain(item);
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.cartItem).toEqual(cartItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICartItem>>();
      const cartItem = { id: 123 };
      jest.spyOn(cartItemFormService, 'getCartItem').mockReturnValue(cartItem);
      jest.spyOn(cartItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cartItem }));
      saveSubject.complete();

      // THEN
      expect(cartItemFormService.getCartItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cartItemService.update).toHaveBeenCalledWith(expect.objectContaining(cartItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICartItem>>();
      const cartItem = { id: 123 };
      jest.spyOn(cartItemFormService, 'getCartItem').mockReturnValue({ id: null });
      jest.spyOn(cartItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cartItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cartItem }));
      saveSubject.complete();

      // THEN
      expect(cartItemFormService.getCartItem).toHaveBeenCalled();
      expect(cartItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICartItem>>();
      const cartItem = { id: 123 };
      jest.spyOn(cartItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cartItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareItem', () => {
      it('Should forward to itemService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(itemService, 'compareItem');
        comp.compareItem(entity, entity2);
        expect(itemService.compareItem).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
