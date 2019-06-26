import { Moment } from 'moment';

export interface ISchema {
  id?: number;
  columnName?: string;
  columnType?: string;
  config?: string;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  sourceId?: number;
}

export class Schema implements ISchema {
  constructor(
    public id?: number,
    public columnName?: string,
    public columnType?: string,
    public config?: string,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public sourceId?: number
  ) {}
}
