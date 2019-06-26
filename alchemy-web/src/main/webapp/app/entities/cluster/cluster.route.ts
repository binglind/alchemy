import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Cluster } from 'app/shared/model/cluster.model';
import { ClusterService } from './cluster.service';
import { ClusterComponent } from './cluster.component';
import { ClusterDetailComponent } from './cluster-detail.component';
import { ClusterUpdateComponent } from './cluster-update.component';
import { ClusterDeletePopupComponent } from './cluster-delete-dialog.component';
import { ICluster } from 'app/shared/model/cluster.model';
import {BusinessResolve} from "app/home";

@Injectable({ providedIn: 'root' })
export class ClusterResolve implements Resolve<ICluster> {
  constructor(private service: ClusterService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICluster> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Cluster>) => response.ok),
        map((cluster: HttpResponse<Cluster>) => cluster.body)
      );
    }
    return of(new Cluster());
  }
}

export const clusterRoute: Routes = [
  {
    path: ':id',
    component: ClusterComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.cluster.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ClusterDetailComponent,
    resolve: {
      cluster: ClusterResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.cluster.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/new',
    component: ClusterUpdateComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.cluster.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ClusterUpdateComponent,
    resolve: {
      cluster: ClusterResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.cluster.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const clusterPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ClusterDeletePopupComponent,
    resolve: {
      cluster: ClusterResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.cluster.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
