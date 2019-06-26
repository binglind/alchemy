/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AlchemyTestModule } from '../../../test.module';
import { JobSqlComponent } from 'app/entities/job-sql/job-sql.component';
import { JobSqlService } from 'app/entities/job-sql/job-sql.service';
import { JobSql } from 'app/shared/model/job-sql.model';

describe('Component Tests', () => {
  describe('JobSql Management Component', () => {
    let comp: JobSqlComponent;
    let fixture: ComponentFixture<JobSqlComponent>;
    let service: JobSqlService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [JobSqlComponent],
        providers: []
      })
        .overrideTemplate(JobSqlComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(JobSqlComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(JobSqlService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new JobSql(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.jobSqls[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
