# job-sql.md
每个sql任务可以有多条sql语句，sql必须是insert into语句，例如：

```sql 
insert into 写入端名称 select * from 数据源名称（可以是视图）
```
![](/docs/media/15615355849207/15615357260682.jpg)

