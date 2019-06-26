/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { UdfUpdateComponent } from 'app/entities/udf/udf-update.component';
import { UdfService } from 'app/entities/udf/udf.service';
import { Udf } from 'app/shared/model/udf.model';

describe('Component Tests', () => {
  describe('Udf Management Update Component', () => {
    let comp: UdfUpdateComponent;
    let fixture: ComponentFixture<UdfUpdateComponent>;
    let service: UdfService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [UdfUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(UdfUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UdfUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(UdfService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Udf(123);
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
        const entity = new Udf();
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
