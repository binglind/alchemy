package com.dfire.platform.alchemy.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceFileUtil {
	private static Logger logger = LoggerFactory.getLogger(SequenceFileUtil.class);
	private static Configuration configuration = new Configuration();
	
	public static void writeSequenceFile(String path) throws Exception{
		Writer.Option filePath = Writer.file(new Path(path));
		Writer.Option keyClass = Writer.keyClass(IntWritable.class);
		Writer.Option valueClass = Writer.valueClass(Text.class);
		Writer.Option compression = Writer.compression(CompressionType.NONE);
		Writer writer = SequenceFile.createWriter(configuration, filePath, keyClass, valueClass, compression);
		IntWritable key = new IntWritable();
		Text value = new Text("");
		for(int i=0;i<100;i++){
			key.set(i);
			value.set("value_"+i);
			writer.append(key, value);
		}
		writer.hflush();
		writer.close();
	}
	public static void readSequenceFile(String path) throws Exception{
		Reader.Option filePath = Reader.file(new Path(path));//指定文件路径
		Reader sqReader = new Reader(configuration,filePath);//构造read而对象
		
		Writable key = (Writable)ReflectionUtils.newInstance(sqReader.getKeyClass(), configuration);
		Writable value = (Writable)ReflectionUtils.newInstance(sqReader.getValueClass(), configuration);
		
		while(sqReader.next(key, value)){
			logger.info("key:"+key+",value:"+value);
		}
	}
	public static void main(String[] args) throws Exception {
		writeSequenceFile("/tmp/test.seq");
//		readSequenceFile("/tmp/test.seq");
	}
}
