import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IJobSql } from 'app/shared/model/job-sql.model';

type EntityResponseType = HttpResponse<IJobSql>;
type EntityArrayResponseType = HttpResponse<IJobSql[]>;

@Injectable({ providedIn: 'root' })
export class JobSqlService {
  public resourceUrl = SERVER_API_URL + 'api/job-sqls';

  constructor(protected http: HttpClient) {}

  create(jobSql: IJobSql): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(jobSql);
    return this.http
      .post<IJobSql>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(jobSql: IJobSql): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(jobSql);
    return this.http
      .put<IJobSql>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IJobSql>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IJobSql[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(jobSql: IJobSql): IJobSql {
    const copy: IJobSql = Object.assign({}, jobSql, {
      createdDate: jobSql.createdDate != null && jobSql.createdDate.isValid() ? jobSql.createdDate.toJSON() : null,
      lastModifiedDate: jobSql.lastModifiedDate != null && jobSql.lastModifiedDate.isValid() ? jobSql.lastModifiedDate.toJSON() : null
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
      res.body.forEach((jobSql: IJobSql) => {
        jobSql.createdDate = jobSql.createdDate != null ? moment(jobSql.createdDate) : null;
        jobSql.lastModifiedDate = jobSql.lastModifiedDate != null ? moment(jobSql.lastModifiedDate) : null;
      });
    }
    return res;
  }
}
