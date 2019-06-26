/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { SourceUpdateComponent } from 'app/entities/source/source-update.component';
import { SourceService } from 'app/entities/source/source.service';
import { Source } from 'app/shared/model/source.model';

describe('Component Tests', () => {
  describe('Source Management Update Component', () => {
    let comp: SourceUpdateComponent;
    let fixture: ComponentFixture<SourceUpdateComponent>;
    let service: SourceService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [SourceUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SourceUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SourceUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SourceService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Source(123);
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
        const entity = new Source();
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
