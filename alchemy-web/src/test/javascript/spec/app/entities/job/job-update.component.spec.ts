/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { JobUpdateComponent } from 'app/entities/job/job-update.component';
import { JobService } from 'app/entities/job/job.service';
import { Job } from 'app/shared/model/job.model';

describe('Component Tests', () => {
  describe('Job Management Update Component', () => {
    let comp: JobUpdateComponent;
    let fixture: ComponentFixture<JobUpdateComponent>;
    let service: JobService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [JobUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(JobUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(JobUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(JobService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Job(123);
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
        const entity = new Job();
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
