import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import { IJob } from 'app/shared/model/job.model';
import { JobService } from './job.service';

@Component({
  selector: 'jhi-job-cancel-dialog',
  templateUrl: './job-cancel-dialog.component.html'
})
export class JobCancelDialogComponent {
  job: IJob;

  constructor(protected jobService: JobService,
              public activeModal: NgbActiveModal,
              protected eventManager: JhiEventManager,
              protected jhiAlertService: JhiAlertService,) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmcancel(id: number) {
    this.jobService.cancel(id).subscribe(response => {

      this.eventManager.broadcast({
        name: 'jobListModification',
        content: 'cancel an job'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-job-cancel-popup',
  template: ''
})
export class JobCancelPopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ job }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(JobCancelDialogComponent as Component, { size: 'lg', backdrop: 'static' });
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
