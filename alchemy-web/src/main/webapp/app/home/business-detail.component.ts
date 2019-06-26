import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBusiness } from 'app/shared/model/business.model';

@Component({
  selector: 'jhi-business-detail',
  templateUrl: './business-detail.component.html'
})
export class BusinessDetailComponent implements OnInit {
  business: IBusiness;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ business }) => {
      this.business = business;
    });
  }

  previousState() {
    window.history.back();
  }
}
