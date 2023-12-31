import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FavoriteFormService, FavoriteFormGroup } from './favorite-form.service';
import { IFavorite } from '../favorite.model';
import { FavoriteService } from '../service/favorite.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-favorite-update',
  templateUrl: './favorite-update.component.html',
})
export class FavoriteUpdateComponent implements OnInit {
  isSaving = false;
  favorite: IFavorite | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: FavoriteFormGroup = this.favoriteFormService.createFavoriteFormGroup();

  constructor(
    protected favoriteService: FavoriteService,
    protected favoriteFormService: FavoriteFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ favorite }) => {
      this.favorite = favorite;
      if (favorite) {
        this.updateForm(favorite);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const favorite = this.favoriteFormService.getFavorite(this.editForm);
    if (favorite.id !== null) {
      this.subscribeToSaveResponse(this.favoriteService.update(favorite));
    } else {
      this.subscribeToSaveResponse(this.favoriteService.create(favorite));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFavorite>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(favorite: IFavorite): void {
    this.favorite = favorite;
    this.favoriteFormService.resetForm(this.editForm, favorite);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, favorite.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.favorite?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
