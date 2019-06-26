import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IJobSql, JobSql } from 'app/shared/model/job-sql.model';
import { JobSqlService } from './job-sql.service';
import {IJob, Job} from 'app/shared/model/job.model';
import { JobService } from 'app/entities/job';
import 'codemirror/mode/sql/sql';

@Component({
  selector: 'jhi-job-sql-update',
  templateUrl: './job-sql-update.component.html'
})
export class JobSqlUpdateComponent implements OnInit {
  jobSql: IJobSql;
  isSaving: boolean;
  sqlConfig: any = { lineNumbers: true, mode: 'text/x-sql' };
  job: IJob;

  editForm = this.fb.group({
    id: [],
    sql: [null, [Validators.required]],
    createdBy: [],
    createdDate: [],
    lastModifiedBy: [],
    lastModifiedDate: [],
    jobId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected jobSqlService: JobSqlService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ job }) => {
      if(job){
        this.job = job;
        this.jobSql = new JobSql();
      }
    });
    this.activatedRoute.data.subscribe(({ jobSql }) => {
      if(jobSql){
        this.updateForm(jobSql);
        this.jobSql = jobSql;
        this.job = new Job();
        this.job.id = this.jobSql.jobId;
      }

    });
  }

  updateForm(jobSql: IJobSql) {
    this.editForm.patchValue({
      id: jobSql.id,
      sql: jobSql.sql,
      createdBy: jobSql.createdBy,
      createdDate: jobSql.createdDate != null ? jobSql.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: jobSql.lastModifiedBy,
      lastModifiedDate: jobSql.lastModifiedDate != null ? jobSql.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      jobId: jobSql.jobId
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file = event.target.files[0];
        if (isImage && !/^image\//.test(file.type)) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      () => console.log('blob added'), // sucess
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const jobSql = this.createFromForm();
    if (jobSql.id !== undefined && jobSql.id != null) {
      this.subscribeToSaveResponse(this.jobSqlService.update(jobSql));
    } else {
      this.subscribeToSaveResponse(this.jobSqlService.create(jobSql));
    }
  }

  private createFromForm(): IJobSql {
    const entity = {
      ...new JobSql(),
      id: this.editForm.get(['id']).value,
      sql: this.editForm.get(['sql']).value,
      createdBy: this.editForm.get(['createdBy']).value,
      createdDate:
        this.editForm.get(['createdDate']).value != null ? moment(this.editForm.get(['createdDate']).value, DATE_TIME_FORMAT) : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy']).value,
      lastModifiedDate:
        this.editForm.get(['lastModifiedDate']).value != null
          ? moment(this.editForm.get(['lastModifiedDate']).value, DATE_TIME_FORMAT)
          : undefined,
      jobId: this.job.id
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJobSql>>) {
    result.subscribe((res: HttpResponse<IJobSql>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackJobById(index: number, item: IJob) {
    return item.id;
  }
}
