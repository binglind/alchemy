import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IJobSql } from 'app/shared/model/job-sql.model';

@Component({
  selector: 'jhi-job-sql-detail',
  templateUrl: './job-sql-detail.component.html'
})
export class JobSqlDetailComponent implements OnInit {
  jobSql: IJobSql;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ jobSql }) => {
      this.jobSql = jobSql;
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }
  previousState() {
    window.history.back();
  }
}
