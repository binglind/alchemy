import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Sink } from 'app/shared/model/sink.model';
import { SinkService } from './sink.service';
import { SinkComponent } from './sink.component';
import { SinkDetailComponent } from './sink-detail.component';
import { SinkUpdateComponent } from './sink-update.component';
import { SinkDeletePopupComponent } from './sink-delete-dialog.component';
import { ISink } from 'app/shared/model/sink.model';
import {BusinessResolve} from "app/home";

@Injectable({ providedIn: 'root' })
export class SinkResolve implements Resolve<ISink> {
  constructor(private service: SinkService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ISink> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Sink>) => response.ok),
        map((sink: HttpResponse<Sink>) => sink.body)
      );
    }
    return of(new Sink());
  }
}

export const sinkRoute: Routes = [
  {
    path: ':id',
    component: SinkComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'alchemyApp.sink.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SinkDetailComponent,
    resolve: {
      sink: SinkResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.sink.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/new',
    component: SinkUpdateComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.sink.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: SinkUpdateComponent,
    resolve: {
      sink: SinkResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.sink.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const sinkPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: SinkDeletePopupComponent,
    resolve: {
      sink: SinkResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.sink.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
