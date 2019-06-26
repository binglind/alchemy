import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IJobSql } from 'app/shared/model/job-sql.model';
import { JobSqlService } from './job-sql.service';

@Component({
  selector: 'jhi-job-sql-delete-dialog',
  templateUrl: './job-sql-delete-dialog.component.html'
})
export class JobSqlDeleteDialogComponent {
  jobSql: IJobSql;

  constructor(protected jobSqlService: JobSqlService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.jobSqlService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'jobSqlListModification',
        content: 'Deleted an jobSql'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-job-sql-delete-popup',
  template: ''
})
export class JobSqlDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ jobSql }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(JobSqlDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.jobSql = jobSql;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/job-sql', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/job-sql', { outlets: { popup: null } }]);
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
