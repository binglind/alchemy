import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { ICluster, Cluster } from 'app/shared/model/cluster.model';
import { ClusterService } from './cluster.service';
import {Business, IBusiness} from 'app/shared/model/business.model';
import 'codemirror/mode/yaml/yaml';

@Component({
  selector: 'jhi-cluster-update',
  templateUrl: './cluster-update.component.html'
})
export class ClusterUpdateComponent implements OnInit {
  cluster: ICluster;
  isSaving: boolean;
  business: IBusiness;

  yamlConfig: any = { lineNumbers: true, mode: 'text/x-yaml', theme: 'material' };

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    type: [null, [Validators.required]],
    config: [null, [Validators.required]],
    remark: [null, [Validators.required]],
    createdBy: [],
    createdDate: [],
    lastModifiedBy: [],
    lastModifiedDate: [],
    businessId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected clusterService: ClusterService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ business }) => {
      if(business){
        this.business = business;
        this.cluster = new Cluster();
      }
    });
    this.activatedRoute.data.subscribe(({ cluster }) => {
      if(cluster){
        this.updateForm(cluster);
        this.cluster = cluster;
        this.business = new Business();
        this.business.id = this.cluster.businessId;
      }
    });
  }

  updateForm(cluster: ICluster) {
    this.editForm.patchValue({
      id: cluster.id,
      name: cluster.name,
      type: cluster.type,
      config: cluster.config,
      remark: cluster.remark,
      createdBy: cluster.createdBy,
      createdDate: cluster.createdDate != null ? cluster.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: cluster.lastModifiedBy,
      lastModifiedDate: cluster.lastModifiedDate != null ? cluster.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      businessId: cluster.businessId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const cluster = this.createFromForm();
    if (cluster.id !== undefined && cluster.id != null) {
      this.subscribeToSaveResponse(this.clusterService.update(cluster));
    } else {
      this.subscribeToSaveResponse(this.clusterService.create(cluster));
    }
  }

  private createFromForm(): ICluster {
    const entity = {
      ...new Cluster(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      type: this.editForm.get(['type']).value,
      config: this.editForm.get(['config']).value,
      remark: this.editForm.get(['remark']).value,
      createdBy: this.editForm.get(['createdBy']).value,
      createdDate:
        this.editForm.get(['createdDate']).value != null ? moment(this.editForm.get(['createdDate']).value, DATE_TIME_FORMAT) : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy']).value,
      lastModifiedDate:
        this.editForm.get(['lastModifiedDate']).value != null
          ? moment(this.editForm.get(['lastModifiedDate']).value, DATE_TIME_FORMAT)
          : undefined,
      businessId: this.business.id
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICluster>>) {
    result.subscribe((res: HttpResponse<ICluster>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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
}
