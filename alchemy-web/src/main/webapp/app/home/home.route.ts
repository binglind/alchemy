import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Business } from 'app/shared/model/business.model';
import { BusinessService } from './business.service';
import { HomeComponent } from './home.component';
import { BusinessDetailComponent } from './business-detail.component';
import { BusinessUpdateComponent } from './business-update.component';
import { BusinessDeletePopupComponent } from './business-delete-dialog.component';
import { IBusiness } from 'app/shared/model/business.model';

@Injectable({ providedIn: 'root' })
export class BusinessResolve implements Resolve<IBusiness> {
  constructor(private service: BusinessService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IBusiness> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Business>) => response.ok),
        map((business: HttpResponse<Business>) => business.body)
      );
    }
    return of(new Business());
  }
}

export const businessRoute: Routes = [
  {
    path: '',
    component: HomeComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.business.home.title'
    },
    // canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: BusinessDetailComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.business.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: BusinessUpdateComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.business.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: BusinessUpdateComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.business.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const businessPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: BusinessDeletePopupComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.business.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
