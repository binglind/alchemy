import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IUdf } from 'app/shared/model/udf.model';
import { UdfService } from './udf.service';

@Component({
  selector: 'jhi-udf-delete-dialog',
  templateUrl: './udf-delete-dialog.component.html'
})
export class UdfDeleteDialogComponent {
  udf: IUdf;

  constructor(protected udfService: UdfService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.udfService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'udfListModification',
        content: 'Deleted an udf'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-udf-delete-popup',
  template: ''
})
export class UdfDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ udf }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(UdfDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.udf = udf;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/udf', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/udf', { outlets: { popup: null } }]);
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
