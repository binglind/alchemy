import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { CodemirrorModule } from 'ng2-codemirror';
import { AlchemySharedModule } from 'app/shared';
import {
  ClusterComponent,
  ClusterDetailComponent,
  ClusterUpdateComponent,
  ClusterDeletePopupComponent,
  ClusterDeleteDialogComponent,
  clusterRoute,
  clusterPopupRoute
} from './';

const ENTITY_STATES = [...clusterRoute, ...clusterPopupRoute];

@NgModule({
  imports: [AlchemySharedModule, CodemirrorModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ClusterComponent,
    ClusterDetailComponent,
    ClusterUpdateComponent,
    ClusterDeleteDialogComponent,
    ClusterDeletePopupComponent
  ],
  entryComponents: [ClusterComponent, ClusterUpdateComponent, ClusterDeleteDialogComponent, ClusterDeletePopupComponent],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemyClusterModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
