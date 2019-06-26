import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { IJob, Job } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import { Business, IBusiness } from 'app/shared/model/business.model';
import { ICluster } from 'app/shared/model/cluster.model';
import { ClusterService } from 'app/entities/cluster';
import 'codemirror/mode/yaml/yaml';

@Component({
  selector: 'jhi-job-update',
  templateUrl: './job-update.component.html'
})
export class JobUpdateComponent implements OnInit {
  job: IJob;
  isSaving: boolean;
  yamlConfig: any = { lineNumbers: true, mode: 'text/x-yaml', theme: 'material' };

  business: IBusiness;

  clusters: ICluster[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    type: [null, [Validators.required]],
    config: [null, [Validators.required]],
    remark: [null, [Validators.required]],
    clusterJobId: [],
    status: [],
    createdBy: [],
    createdDate: [],
    lastModifiedBy: [],
    lastModifiedDate: [],
    businessId: [],
    clusterId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected jobService: JobService,
    protected clusterService: ClusterService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ business }) => {
      if (business) {
        this.business = business;
        this.job = new Job();
      }
    });
    this.activatedRoute.data.subscribe(({ job }) => {
      if (job) {
        this.updateForm(job);
        this.job = job;
        this.business = new Business();
        this.business.id = this.job.businessId;
      }
    });
    this.clusterService
      .query({
        'businessId.equals': this.business.id
      })
      .pipe(
        filter((mayBeOk: HttpResponse<ICluster[]>) => mayBeOk.ok),
        map((response: HttpResponse<ICluster[]>) => response.body)
      )
      .subscribe((res: ICluster[]) => (this.clusters = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(job: IJob) {
    this.editForm.patchValue({
      id: job.id,
      name: job.name,
      type: job.type,
      config: job.config,
      remark: job.remark,
      clusterJobId: job.clusterJobId,
      status: job.status,
      createdBy: job.createdBy,
      createdDate: job.createdDate != null ? job.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: job.lastModifiedBy,
      lastModifiedDate: job.lastModifiedDate != null ? job.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      businessId: job.businessId,
      clusterId: job.clusterId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const job = this.createFromForm();
    if (job.id !== undefined && job.id != null) {
      this.subscribeToSaveResponse(this.jobService.update(job));
    } else {
      this.subscribeToSaveResponse(this.jobService.create(job));
    }
  }

  private createFromForm(): IJob {
    const entity = {
      ...new Job(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      type: this.editForm.get(['type']).value,
      config: this.editForm.get(['config']).value,
      remark: this.editForm.get(['remark']).value,
      clusterJobId: this.editForm.get(['clusterJobId']).value,
      status: this.editForm.get(['status']).value,
      createdBy: this.editForm.get(['createdBy']).value,
      createdDate:
        this.editForm.get(['createdDate']).value != null ? moment(this.editForm.get(['createdDate']).value, DATE_TIME_FORMAT) : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy']).value,
      lastModifiedDate:
        this.editForm.get(['lastModifiedDate']).value != null
          ? moment(this.editForm.get(['lastModifiedDate']).value, DATE_TIME_FORMAT)
          : undefined,
      businessId: this.business.id,
      clusterId: this.editForm.get(['clusterId']).value
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJob>>) {
    result.subscribe((res: HttpResponse<IJob>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

  trackBusinessById(index: number, item: IBusiness) {
    return item.id;
  }

  trackClusterById(index: number, item: ICluster) {
    return item.id;
  }
}
