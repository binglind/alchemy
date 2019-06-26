import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IJob } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import {FormBuilder, Validators} from "@angular/forms";

@Component({
  selector: 'jhi-job-savepoint-dialog',
  templateUrl: './job-savepoint-dialog.component.html'
})
export class JobSavepointDialogComponent {
  job: IJob;
  savepointForm = this.fb.group({
    savepointDirectory: [null, [Validators.required]]
  });

  constructor(protected jobService: JobService,
              public activeModal: NgbActiveModal,
              protected eventManager: JhiEventManager,
              private fb: FormBuilder) {}

  clear() {
    this.activeModal.dismiss('savepoint');
  }

  confirmSavepoint(id: number) {
    this.jobService.savepoint(id, this.savepointForm.get("savepointDirectory").value).subscribe(response => {
      this.eventManager.broadcast({
        name: 'jobListModification',
        content: 'savepoint'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-job-savepoint-popup',
  template: ''
})
export class JobSavepointPopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ job }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(JobSavepointDialogComponent as Component, { size: 'lg', backdrop: 'static' });
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
