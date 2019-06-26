import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { CodemirrorModule } from 'ng2-codemirror';

import { AlchemySharedModule } from 'app/shared';
import {
  SinkComponent,
  SinkDetailComponent,
  SinkUpdateComponent,
  SinkDeletePopupComponent,
  SinkDeleteDialogComponent,
  sinkRoute,
  sinkPopupRoute
} from './';

const ENTITY_STATES = [...sinkRoute, ...sinkPopupRoute];

@NgModule({
  imports: [AlchemySharedModule,CodemirrorModule,  RouterModule.forChild(ENTITY_STATES)],
  declarations: [SinkComponent, SinkDetailComponent, SinkUpdateComponent, SinkDeleteDialogComponent, SinkDeletePopupComponent],
  entryComponents: [SinkComponent, SinkUpdateComponent, SinkDeleteDialogComponent, SinkDeletePopupComponent],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemySinkModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
