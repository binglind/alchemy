import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IJob } from 'app/shared/model/job.model';
import {JhiTrackerService} from "app/core";


@Component({
  selector: 'jhi-job-detail',
  templateUrl: './job-detail.component.html'
})
export class JobDetailComponent implements OnInit {
  job: IJob;

  constructor(protected activatedRoute: ActivatedRoute, private trackerService: JhiTrackerService) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ job }) => {
      this.job = job;
    });
    this.trackerService.connect();
    this.trackerService.sendActivity();
    this.trackerService.subscribe();
    this.trackerService.receive().subscribe(({ data }) => {
       console.log(data)
    })

  }

  previousState() {
    window.history.back();
  }
}
