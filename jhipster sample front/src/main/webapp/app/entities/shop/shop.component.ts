import { Component, OnInit } from '@angular/core';
import {ItemService } from '../item/service/item.service';
import { Observable, map, pipe } from 'rxjs';
import { IItem } from '../item/item.model';
import { IFavorite, NewFavorite } from '../favorite/favorite.model';
import { FavoriteService } from '../favorite/service/favorite.service';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'jhi-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.scss']
})
export class ShopComponent implements OnInit {

  items$?:Observable<IItem[]>;
  favorite? :NewFavorite;
  favorites? :IFavorite[] = []
  userId?:number

  constructor(private itemService:ItemService,private cookieService:CookieService, private favoriteService:FavoriteService) { }

  ngOnInit(): void {
    this.getItemsByCategory();
    this.userId =  JSON.parse(this.cookieService.get("user"))
    if(this.userId){
      this.getUserFavorites()
    }
    console.log(this.favorites)
  }

  getItemsByCategory(){
    this.items$ = this.itemService.query().pipe(
      map(result => result.body!)
    );
  }

  insertFavorite(itemId:number){
   this.favorite = {itemId:itemId,user:{id:this.userId!},id:null}
   this.favoriteService.create(this.favorite).subscribe({
    next: (favorite) =>{
      this.favorites!.push(favorite.body!)
      console.log("Favorite inserted"+favorite)
    } 
   })
   }

   getUserFavorites(){
      this.favoriteService.query({'userId.equals':this.userId}).subscribe({
        next: (response) =>{
          response.body!.map((favorite) => this.favorites?.push(favorite));
        }
      })
   }

   isFavorite(itemId:number): boolean {
    return <boolean>this.favorites?.some(favorite => favorite.itemId === itemId);
  }

  getFavoriteIdByItemId(itemId:number){
    let favorite = this.favorites?.find((favorite) => favorite.itemId === itemId);
    return favorite?.id;
  }

  deleteFavorite(favoriteId :number){
    console.log("ejecutando borrado")
    this.favoriteService.delete(favoriteId).subscribe({
      next:(favorite)=>{
        console.log("Favorito Borrado Correctamente")
      },
      error:(err) =>console.log(err)
    });
    let index = this.favorites?.findIndex(favorite => favorite.id === favoriteId)
    this.favorites?.splice(index!)
  }

  favoriteComposer(itemId: number){
    if(this.isFavorite(itemId)){
      console.log("el elemento era favorito")
      let favoriteId = this.getFavoriteIdByItemId(itemId)
      console.log(favoriteId)
      this.deleteFavorite(favoriteId!)
    }
    else{
      this.insertFavorite(itemId)
    }
  }
  }


