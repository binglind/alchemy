import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IBusiness } from 'app/shared/model/business.model';
import { AccountService } from 'app/core';
import { BusinessService } from './business.service';

@Component({
  selector: 'jhi-business',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit, OnDestroy {
  businesses: IBusiness[];
  currentAccount: any;
  eventSubscriber: Subscription;
  isNavbarCollapsed: boolean;

  constructor(
    protected businessService: BusinessService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.businessService
      .query()
      .pipe(
        filter((res: HttpResponse<IBusiness[]>) => res.ok),
        map((res: HttpResponse<IBusiness[]>) => res.body)
      )
      .subscribe(
        (res: IBusiness[]) => {
          this.businesses = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInBusinesses();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IBusiness) {
    return item.id;
  }

  registerChangeInBusinesses() {
    this.eventSubscriber = this.eventManager.subscribe('businessListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
