/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { SinkDetailComponent } from 'app/entities/sink/sink-detail.component';
import { Sink } from 'app/shared/model/sink.model';

describe('Component Tests', () => {
  describe('Sink Management Detail Component', () => {
    let comp: SinkDetailComponent;
    let fixture: ComponentFixture<SinkDetailComponent>;
    const route = ({ data: of({ sink: new Sink(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [SinkDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(SinkDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SinkDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.sink).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
