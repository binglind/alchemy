import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IUdf } from 'app/shared/model/udf.model';

@Component({
  selector: 'jhi-udf-detail',
  templateUrl: './udf-detail.component.html'
})
export class UdfDetailComponent implements OnInit {
  udf: IUdf;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ udf }) => {
      this.udf = udf;
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
