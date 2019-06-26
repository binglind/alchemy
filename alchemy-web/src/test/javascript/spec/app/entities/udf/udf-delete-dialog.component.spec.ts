/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AlchemyTestModule } from '../../../test.module';
import { UdfDeleteDialogComponent } from 'app/entities/udf/udf-delete-dialog.component';
import { UdfService } from 'app/entities/udf/udf.service';

describe('Component Tests', () => {
  describe('Udf Management Delete Component', () => {
    let comp: UdfDeleteDialogComponent;
    let fixture: ComponentFixture<UdfDeleteDialogComponent>;
    let service: UdfService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [UdfDeleteDialogComponent]
      })
        .overrideTemplate(UdfDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(UdfDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(UdfService);
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
