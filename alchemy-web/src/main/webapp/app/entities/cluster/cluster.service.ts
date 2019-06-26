import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICluster } from 'app/shared/model/cluster.model';

type EntityResponseType = HttpResponse<ICluster>;
type EntityArrayResponseType = HttpResponse<ICluster[]>;

@Injectable({ providedIn: 'root' })
export class ClusterService {
  public resourceUrl = SERVER_API_URL + 'api/clusters';

  constructor(protected http: HttpClient) {}

  create(cluster: ICluster): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cluster);
    return this.http
      .post<ICluster>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(cluster: ICluster): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cluster);
    return this.http
      .put<ICluster>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICluster>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICluster[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }


  queryUrl(id: number): Observable<HttpResponse<any>>{
    return this.http
      .get<any>(`${this.resourceUrl}/web-url/${id}`, { observe: 'response' })
  }

  protected convertDateFromClient(cluster: ICluster): ICluster {
    const copy: ICluster = Object.assign({}, cluster, {
      createdDate: cluster.createdDate != null && cluster.createdDate.isValid() ? cluster.createdDate.toJSON() : null,
      lastModifiedDate: cluster.lastModifiedDate != null && cluster.lastModifiedDate.isValid() ? cluster.lastModifiedDate.toJSON() : null
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
      res.body.forEach((cluster: ICluster) => {
        cluster.createdDate = cluster.createdDate != null ? moment(cluster.createdDate) : null;
        cluster.lastModifiedDate = cluster.lastModifiedDate != null ? moment(cluster.lastModifiedDate) : null;
      });
    }
    return res;
  }
}
