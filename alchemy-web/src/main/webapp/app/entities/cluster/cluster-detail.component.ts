import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICluster } from 'app/shared/model/cluster.model';

@Component({
  selector: 'jhi-cluster-detail',
  templateUrl: './cluster-detail.component.html'
})
export class ClusterDetailComponent implements OnInit {
  cluster: ICluster;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ cluster }) => {
      this.cluster = cluster;
    });
  }

  previousState() {
    window.history.back();
  }
}
