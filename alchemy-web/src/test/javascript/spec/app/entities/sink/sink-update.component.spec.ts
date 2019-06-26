/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { SinkUpdateComponent } from 'app/entities/sink/sink-update.component';
import { SinkService } from 'app/entities/sink/sink.service';
import { Sink } from 'app/shared/model/sink.model';

describe('Component Tests', () => {
  describe('Sink Management Update Component', () => {
    let comp: SinkUpdateComponent;
    let fixture: ComponentFixture<SinkUpdateComponent>;
    let service: SinkService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [SinkUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SinkUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SinkUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SinkService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Sink(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Sink();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
