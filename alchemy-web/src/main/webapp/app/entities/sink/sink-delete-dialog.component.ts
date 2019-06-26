import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISink } from 'app/shared/model/sink.model';
import { SinkService } from './sink.service';

@Component({
  selector: 'jhi-sink-delete-dialog',
  templateUrl: './sink-delete-dialog.component.html'
})
export class SinkDeleteDialogComponent {
  sink: ISink;

  constructor(protected sinkService: SinkService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.sinkService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'sinkListModification',
        content: 'Deleted an sink'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-sink-delete-popup',
  template: ''
})
export class SinkDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ sink }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(SinkDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.sink = sink;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/sink', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/sink', { outlets: { popup: null } }]);
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
