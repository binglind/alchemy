/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { UdfDetailComponent } from 'app/entities/udf/udf-detail.component';
import { Udf } from 'app/shared/model/udf.model';

describe('Component Tests', () => {
  describe('Udf Management Detail Component', () => {
    let comp: UdfDetailComponent;
    let fixture: ComponentFixture<UdfDetailComponent>;
    const route = ({ data: of({ udf: new Udf(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [UdfDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(UdfDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(UdfDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.udf).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
