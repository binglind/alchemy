import { Moment } from 'moment';

export const enum JobConfType {
  BASIC = 'BASIC',
  SQL = 'SQL'
}

export const enum Valid {
  INVALID = 'INVALID',
  VALID = 'VALID'
}

export interface IJobConf {
  id?: number;
  confType?: JobConfType;
  config?: string;
  valid?: Valid;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  jobId?: number;
}

export class JobConf implements IJobConf {
  constructor(
    public id?: number,
    public confType?: JobConfType,
    public config?: string,
    public valid?: Valid,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public jobId?: number
  ) {}
}
