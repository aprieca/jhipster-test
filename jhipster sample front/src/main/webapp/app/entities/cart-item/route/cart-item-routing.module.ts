import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CartItemComponent } from '../list/cart-item.component';
import { CartItemDetailComponent } from '../detail/cart-item-detail.component';
import { CartItemUpdateComponent } from '../update/cart-item-update.component';
import { CartItemRoutingResolveService } from './cart-item-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const cartItemRoute: Routes = [
  {
    path: '',
    component: CartItemComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CartItemDetailComponent,
    resolve: {
      cartItem: CartItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CartItemUpdateComponent,
    resolve: {
      cartItem: CartItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CartItemUpdateComponent,
    resolve: {
      cartItem: CartItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cartItemRoute)],
  exports: [RouterModule],
})
export class CartItemRoutingModule {}
