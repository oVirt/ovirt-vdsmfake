package org.ovirt.vdsmfake.service;

import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.domain.VM;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;

import java.io.StringReader;
import java.util.*;

/**
 * Author: Vinzenz Feenstra
 * Date: 2013-08-22
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class VMInfoService extends AbstractService {
    final static VMInfoService instance = new VMInfoService();

    private Map values = map();
    private Map<String, Long> randomValueTimeouts = new HashMap<String, Long>();

    public static VMInfoService getInstance() {
        return instance;
    }

    public VMInfoService() {
        // vmConfAndStatsConstants
        LoggerFactory.getLogger(VMInfoService.class).info("Logging vmConfAndStatsConstants: ", AppConfig.getInstance().getVmConfAndStatsConstants());
        Yaml y = new Yaml();
        values = (Map)y.load(AppConfig.getInstance().getVmConfAndStatsConstants());
        for( Object e: values.keySet() ){
            if(values.get(e) == null) {
                values.put(e, "");
            }
        }
        LinkedHashMap timing = (LinkedHashMap)y.load(AppConfig.getInstance().getVmConfAndStatsUpdateIntervals());
        for( Object e: timing.keySet() ){
            randomValueTimeouts.put((String) e, (long)(((Integer)timing.get(e)) * 1000));
        }
    }

    private long getRandomNumberTimeout(String name)
    {
        if(!randomValueTimeouts.containsKey(name))
        {
            return 0;
        }
        return randomValueTimeouts.get(name);
    }

    private long getNow()
    {
        return new Date().getTime();
    }

    public String getRandomNumber(VM vm, String name, int length)
    {
        long last = 0;
        if(vm.getLastRandomNumberUpdate().containsKey(name))
        {
            last = vm.getLastRandomNumberUpdate().get(name);
        }
        // If never set/updated or if bigger than configured timeout
        // update the last update time and update the value
        final long now = getNow();
        if(last == 0 || (now - last) > getRandomNumberTimeout(name))
        {
            vm.getLastRandomNumberUpdate().put(name, getNow());
            vm.getRandomNumberStore().put(name, getRandomNum(length));
        }

        return vm.getRandomNumberStore().get(name);
    }

    private Map getDynamicValues(VM vm) {
        Map values = map();
        values.put("appsList",              lst());
        values.put("cpuSys",                "0." + getRandomNumber(vm, "cpuSys", 2));
        values.put("cpuUser",               "0." + getRandomNumber(vm, "cpuUser", 2));
        values.put("hash",                  getRandomNumber(vm, "hash", 20)); // 3077163634575265748
        return values;
    }

    public Map getFromKeys(VM vm, List keys) {
        Map result = map();
        final Map dynamic = getDynamicValues(vm);
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
