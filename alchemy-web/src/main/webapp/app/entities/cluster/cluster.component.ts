import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { ICluster } from 'app/shared/model/cluster.model';
import { AccountService } from 'app/core';

import { ITEMS_PER_PAGE } from 'app/shared';
import { ClusterService } from './cluster.service';
import { IBusiness } from 'app/shared/model/business.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'jhi-cluster',
  templateUrl: './cluster.component.html'
})
export class ClusterComponent implements OnInit, OnDestroy {
  business: IBusiness;
  clusters: ICluster[];
  currentAccount: any;
  eventSubscriber: Subscription;
  itemsPerPage: number;
  links: any;
  page: any;
  predicate: any;
  reverse: any;
  totalItems: number;

  constructor(
    protected clusterService: ClusterService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected parseLinks: JhiParseLinks,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute
  ) {
    this.clusters = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.reverse = true;
  }

  loadAll() {
    this.clusterService
      .query({
        'businessId.equals': this.business.id,
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe(
        (res: HttpResponse<ICluster[]>) => this.paginateClusters(res.body, res.headers),
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  reset() {
    this.page = 0;
    this.clusters = [];
    this.loadAll();
  }

  loadPage(page) {
    this.page = page;
    this.loadAll();
  }

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ business }) => {
      this.business = business;
      this.loadAll();
    });
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInClusters();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ICluster) {
    return item.id;
  }

  registerChangeInClusters() {
    this.eventSubscriber = this.eventManager.subscribe('clusterListModification', response => this.reset());
  }

  sort() {
    const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateClusters(data: ICluster[], headers: HttpHeaders) {
    this.links = this.parseLinks.parse(headers.get('link'));
    this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
    for (let i = 0; i < data.length; i++) {
      this.clusters.push(data[i]);
    }
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  detail(id) {
    this.clusterService.queryUrl(id).subscribe(response => {
      console.log(response);
      if (response.body && response.body.url) {
        window.open(response.body.url, '_blank');
      } else {
        this.onError('Please set web-interface-url');
      }
    });
  }
}
