import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { AlchemySharedModule } from 'app/shared';
import {
  HomeComponent,
  BusinessDetailComponent,
  BusinessUpdateComponent,
  BusinessDeletePopupComponent,
  BusinessDeleteDialogComponent,
  businessRoute,
  businessPopupRoute
} from './';

const ENTITY_STATES = [...businessRoute, ...businessPopupRoute];

@NgModule({
  imports: [AlchemySharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    HomeComponent,
    BusinessDetailComponent,
    BusinessUpdateComponent,
    BusinessDeleteDialogComponent,
    BusinessDeletePopupComponent
  ],
  entryComponents: [HomeComponent, BusinessUpdateComponent, BusinessDeleteDialogComponent, BusinessDeletePopupComponent],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemyHomeModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
