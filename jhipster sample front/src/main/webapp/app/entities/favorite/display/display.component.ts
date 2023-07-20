import { Component, OnInit } from '@angular/core';
import { FavoriteService } from '../service/favorite.service';
import { IFavorite } from '../favorite.model';
import { CookieService } from 'ngx-cookie-service';
import { IItem } from 'app/entities/item/item.model';
import { Observable, map } from 'rxjs';
import { ItemService } from 'app/entities/item/service/item.service';


@Component({
  selector: 'jhi-display',
  templateUrl: './display.component.html',
  styleUrls: ['./display.component.scss']
})
export class DisplayComponent implements OnInit {

  favorites? :IFavorite[] = []
  userId?:number
  itemIds?: number[] = [];
  items$!: Observable<IItem[]>;

  constructor(private favoriteService:FavoriteService, private cookieService:CookieService,private itemService:ItemService) { }

  ngOnInit(): void {
    this.userId =  JSON.parse(this.cookieService.get("user"))
    if(this.userId){
      this.getUserFavorites()
    }
  }

  getUserFavorites(){
    this.favoriteService.query({'userId.equals':this.userId}).subscribe({
      next: (response) =>{
        response.body!.map((favorite) => this.favorites?.push(favorite));
        response.body!.map((favorite) => this.itemIds?.push(favorite.itemId!));
        console.log(this.itemIds)
        if(this.itemIds){
          console.log("llega")
          this.loadItemsBatch(this.itemIds)
          console.log(this.items$)
        }
      },
      error: (error) => {
        console.log(error)
      }
    });
 }

 loadItemsBatch(itemIds:number[]){
  console.log("Ha entrado aqui")
  this.items$ = this.itemService.batch(itemIds).pipe(
    map(response => response.body!)
  )
 }

 prepareFavoriteToDelete(itemId: number) {
  const favoriteId = this.getFavoriteIdByItemId(itemId);
  if (favoriteId) {
    this.deleteFavorite(favoriteId, itemId);
  }
}

deleteFavorite(favoriteId: number, itemId: number) {
  this.favoriteService.delete(favoriteId).subscribe({
    next: (favorite) => {
      console.log("Favorito Eliminado");
      this.removeItemFromList(itemId)
    },
    error: (err) => {
      console.log("Error deleting favorite" + err);
    }
  });
}

 removeItemFromList(itemId: number) {
  this.items$ = this.items$.pipe(
    map(items => items.filter(item => item.id !== itemId)))
}

getFavoriteIdByItemId(itemId: number): number | undefined {
  let favorite = this.favorites?.find(favorite => favorite.itemId === itemId);
  return favorite?.id;
}

}
