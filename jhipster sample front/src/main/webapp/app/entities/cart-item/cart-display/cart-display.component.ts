import { Component, OnInit } from '@angular/core';
import { CartItemService } from '../service/cart-item.service';
import { CookieService } from 'ngx-cookie-service';
import { ICartItem } from '../cart-item.model';

@Component({
  selector: 'jhi-cart-display',
  templateUrl: './cart-display.component.html',
  styleUrls: ['./cart-display.component.scss']
})
export class CartDisplayComponent implements OnInit {

  userId?: number;
  cartItems?: ICartItem[]
  prices:number[] = [];
  totalPrice:number=0;

  constructor(private cartItemService:CartItemService,private cookieService:CookieService) { }

  ngOnInit(): void {
    this.userId =  JSON.parse(this.cookieService.get("user"))
    if(this.userId){
      this.getUserCart();
    }
  }

getUserCart(){
  this.cartItemService.query({'userId.equals':this.userId}).subscribe({
    next: (items) => {
      this.cartItems = items.body!
      this.prices = [];
      this.cartItems.map(item=>this.prices.push(item.price!*item.quantity!));
      this.calculatePrice();
    },
    error:(err)=>console.log(err)
  })
}

calculatePrice():void{
  for (let price of this.prices ){
    this.totalPrice += price;
  }
}
}
