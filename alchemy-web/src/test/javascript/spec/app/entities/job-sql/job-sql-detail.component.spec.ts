/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { JobSqlDetailComponent } from 'app/entities/job-sql/job-sql-detail.component';
import { JobSql } from 'app/shared/model/job-sql.model';

describe('Component Tests', () => {
  describe('JobSql Management Detail Component', () => {
    let comp: JobSqlDetailComponent;
    let fixture: ComponentFixture<JobSqlDetailComponent>;
    const route = ({ data: of({ jobSql: new JobSql(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [JobSqlDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(JobSqlDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(JobSqlDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.jobSql).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
