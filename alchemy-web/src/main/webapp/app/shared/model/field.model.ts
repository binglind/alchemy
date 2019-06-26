import { Moment } from 'moment';

export interface IField {
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

export class Field implements IField {
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
