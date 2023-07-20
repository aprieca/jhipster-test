import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICartItem } from '../cart-item.model';
import { CartItemService } from '../service/cart-item.service';

@Injectable({ providedIn: 'root' })
export class CartItemRoutingResolveService implements Resolve<ICartItem | null> {
  constructor(protected service: CartItemService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICartItem | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cartItem: HttpResponse<ICartItem>) => {
          if (cartItem.body) {
            return of(cartItem.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
