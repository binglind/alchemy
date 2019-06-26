/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { ClusterDetailComponent } from 'app/entities/cluster/cluster-detail.component';
import { Cluster } from 'app/shared/model/cluster.model';

describe('Component Tests', () => {
  describe('Cluster Management Detail Component', () => {
    let comp: ClusterDetailComponent;
    let fixture: ComponentFixture<ClusterDetailComponent>;
    const route = ({ data: of({ cluster: new Cluster(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [ClusterDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ClusterDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ClusterDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.cluster).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
