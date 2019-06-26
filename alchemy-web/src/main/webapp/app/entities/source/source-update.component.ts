import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ISource, Source } from 'app/shared/model/source.model';
import { SourceService } from './source.service';
import {Business, IBusiness} from 'app/shared/model/business.model';
import 'codemirror/mode/yaml/yaml';
import {Cluster} from "app/shared/model/cluster.model";
@Component({
  selector: 'jhi-source-update',
  templateUrl: './source-update.component.html'
})
export class SourceUpdateComponent implements OnInit {
  source: ISource;
  isSaving: boolean;
  yamlConfig: any = { lineNumbers: true, mode: 'text/x-yaml', theme: 'material' };
  business: IBusiness;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    tableType: [null, [Validators.required]],
    sourceType: [null, [Validators.required]],
    remark: [null, [Validators.required]],
    config: [null, [Validators.required]],
    createdBy: [],
    createdDate: [],
    lastModifiedBy: [],
    lastModifiedDate: [],
    businessId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected sourceService: SourceService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ business }) => {
      if(business){
        this.business = business;
        this.source = new Source();
      }
    });
    this.activatedRoute.data.subscribe(({ source }) => {
      if(source){
        this.updateForm(source);
        this.source = source;
        this.business = new Business();
        this.business.id = this.source.businessId;
      }

    });
  }

  updateForm(source: ISource) {
    this.editForm.patchValue({
      id: source.id,
      name: source.name,
      tableType: source.tableType,
      sourceType: source.sourceType,
      config: source.config,
      remark: source.remark,
      createdBy: source.createdBy,
      createdDate: source.createdDate != null ? source.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: source.lastModifiedBy,
      lastModifiedDate: source.lastModifiedDate != null ? source.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      businessId: source.businessId
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
    const source = this.createFromForm();
    if (source.id !== undefined && source.id != null) {
      this.subscribeToSaveResponse(this.sourceService.update(source));
    } else {
      this.subscribeToSaveResponse(this.sourceService.create(source));
    }
  }

  private createFromForm(): ISource {
    const entity = {
      ...new Source(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      tableType: this.editForm.get(['tableType']).value,
      sourceType: this.editForm.get(['sourceType']).value,
      config: this.editForm.get(['config']).value,
      createdBy: this.editForm.get(['createdBy']).value,
      createdDate:
        this.editForm.get(['createdDate']).value != null ? moment(this.editForm.get(['createdDate']).value, DATE_TIME_FORMAT) : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy']).value,
      lastModifiedDate:
        this.editForm.get(['lastModifiedDate']).value != null
          ? moment(this.editForm.get(['lastModifiedDate']).value, DATE_TIME_FORMAT)
          : undefined,
      remark: this.editForm.get(['remark']).value,
      businessId: this.business.id
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISource>>) {
    result.subscribe((res: HttpResponse<ISource>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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
