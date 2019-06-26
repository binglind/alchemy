import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'cluster',
        loadChildren: './cluster/cluster.module#AlchemyClusterModule'
      },
      {
        path: 'job',
        loadChildren: './job/job.module#AlchemyJobModule'
      },
      {
        path: 'job-sql',
        loadChildren: './job-sql/job-sql.module#AlchemyJobSqlModule'
      },
      {
        path: 'source',
        loadChildren: './source/source.module#AlchemySourceModule'
      },
      {
        path: 'udf',
        loadChildren: './udf/udf.module#AlchemyUdfModule'
      },
      {
        path: 'sink',
        loadChildren: './sink/sink.module#AlchemySinkModule'
      },
      {
        path: 'udf',
        loadChildren: './udf/udf.module#AlchemyUdfModule'
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ],
  declarations: [],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlchemyEntityModule {}
