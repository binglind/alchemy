import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { CodemirrorModule } from 'ng2-codemirror';

import { AlchemySharedModule } from 'app/shared';
import {
  JobSqlComponent,
  JobSqlDetailComponent,
  JobSqlUpdateComponent,
  JobSqlDeletePopupComponent,
  JobSqlDeleteDialogComponent,
  jobSqlRoute,
  jobSqlPopupRoute
} from './';

const ENTITY_STATES = [...jobSqlRoute, ...jobSqlPopupRoute];

@NgModule({
  imports: [AlchemySharedModule,CodemirrorModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [JobSqlComponent, JobSqlDetailComponent, JobSqlUpdateComponent, JobSqlDeleteDialogComponent, JobSqlDeletePopupComponent],
  entryComponents: [JobSqlComponent, JobSqlUpdateComponent, JobSqlDeleteDialogComponent, JobSqlDeletePopupComponent],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemyJobSqlModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
