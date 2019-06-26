/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { JobSqlUpdateComponent } from 'app/entities/job-sql/job-sql-update.component';
import { JobSqlService } from 'app/entities/job-sql/job-sql.service';
import { JobSql } from 'app/shared/model/job-sql.model';

describe('Component Tests', () => {
  describe('JobSql Management Update Component', () => {
    let comp: JobSqlUpdateComponent;
    let fixture: ComponentFixture<JobSqlUpdateComponent>;
    let service: JobSqlService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [JobSqlUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(JobSqlUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(JobSqlUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(JobSqlService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new JobSql(123);
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
        const entity = new JobSql();
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
