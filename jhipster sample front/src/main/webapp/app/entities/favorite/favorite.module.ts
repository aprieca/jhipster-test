import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FavoriteComponent } from './list/favorite.component';
import { FavoriteDetailComponent } from './detail/favorite-detail.component';
import { FavoriteUpdateComponent } from './update/favorite-update.component';
import { FavoriteDeleteDialogComponent } from './delete/favorite-delete-dialog.component';
import { FavoriteRoutingModule } from './route/favorite-routing.module';
import { DisplayComponent } from './display/display.component';

@NgModule({
  imports: [SharedModule, FavoriteRoutingModule],
  declarations: [FavoriteComponent, FavoriteDetailComponent, FavoriteUpdateComponent, FavoriteDeleteDialogComponent, DisplayComponent],
})
export class FavoriteModule {}
