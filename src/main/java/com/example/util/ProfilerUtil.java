package com.example.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;

/**
 * 打印日志 方法执行时间
 **/
public class ProfilerUtil {
    private static long MAX_SIZE = 10000;
    private static Logger logger = LoggerFactory.getLogger(ProfilerUtil.class);

    private static ThreadLocal<HashMap<String, TimeUse>> threadLocal = new ThreadLocal();

    /**
     * start time record
     *
     * @param method
     */
    public static void start(String method) {
        if (StringUtils.isBlank(method)) {
            logger.warn("Has not method, can not start!!!");
            return;
        }
        //key method, value time
        HashMap<String, TimeUse> timeMap = threadLocal.get();
        if (Objects.isNull(timeMap)) {
            timeMap = new HashMap<>(10);
        }
        //protested oom
        if (timeMap.size() > MAX_SIZE) {
            logger.warn("Over max size :【{}】 of profiler to analysis log, will not record for method:【{}】", MAX_SIZE, method);
            return;
        }
        TimeUse timeUse = new TimeUse(System.currentTimeMillis());
        timeMap.put(method, timeUse);
        threadLocal.set(timeMap);
    }

    /**
     * end time record
     *
     * @param method
     */
    public static void end(String method) {
        if (StringUtils.isBlank(method)) {
            logger.warn("Has not method, can not end!!!");
            return;
        }
        //key method, value time
        HashMap<String, TimeUse> timeMap = threadLocal.get();
        if (Objects.isNull(timeMap) || !timeMap.containsKey(method)) {
            logger.warn("Has not timeMap or have not method in cache, can not end!!!");
            return;
        }
        timeMap.get(method);
        TimeUse timeUse = timeMap.get(method);
        timeUse.setEnd(System.currentTimeMillis());
        logger.info("【{}】 use time:【{}】ms", method, timeUse.getEnd() - timeUse.getStart());
        timeMap.remove(method);
    }

    /**
     * clear time use
     */
    public static void clear() {
        threadLocal.remove();
    }

    /**
     * inner class to record time use
     */
    private static class TimeUse {
        private Long start;
        private Long end;

        TimeUse(Long start) {
            this.start = start;
        }

        public void setStart(Long start) {
            this.start = start;
        }

        public Long getStart() {
            return start;
        }

        public void setEnd(Long end) {
            this.end = end;
        }

        public Long getEnd() {
            return end;
        }
    }
}
