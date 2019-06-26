import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Udf } from 'app/shared/model/udf.model';
import { UdfService } from './udf.service';
import { UdfComponent } from './udf.component';
import { UdfDetailComponent } from './udf-detail.component';
import { UdfUpdateComponent } from './udf-update.component';
import { UdfDeletePopupComponent } from './udf-delete-dialog.component';
import { IUdf } from 'app/shared/model/udf.model';
import {BusinessResolve} from "app/home";

@Injectable({ providedIn: 'root' })
export class UdfResolve implements Resolve<IUdf> {
  constructor(private service: UdfService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IUdf> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Udf>) => response.ok),
        map((udf: HttpResponse<Udf>) => udf.body)
      );
    }
    return of(new Udf());
  }
}

export const udfRoute: Routes = [
  {
    path: ':id',
    component: UdfComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'alchemyApp.udf.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: UdfDetailComponent,
    resolve: {
      udf: UdfResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.udf.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/new',
    component: UdfUpdateComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.udf.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: UdfUpdateComponent,
    resolve: {
      udf: UdfResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.udf.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const udfPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: UdfDeletePopupComponent,
    resolve: {
      udf: UdfResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.udf.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
