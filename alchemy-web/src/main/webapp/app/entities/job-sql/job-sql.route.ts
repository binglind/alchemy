import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JobSql } from 'app/shared/model/job-sql.model';
import { JobSqlService } from './job-sql.service';
import { JobSqlComponent } from './job-sql.component';
import { JobSqlDetailComponent } from './job-sql-detail.component';
import { JobSqlUpdateComponent } from './job-sql-update.component';
import { JobSqlDeletePopupComponent } from './job-sql-delete-dialog.component';
import { IJobSql } from 'app/shared/model/job-sql.model';
import {JobResolve} from "app/entities/job";

@Injectable({ providedIn: 'root' })
export class JobSqlResolve implements Resolve<IJobSql> {
  constructor(private service: JobSqlService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IJobSql> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<JobSql>) => response.ok),
        map((jobSql: HttpResponse<JobSql>) => jobSql.body)
      );
    }
    return of(new JobSql());
  }
}

export const jobSqlRoute: Routes = [
  {
    path: ':id',
    component: JobSqlComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.jobSql.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: JobSqlDetailComponent,
    resolve: {
      jobSql: JobSqlResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.jobSql.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/new',
    component: JobSqlUpdateComponent,
    resolve: {
      job: JobResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.jobSql.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: JobSqlUpdateComponent,
    resolve: {
      jobSql: JobSqlResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.jobSql.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const jobSqlPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: JobSqlDeletePopupComponent,
    resolve: {
      jobSql: JobSqlResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'alchemyApp.jobSql.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
