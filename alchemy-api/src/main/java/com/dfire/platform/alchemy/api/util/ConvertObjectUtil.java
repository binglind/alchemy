package com.dfire.platform.alchemy.api.util;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author congbai
 * @date 2019/5/23
 */
public class ConvertObjectUtil {

    private static final ThreadLocal<SimpleDateFormat> UTIL_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> SQL_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> TIME_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss'Z'");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> TIME_FORMAT_WITH_MILLIS = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss.SSS'Z'");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> TIMESTAMP_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        }
    };

    public static Object transform(Object value, TypeInformation info) {
        if (info.equals( Types.VOID) || value == null) {
            return null;
        } else if (info.equals(Types.BOOLEAN)) {
            return getBoolean(value);
        } else if (info.equals(Types.STRING)) {
            return getString(value);
        } else if (info.equals(Types.FLOAT)) {
            return getFloat(value);
        } else if (info.equals(Types.INT)) {
            return getInteger(value);
        } else if (info.equals(Types.DOUBLE)) {
            return getDouble(value);
        } else if (info.equals(Types.BYTE)) {
            return getByte(value);
        } else if (info.equals(Types.LONG)) {
            return getLong(value);
        } else if (info.equals(Types.SHORT)) {
            return getShort(value);
        }  else if (info.equals(Types.BIG_DEC)) {
            return getBigDecimal(value);
        } else if (info.equals(Types.BIG_INT)) {
            return getBigInt(value);
        } else if (info.equals(Types.SQL_DATE)) {
           return getDate(value);
        } else if (info.equals(Types.SQL_TIME)) {
           return getTime(value);
        } else if (info.equals(Types.SQL_TIMESTAMP)) {
           return getTimestamp(value);
        }
        return value;
    }

    private static Time getTime(Object obj) {
        if (obj instanceof Time) {
            return ((Time) obj);
        } else if (obj instanceof Timestamp) {
            return new Time(((Timestamp) obj).getTime());
        } else if (obj instanceof String) {
            return Time.valueOf((String)obj);
        } else if (obj instanceof Long) {
            return new Time((Long) obj);
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Time.");
    }

    private static BigInteger getBigInt(Object obj) {
        if (obj instanceof BigInteger) {
            return (BigInteger) obj;
        }
        else if (obj instanceof String || obj instanceof Integer) {
            return new BigInteger(String.valueOf(obj));
        } else if (obj instanceof Long) {
            return BigInteger.valueOf((Long) obj);
        } else if (obj instanceof Double) {
            return BigInteger.valueOf(((Double) obj).longValue());
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toBigInteger();
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to BigInteger.");
    }

    private static Long getLong(Object obj) {
        if (obj instanceof String || obj instanceof Integer) {
            return Long.valueOf(String.valueOf(obj));
        } else if (obj instanceof Long || obj instanceof Double) {
            return (Long) obj;
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Long.");
    }

    private static Integer getInteger(Object obj) {
        if (obj instanceof String) {
            return Integer.valueOf(String.valueOf(obj));
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) obj).intValue();
        }

        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Integer.");
    }

    private static Float getFloat(Object obj) {
        if (obj instanceof String) {
            return Float.valueOf(String.valueOf(obj));
        } else if (obj instanceof Float) {
            return (Float) obj;
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).floatValue();
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Float.");
    }


    private static Double getDouble(Object obj) {
        if (obj instanceof String) {
            return Double.parseDouble(String.valueOf(obj));
        } else if (obj instanceof Float) {
            return Double.parseDouble(String.valueOf(obj));
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).doubleValue();
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Double.");
    }


    private static Boolean getBoolean(Object obj) {
        if (obj instanceof String) {
            return Boolean.valueOf(String.valueOf(obj));
        } else if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Boolean.");
    }

    private static String getString(Object obj) {
        if(obj instanceof java.util.Date){
            return UTIL_DATE_FORMAT.get().format((java.util.Date)obj);
        }else if(obj instanceof  Date){
            return SQL_DATE_FORMAT.get().format((Date)obj);
        }else if(obj instanceof Time){
            Time time  = (Time) obj;
            if (time.getTime() % 1000 > 0) {
                return TIME_FORMAT_WITH_MILLIS.get().format(time);
            }
            return TIME_FORMAT.get().format(time);
        }else if(obj instanceof Timestamp){
            return TIMESTAMP_FORMAT.get().format((Timestamp)obj);
        }else{
            return String.valueOf(obj);
        }

    }

    private static Byte getByte(Object obj) {
        if (obj instanceof String) {
            return Byte.valueOf(String.valueOf(obj));
        } else if (obj instanceof Byte) {
            return (Byte) obj;
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Byte.");
    }

    private static Short getShort(Object obj) {
        if (obj instanceof String) {
            return Short.valueOf(String.valueOf(obj));
        } else if (obj instanceof Short) {
            return (Short) obj;
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Short.");
    }

    private static BigDecimal getBigDecimal(Object obj) {
        if (obj instanceof String) {
            return new BigDecimal(String.valueOf(obj));
        } else if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        } else if (obj instanceof BigInteger) {
            return new BigDecimal((BigInteger) obj);
        } else if (obj instanceof Number) {
            return new BigDecimal(((Number) obj).doubleValue());
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to BigDecimal.");
    }

    private static Date getDate(Object obj) {
        if (obj instanceof String) {
            return Date.valueOf((String)obj);
        } else if (obj instanceof Timestamp) {
            return new Date(((Timestamp) obj).getTime());
        } else if (obj instanceof Date) {
            return (Date) obj;
        } else if (obj instanceof Long) {
            return new Date((Long) obj);
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Date.");
    }

    private static Timestamp getTimestamp(Object obj) {
        if (obj instanceof Timestamp) {
            return (Timestamp) obj;
        } else if (obj instanceof Date) {
            return new Timestamp(((Date) obj).getTime());
        } else if (obj instanceof String) {
            return Timestamp.valueOf((String)obj);
        } else if (obj instanceof Long) {
            return new Timestamp((Long) obj);
        }
        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Timestamp.");
    }
}
