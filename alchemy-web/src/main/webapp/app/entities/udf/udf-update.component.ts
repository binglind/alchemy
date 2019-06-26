import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IUdf, Udf } from 'app/shared/model/udf.model';
import { UdfService } from './udf.service';
import { Business, IBusiness } from 'app/shared/model/business.model';
import 'codemirror/mode/groovy/groovy';

@Component({
  selector: 'jhi-udf-update',
  templateUrl: './udf-update.component.html'
})
export class UdfUpdateComponent implements OnInit {
  udf: IUdf;
  isSaving: boolean;
  groovyConfig: any = { lineNumbers: true, mode: 'text/x-groovy', theme: 'material' };
  business: IBusiness;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    type: [null, [Validators.required]],
    remark: [null, [Validators.required]],
    value: [null, [Validators.required]],
    dependency: [],
    createdBy: [],
    createdDate: [],
    lastModifiedBy: [],
    lastModifiedDate: [],
    businessId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected udfService: UdfService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ business }) => {
      if (business) {
        this.business = business;
        this.udf = new Udf();
      }
    });
    this.activatedRoute.data.subscribe(({ udf }) => {
      if (udf) {
        this.updateForm(udf);
        this.udf = udf;
        this.business = new Business();
        this.business.id = this.udf.businessId;
      }
    });
  }

  updateForm(udf: IUdf) {
    this.editForm.patchValue({
      id: udf.id,
      name: udf.name,
      type: udf.type,
      value: udf.value,
      dependency: udf.dependency,
      createdBy: udf.createdBy,
      createdDate: udf.createdDate != null ? udf.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: udf.lastModifiedBy,
      lastModifiedDate: udf.lastModifiedDate != null ? udf.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      remark: udf.remark,
      businessId: udf.businessId
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
    const udf = this.createFromForm();
    if (udf.id !== undefined && udf.id != null) {
      this.subscribeToSaveResponse(this.udfService.update(udf));
    } else {
      this.subscribeToSaveResponse(this.udfService.create(udf));
    }
  }

  private createFromForm(): IUdf {
    const entity = {
      ...new Udf(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      type: this.editForm.get(['type']).value,
      value: this.editForm.get(['value']).value,
      dependency: this.editForm.get(['dependency']).value,
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUdf>>) {
    result.subscribe((res: HttpResponse<IUdf>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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
