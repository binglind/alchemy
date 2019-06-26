/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AlchemyTestModule } from '../../../test.module';
import { SinkDeleteDialogComponent } from 'app/entities/sink/sink-delete-dialog.component';
import { SinkService } from 'app/entities/sink/sink.service';

describe('Component Tests', () => {
  describe('Sink Management Delete Component', () => {
    let comp: SinkDeleteDialogComponent;
    let fixture: ComponentFixture<SinkDeleteDialogComponent>;
    let service: SinkService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [SinkDeleteDialogComponent]
      })
        .overrideTemplate(SinkDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SinkDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SinkService);
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
