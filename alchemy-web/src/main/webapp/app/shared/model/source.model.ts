import { Moment } from 'moment';

export const enum TableType {
  TABLE = 'TABLE',
  VIEW = 'VIEW',
  SIDE = 'SIDE'
}

export const enum SourceType {
  KAFKA = 'KAFKA010',
  MYSQL = 'MYSQL',
  CSV = 'CSV'
}

export interface ISource {
  id?: number;
  name?: string;
  tableType?: TableType;
  sourceType?: SourceType;
  config?: any;
  remark?: string;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  businessId?: number;
}

export class Source implements ISource {
  constructor(
    public id?: number,
    public name?: string,
    public tableType?: TableType,
    public sourceType?: SourceType,
    public config?: any,
    public remark?: string,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public businessId?: number
  ) {}
}
