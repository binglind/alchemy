import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBusiness } from 'app/shared/model/business.model';
import { BusinessService } from './business.service';

@Component({
  selector: 'jhi-business-delete-dialog',
  templateUrl: './business-delete-dialog.component.html'
})
export class BusinessDeleteDialogComponent {
  business: IBusiness;

  constructor(protected businessService: BusinessService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.businessService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'businessListModification',
        content: 'Deleted an business'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-business-delete-popup',
  template: ''
})
export class BusinessDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ business }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(BusinessDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.business = business;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/business', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/business', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
