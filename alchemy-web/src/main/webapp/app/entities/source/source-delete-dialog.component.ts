import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISource } from 'app/shared/model/source.model';
import { SourceService } from './source.service';

@Component({
  selector: 'jhi-source-delete-dialog',
  templateUrl: './source-delete-dialog.component.html'
})
export class SourceDeleteDialogComponent {
  source: ISource;

  constructor(protected sourceService: SourceService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.sourceService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'sourceListModification',
        content: 'Deleted an source'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-source-delete-popup',
  template: ''
})
export class SourceDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ source }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(SourceDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.source = source;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/source', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/source', { outlets: { popup: null } }]);
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
