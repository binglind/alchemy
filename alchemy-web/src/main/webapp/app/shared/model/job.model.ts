import { Moment } from 'moment';
import { IJobSql } from 'app/shared/model/job-sql.model';

export const enum JobType {
  JAR = 'JAR',
  SQL = 'SQL'
}

export const enum JobStatus {
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  SUBMIT = 'SUBMIT',
  RUNNING = 'RUNNING',
  FAILED = 'FAILED',
  CANCELED = 'CANCELED',
  FINISHED = 'FINISHED'
}

export interface IJob {
  id?: number;
  name?: string;
  type?: JobType;
  config?: any;
  remark?: string;
  clusterJobId?: string;
  status?: JobStatus;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  businessId?: number;
  clusterId?: number;
  sqls?: IJobSql[];
}

export class Job implements IJob {
  constructor(
    public id?: number,
    public name?: string,
    public type?: JobType,
    public config?: any,
    public remark?: string,
    public clusterJobId?: string,
    public status?: JobStatus,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public businessId?: number,
    public clusterId?: number,
    public sqls?: IJobSql[]
  ) {}
}
