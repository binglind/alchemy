import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IJob } from 'app/shared/model/job.model';

type EntityResponseType = HttpResponse<IJob>;
type EntityArrayResponseType = HttpResponse<IJob[]>;

@Injectable({ providedIn: 'root' })
export class JobService {
  public resourceUrl = SERVER_API_URL + 'api/jobs';

  constructor(protected http: HttpClient) {}

  create(job: IJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http
      .post<IJob>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(job: IJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http
      .put<IJob>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IJob>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IJob[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  submit(id: number): Observable<HttpResponse<any>> {
    return this.http.get<any>(`${this.resourceUrl}/submit/${id}`, { observe: 'response' });
  }

  cancel(id: number): Observable<HttpResponse<any>> {
    return this.http.get<any>(`${this.resourceUrl}/cancel/${id}`, { observe: 'response' });
  }

  cancelWithSavepoint(id: number, savepointDirectory: string): Observable<HttpResponse<any>> {
    const copy = {id: id, savepointDirectory: savepointDirectory}
    return this.http
      .get<any>(`${this.resourceUrl}/cancel-savepoint?id=`+id+`&savepointDirectory=`+savepointDirectory, { observe: 'response' });
  }

  savepoint(id: number, savepointDirectory: string): Observable<HttpResponse<any>> {
    return this.http
      .get<any>(`${this.resourceUrl}/trigger-savepoint?id=`+id+`&savepointDirectory=`+savepointDirectory, { observe: 'response' });
  }

  rescale(id: number, newParallelism: number): Observable<HttpResponse<any>> {
    return this.http
      .get<any>(`${this.resourceUrl}/rescale?id=`+id+`&newParallelism=`+newParallelism, { observe: 'response' });
  }

  protected convertDateFromClient(job: IJob): IJob {
    const copy: IJob = Object.assign({}, job, {
      createdDate: job.createdDate != null && job.createdDate.isValid() ? job.createdDate.toJSON() : null,
      lastModifiedDate: job.lastModifiedDate != null && job.lastModifiedDate.isValid() ? job.lastModifiedDate.toJSON() : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate != null ? moment(res.body.createdDate) : null;
      res.body.lastModifiedDate = res.body.lastModifiedDate != null ? moment(res.body.lastModifiedDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((job: IJob) => {
        job.createdDate = job.createdDate != null ? moment(job.createdDate) : null;
        job.lastModifiedDate = job.lastModifiedDate != null ? moment(job.lastModifiedDate) : null;
      });
    }
    return res;
  }
}
