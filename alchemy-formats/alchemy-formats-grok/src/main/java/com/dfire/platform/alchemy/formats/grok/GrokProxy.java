
package com.dfire.platform.alchemy.formats.grok;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/****
 * grok {
 match => ["message", "\[%{DATA:syslog_host}\] \[%{DATA:syslog_tag}\] %{DATA:am_datetime} %{LOGLEVEL:am_level}%{SPACE}%{DATA:am_class}(?: traceId\:%{DATA:traceId}|)(?: spandId\:%{DATA:spandId}|)(?: parentId\:%{DATA:parentId}|) (?:%{DATA:am_marker}([。|：|；])|)%{SPACE}(?:json\:%{SPACE}%{GREEDYDATA:am_json}|%{GREEDYDATA:am_msg})(?:exception_msg\:%{GREEDYDATA:exception}|)"]
 }
 */
public class GrokProxy {

    private static final Logger logger = LoggerFactory.getLogger(GrokProxy.class);

    private static ConcurrentHashMap<String, Grok> context = new ConcurrentHashMap<>();

    private static GrokProxy INSTANCE = new GrokProxy();

    private GrokCompiler grokCompiler;

    private GrokProxy() {
        grokCompiler = GrokCompiler.newInstance();
        // 进行注册, registerDefaultPatterns()方法注册的是Grok内置的patterns
        grokCompiler.registerDefaultPatterns();
    }

    public static GrokProxy getInstance() {
        return INSTANCE;
    }

    public Map<String, Object> match(String message, String pattern) {
        Grok grok = context.get(pattern);
        if (grok == null) {
            grok = grokCompiler.compile(pattern);
            context.putIfAbsent(pattern, grok);
        }
        Map<String, Object> resultMap = null;
        try {
            Match grokMatch = grok.match(message);
            resultMap = grokMatch.capture();
        } catch (Exception e) {
            logger.error("grok fail", e);
        }
        return resultMap;
    }
}
