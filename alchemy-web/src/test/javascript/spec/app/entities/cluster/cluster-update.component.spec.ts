/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AlchemyTestModule } from '../../../test.module';
import { ClusterUpdateComponent } from 'app/entities/cluster/cluster-update.component';
import { ClusterService } from 'app/entities/cluster/cluster.service';
import { Cluster } from 'app/shared/model/cluster.model';

describe('Component Tests', () => {
  describe('Cluster Management Update Component', () => {
    let comp: ClusterUpdateComponent;
    let fixture: ComponentFixture<ClusterUpdateComponent>;
    let service: ClusterService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AlchemyTestModule],
        declarations: [ClusterUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ClusterUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ClusterUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ClusterService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Cluster(123);
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
        const entity = new Cluster();
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
