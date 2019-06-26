import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { IJobSql } from 'app/shared/model/job-sql.model';
import { AccountService } from 'app/core';
import { JobSqlService } from './job-sql.service';
import { IJob } from 'app/shared/model/job.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'jhi-job-sql',
  templateUrl: './job-sql.component.html'
})
export class JobSqlComponent implements OnInit, OnDestroy {
  job: IJob;
  jobSqls: IJobSql[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected jobSqlService: JobSqlService,
    protected jhiAlertService: JhiAlertService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute
  ) {}

  loadAll() {
    this.jobSqlService
      .query({
        'jobId.equals': this.job.id
      })
      .pipe(
        filter((res: HttpResponse<IJobSql[]>) => res.ok),
        map((res: HttpResponse<IJobSql[]>) => res.body)
      )
      .subscribe(
        (res: IJobSql[]) => {
          this.jobSqls = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ job }) => {
      this.job = job;
      this.loadAll();
    });
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInJobSqls();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IJobSql) {
    return item.id;
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  registerChangeInJobSqls() {
    this.eventSubscriber = this.eventManager.subscribe('jobSqlListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  previousState() {
    window.history.back();
  }
}
