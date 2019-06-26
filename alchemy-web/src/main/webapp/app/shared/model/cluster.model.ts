import { Moment } from 'moment';
import { IJob } from 'app/shared/model/job.model';

export const enum ClusterType {
  STANDALONE = 'STANDALONE',
  YARN = 'YARN'
}

export interface ICluster {
  id?: number;
  name?: string;
  type?: ClusterType;
  config?: any;
  remark?: string;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  businessId?: number;
  jobs?: IJob[];
}

export class Cluster implements ICluster {
  constructor(
    public id?: number,
    public name?: string,
    public type?: ClusterType,
    public config?: any,
    public remark?: string,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public businessId?: number,
    public jobs?: IJob[]
  ) {}
}
