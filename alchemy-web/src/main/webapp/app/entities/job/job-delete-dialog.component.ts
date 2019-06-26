import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IJob } from 'app/shared/model/job.model';
import { JobService } from './job.service';

@Component({
  selector: 'jhi-job-delete-dialog',
  templateUrl: './job-delete-dialog.component.html'
})
export class JobDeleteDialogComponent {
  job: IJob;

  constructor(protected jobService: JobService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.jobService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'jobListModification',
        content: 'Deleted an job'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-job-delete-popup',
  template: ''
})
export class JobDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ job }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(JobDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.job = job;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/job', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/job', { outlets: { popup: null } }]);
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
