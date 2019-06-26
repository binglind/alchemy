/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JobService } from 'app/entities/job/job.service';
import { IJob, Job, JobType, JobStatus } from 'app/shared/model/job.model';

describe('Service Tests', () => {
  describe('Job Service', () => {
    let injector: TestBed;
    let service: JobService;
    let httpMock: HttpTestingController;
    let elemDefault: IJob;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(JobService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Job(
        0,
        'AAAAAAA',
        JobType.JAR,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        JobStatus.CREATE,
        'AAAAAAA',
        currentDate,
        'AAAAAAA',
        currentDate
      );
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a Job', async () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            createdDate: currentDate,
            lastModifiedDate: currentDate
          },
          returnedFromService
        );
        service
          .create(new Job(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a Job', async () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            type: 'BBBBBB',
            config: 'BBBBBB',
            remark: 'BBBBBB',
            clusterJobId: 'BBBBBB',
            status: 'BBBBBB',
            createdBy: 'BBBBBB',
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedBy: 'BBBBBB',
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdDate: currentDate,
            lastModifiedDate: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of Job', async () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            type: 'BBBBBB',
            config: 'BBBBBB',
            remark: 'BBBBBB',
            clusterJobId: 'BBBBBB',
            status: 'BBBBBB',
            createdBy: 'BBBBBB',
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedBy: 'BBBBBB',
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            createdDate: currentDate,
            lastModifiedDate: currentDate
          },
          returnedFromService
        );
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Job', async () => {
        const rxPromise = service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
