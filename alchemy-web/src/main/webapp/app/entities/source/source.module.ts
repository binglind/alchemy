import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { CodemirrorModule } from 'ng2-codemirror';

import { AlchemySharedModule } from 'app/shared';
import {
  SourceComponent,
  SourceDetailComponent,
  SourceUpdateComponent,
  SourceDeletePopupComponent,
  SourceDeleteDialogComponent,
  sourceRoute,
  sourcePopupRoute
} from './';

const ENTITY_STATES = [...sourceRoute, ...sourcePopupRoute];

@NgModule({
  imports: [AlchemySharedModule, CodemirrorModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [SourceComponent, SourceDetailComponent, SourceUpdateComponent, SourceDeleteDialogComponent, SourceDeletePopupComponent],
  entryComponents: [SourceComponent, SourceUpdateComponent, SourceDeleteDialogComponent, SourceDeletePopupComponent],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemySourceModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
