import { Moment } from 'moment';

export const enum SinkType {
  REDIS = 'REDIS',
  KAFKA = 'KAFKA010',
  MYSQL = 'MYSQL',
  HBASE = 'HBASE',
  TSDB = 'TSDB',
  FILE = 'FILE',
  ELASTICSEARCH5 = 'ELASTICSEARCH5',
  ELASTICSEARCH6 = 'ELASTICSEARCH6',
  DUBBO = 'DUBBO'
}

export interface ISink {
  id?: number;
  name?: string;
  type?: SinkType;
  config?: any;
  remark?: string;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  businessId?: number;
}

export class Sink implements ISink {
  constructor(
    public id?: number,
    public name?: string,
    public type?: SinkType,
    public config?: any,
    public remark?: string,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public businessId?: number
  ) {}
}
