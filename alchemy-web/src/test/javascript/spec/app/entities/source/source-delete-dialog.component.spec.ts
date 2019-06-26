/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AlchemyTestModule } from '../../../test.module';
import { SourceDeleteDialogComponent } from 'app/entities/source/source-delete-dialog.component';
import { SourceService } from 'app/entities/source/source.service';

describe('Component Tests', () => {
  describe('Source Management Delete Component', () => {
    let comp: SourceDeleteDialogComponent;
    let fixture: ComponentFixture<SourceDeleteDialogComponent>;
    let service: SourceService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [SourceDeleteDialogComponent]
      })
        .overrideTemplate(SourceDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SourceDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SourceService);
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
