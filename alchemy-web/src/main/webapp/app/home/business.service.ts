import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IBusiness } from 'app/shared/model/business.model';

type EntityResponseType = HttpResponse<IBusiness>;
type EntityArrayResponseType = HttpResponse<IBusiness[]>;

@Injectable({ providedIn: 'root' })
export class BusinessService {
  public resourceUrl = SERVER_API_URL + 'api/businesses';

  constructor(protected http: HttpClient) {}

  create(business: IBusiness): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(business);
    return this.http
      .post<IBusiness>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(business: IBusiness): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(business);
    return this.http
      .put<IBusiness>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBusiness>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBusiness[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(business: IBusiness): IBusiness {
    const copy: IBusiness = Object.assign({}, business, {
      createdDate: business.createdDate != null && business.createdDate.isValid() ? business.createdDate.toJSON() : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate != null ? moment(res.body.createdDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((business: IBusiness) => {
        business.createdDate = business.createdDate != null ? moment(business.createdDate) : null;
      });
    }
    return res;
  }
}
