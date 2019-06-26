import { Moment } from 'moment';
import { IJob } from 'app/shared/model/job.model';
import { ICluster } from 'app/shared/model/cluster.model';
import { ISource } from 'app/shared/model/source.model';
import { ISink } from 'app/shared/model/sink.model';
import { IUdf } from 'app/shared/model/udf.model';

export interface IBusiness {
  id?: number;
  name?: string;
  remark?: string;
  createdBy?: string;
  createdDate?: Moment;
  jobs?: IJob[];
  clusters?: ICluster[];
  sources?: ISource[];
  sinks?: ISink[];
  udfs?: IUdf[];
}

export class Business implements IBusiness {
  constructor(
    public id?: number,
    public name?: string,
    public remark?: string,
    public createdBy?: string,
    public createdDate?: Moment,
    public jobs?: IJob[],
    public clusters?: ICluster[],
    public sources?: ISource[],
    public sinks?: ISink[],
    public udfs?: IUdf[]
  ) {}
}
