import { Moment } from 'moment';

export const enum UdfType {
  DEPENDENCY = 'DEPENDENCY',
  CODE = 'CODE'
}

export interface IUdf {
  id?: number;
  name?: string;
  type?: UdfType;
  value?: any;
  dependency?: string;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  remark?: string;
  businessId?: number;
}

export class Udf implements IUdf {
  constructor(
    public id?: number,
    public name?: string,
    public type?: UdfType,
    public value?: any,
    public dependency?: string,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public remark?: string,
    public businessId?: number
  ) {}
}
