package org.ovirt.vdsmfake.service;

import org.ovirt.vdsmfake.AppConfig;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Author: Vinzenz Feenstra
 * Date: 2013-08-22
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class VMInfoService extends AbstractService {
    final static VMInfoService instance = new VMInfoService();

    final private Map values = map();

    public static VMInfoService getInstance() {
        return instance;
    }

    public VMInfoService() {
        // vmConfAndStatsConstants
        LoggerFactory.getLogger(VMInfoService.class).info("Logging vmConfAndStatsConstants: ", AppConfig.getInstance().getVmConfAndStatsConstants());
        for(String value : AppConfig.getInstance().getVmConfAndStatsConstants().split("\n")){
            String[] keyValuePair = value.trim().split("=", 2);
            if(keyValuePair.length == 2) {
                values.put(keyValuePair[0], keyValuePair[1]);
            }
            else if(keyValuePair.length == 1) {
                values.put(keyValuePair[0], "");
            }
        }
    }

    private Map getDynamicValues() {
        Map values = map();
        Map balloonMap = map();
        balloonMap.put("balloon_max",       Integer.valueOf(524288));
        balloonMap.put("balloon_cur",       Integer.valueOf(524288));
        values.put("balloonInfo",           balloonMap);
        values.put("appsList",              lst());
        values.put("cpuSys",                "0." + getRandomNum(2));
        values.put("cpuUser",               "0." + getRandomNum(2));
        values.put("hash",                  getRandomNum(20)); // 3077163634575265748
        return values;
    }
    public Map getFromKeys(List keys) {
        Map result = map();
        final Map dynamic = getDynamicValues();
        for( Object key : keys ) {
            if( values.containsKey(key) ) {
                result.put(key, values.get(key));
            }
            else if (dynamic.containsKey(key)) {
                result.put(key, dynamic.get(key));
            }
        }
        return result;
    }
}
