import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Job } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import { JobComponent } from './job.component';
import { JobDetailComponent } from './job-detail.component';
import { JobUpdateComponent } from './job-update.component';
import { JobDeletePopupComponent } from './job-delete-dialog.component';
import { IJob } from 'app/shared/model/job.model';
import {JobSubmitPopupComponent} from "app/entities/job/job-submit-dialog.component";
import {JobCancelPopupComponent} from "app/entities/job/job-cancel-dialog.component";
import {BusinessResolve} from "app/home";
import {JobCancelSavepointPopupComponent} from "app/entities/job/job-cancel-savepoint-dialog.component";
import {JobRescalePopupComponent} from "app/entities/job/job-rescale-dialog.component";
import {JobSavepointPopupComponent} from "app/entities/job/job-savepoint-dialog.component";

@Injectable({ providedIn: 'root' })
export class JobResolve implements Resolve<IJob> {
  constructor(private service: JobService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IJob> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Job>) => response.ok),
        map((job: HttpResponse<Job>) => job.body)
      );
    }
    return of(new Job());
  }
}

export const jobRoute: Routes = [
  {
    path: ':id',
    component: JobComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: JobDetailComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/new',
    component: JobUpdateComponent,
    resolve: {
      business: BusinessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: JobUpdateComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const jobPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: JobDeletePopupComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: ':id/cancel',
    component: JobCancelPopupComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: ':id/submit',
    component: JobSubmitPopupComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: ':id/cancel-savepoint',
    component: JobCancelSavepointPopupComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: ':id/rescale',
    component: JobRescalePopupComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: ':id/savepoint',
    component: JobSavepointPopupComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.job.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
