import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IUdf } from 'app/shared/model/udf.model';

type EntityResponseType = HttpResponse<IUdf>;
type EntityArrayResponseType = HttpResponse<IUdf[]>;

@Injectable({ providedIn: 'root' })
export class UdfService {
  public resourceUrl = SERVER_API_URL + 'api/udfs';

  constructor(protected http: HttpClient) {}

  create(udf: IUdf): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(udf);
    return this.http
      .post<IUdf>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(udf: IUdf): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(udf);
    return this.http
      .put<IUdf>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IUdf>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IUdf[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(udf: IUdf): IUdf {
    const copy: IUdf = Object.assign({}, udf, {
      createdDate: udf.createdDate != null && udf.createdDate.isValid() ? udf.createdDate.toJSON() : null,
      lastModifiedDate: udf.lastModifiedDate != null && udf.lastModifiedDate.isValid() ? udf.lastModifiedDate.toJSON() : null
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
      res.body.forEach((udf: IUdf) => {
        udf.createdDate = udf.createdDate != null ? moment(udf.createdDate) : null;
        udf.lastModifiedDate = udf.lastModifiedDate != null ? moment(udf.lastModifiedDate) : null;
      });
    }
    return res;
  }
}
