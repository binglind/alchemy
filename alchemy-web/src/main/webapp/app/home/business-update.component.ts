import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { IBusiness, Business } from 'app/shared/model/business.model';
import { BusinessService } from './business.service';

@Component({
  selector: 'jhi-business-update',
  templateUrl: './business-update.component.html'
})
export class BusinessUpdateComponent implements OnInit {
  business: IBusiness;
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    remark: [null, [Validators.required]],
    createdBy: [],
    createdDate: []
  });

  constructor(protected businessService: BusinessService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ business }) => {
      this.updateForm(business);
      this.business = business;
    });
  }

  updateForm(business: IBusiness) {
    this.editForm.patchValue({
      id: business.id,
      name: business.name,
      remark: business.remark,
      createdBy: business.createdBy,
      createdDate: business.createdDate != null ? business.createdDate.format(DATE_TIME_FORMAT) : null
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const business = this.createFromForm();
    if (business.id !== undefined) {
      this.subscribeToSaveResponse(this.businessService.update(business));
    } else {
      this.subscribeToSaveResponse(this.businessService.create(business));
    }
  }

  private createFromForm(): IBusiness {
    const entity = {
      ...new Business(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      remark: this.editForm.get(['remark']).value,
      createdBy: this.editForm.get(['createdBy']).value,
      createdDate:
        this.editForm.get(['createdDate']).value != null ? moment(this.editForm.get(['createdDate']).value, DATE_TIME_FORMAT) : undefined
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBusiness>>) {
    result.subscribe((res: HttpResponse<IBusiness>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
