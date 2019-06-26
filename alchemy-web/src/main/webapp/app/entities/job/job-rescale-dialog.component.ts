import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IJob } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import {FormBuilder, Validators} from "@angular/forms";

@Component({
  selector: 'jhi-job-rescale-dialog',
  templateUrl: './job-rescale-dialog.component.html'
})
export class JobReScaleDialogComponent {
  job: IJob;
  newParallelism: number;
  rescaleForm = this.fb.group({
    newParallelism: [null, [Validators.required]]
  });

  constructor(protected jobService: JobService,
              public activeModal: NgbActiveModal,
              protected eventManager: JhiEventManager,
              private fb: FormBuilder) {}

  clear() {
    this.activeModal.dismiss('rescale');
  }

  confirmRescale(id: number) {
    this.jobService.rescale(id, this.rescaleForm.get("newParallelism").value).subscribe(response => {
      this.eventManager.broadcast({
        name: 'jobListModification',
        content: 'rescale an job'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-job-rescale-popup',
  template: ''
})
export class JobRescalePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ job }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(JobReScaleDialogComponent as Component, { size: 'lg', backdrop: 'static' });
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
