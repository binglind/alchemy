import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ISink } from 'app/shared/model/sink.model';

type EntityResponseType = HttpResponse<ISink>;
type EntityArrayResponseType = HttpResponse<ISink[]>;

@Injectable({ providedIn: 'root' })
export class SinkService {
  public resourceUrl = SERVER_API_URL + 'api/sinks';

  constructor(protected http: HttpClient) {}

  create(sink: ISink): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sink);
    return this.http
      .post<ISink>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(sink: ISink): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sink);
    return this.http
      .put<ISink>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ISink>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISink[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(sink: ISink): ISink {
    const copy: ISink = Object.assign({}, sink, {
      createdDate: sink.createdDate != null && sink.createdDate.isValid() ? sink.createdDate.toJSON() : null,
      lastModifiedDate: sink.lastModifiedDate != null && sink.lastModifiedDate.isValid() ? sink.lastModifiedDate.toJSON() : null
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
      res.body.forEach((sink: ISink) => {
        sink.createdDate = sink.createdDate != null ? moment(sink.createdDate) : null;
        sink.lastModifiedDate = sink.lastModifiedDate != null ? moment(sink.lastModifiedDate) : null;
      });
    }
    return res;
  }
}
