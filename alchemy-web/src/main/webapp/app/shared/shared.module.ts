import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { AlchemySharedLibsModule, AlchemySharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [AlchemySharedLibsModule, AlchemySharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [AlchemySharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemySharedModule {
  static forRoot() {
    return {
      ngModule: AlchemySharedModule
    };
  }
}
