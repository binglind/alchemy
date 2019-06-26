/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AlchemyTestModule } from '../../../test.module';
import { JobSqlDeleteDialogComponent } from 'app/entities/job-sql/job-sql-delete-dialog.component';
import { JobSqlService } from 'app/entities/job-sql/job-sql.service';

describe('Component Tests', () => {
  describe('JobSql Management Delete Component', () => {
    let comp: JobSqlDeleteDialogComponent;
    let fixture: ComponentFixture<JobSqlDeleteDialogComponent>;
    let service: JobSqlService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [JobSqlDeleteDialogComponent]
      })
        .overrideTemplate(JobSqlDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(JobSqlDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(JobSqlService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
