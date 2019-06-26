import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICluster } from 'app/shared/model/cluster.model';
import { ClusterService } from './cluster.service';

@Component({
  selector: 'jhi-cluster-delete-dialog',
  templateUrl: './cluster-delete-dialog.component.html'
})
export class ClusterDeleteDialogComponent {
  cluster: ICluster;

  constructor(protected clusterService: ClusterService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.clusterService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'clusterListModification',
        content: 'Deleted an cluster'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-cluster-delete-popup',
  template: ''
})
export class ClusterDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ cluster }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ClusterDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.cluster = cluster;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/cluster', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/cluster', { outlets: { popup: null } }]);
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
