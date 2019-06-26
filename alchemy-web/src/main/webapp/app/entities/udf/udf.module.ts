import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { CodemirrorModule } from 'ng2-codemirror';

import { AlchemySharedModule } from 'app/shared';
import {
  UdfComponent,
  UdfDetailComponent,
  UdfUpdateComponent,
  UdfDeletePopupComponent,
  UdfDeleteDialogComponent,
  udfRoute,
  udfPopupRoute
} from './';

const ENTITY_STATES = [...udfRoute, ...udfPopupRoute];

@NgModule({
  imports: [AlchemySharedModule,CodemirrorModule , RouterModule.forChild(ENTITY_STATES)],
  declarations: [UdfComponent, UdfDetailComponent, UdfUpdateComponent, UdfDeleteDialogComponent, UdfDeletePopupComponent],
  entryComponents: [UdfComponent, UdfUpdateComponent, UdfDeleteDialogComponent, UdfDeletePopupComponent],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemyUdfModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
