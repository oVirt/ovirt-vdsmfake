package org.ovirt.vdsmfake.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ovirt.vdsmfake.domain.VM;
import org.yaml.snakeyaml.Yaml;

/**
 * Author: Vinzenz Feenstra
 * Date: 2013-08-22
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class VMInfoService extends AbstractService {

    private static final VMInfoService INSTANCE = new VMInfoService();

    private Map<String, Object> values = map();
    private Map<String, Long> randomValueTimeouts = new HashMap<>();

    public static VMInfoService getInstance() {
        return INSTANCE;
    }

    public VMInfoService() {
        Yaml y = new Yaml();
        Map<String, Object> m =
                (Map<String, Object>) y.load(VMInfoService.class.getResourceAsStream("VMInfoService.yaml"));
        values = (Map<String, Object>) m.get("value-constants");
        values.forEach((k, v) -> values.computeIfAbsent(k, t ->  ""));
        LinkedHashMap<String, Integer> timing = (LinkedHashMap) m.get("update-intervals");
        timing.forEach((k, v) -> randomValueTimeouts.put(k, v * 1000L));
    }

    private long getRandomNumberTimeout(String name) {
        if (!randomValueTimeouts.containsKey(name)) {
            return 0;
        }
        return randomValueTimeouts.get(name);
    }

    private long getNow() {
        return new Date().getTime();
    }

    public String getRandomNumber(VM vm, String name, int length) {
        long last = 0;
        if (vm.getLastRandomNumberUpdate().containsKey(name)) {
            last = vm.getLastRandomNumberUpdate().get(name);
        }
        // If never set/updated or if bigger than configured timeout
        // update the last update time and update the value
        final long now = getNow();
        if (last == 0 || (now - last) > getRandomNumberTimeout(name)) {
            vm.getLastRandomNumberUpdate().put(name, getNow());
            vm.getRandomNumberStore().put(name, getRandomNum(length));
        }

        return vm.getRandomNumberStore().get(name);
    }

    private Map getDynamicValues(VM vm) {
        Map values = map();
        values.put("cpuSys", "0." + getRandomNumber(vm, "cpuSys", 2));
        values.put("cpuUser", "0." + getRandomNumber(vm, "cpuUser", 2));
        values.put("hash", getRandomNumber(vm, "hash", 20)); // 3077163634575265748
        return values;
    }

    public Map getFromKeys(VM vm, List keys) {
        Map result = map();
        final Map dynamic = getDynamicValues(vm);
        for (Object key : keys) {
            if (values.containsKey(key)) {
                result.put(key, values.get(key));
            } else if (dynamic.containsKey(key)) {
                result.put(key, dynamic.get(key));
            }
        }
        return result;
    }
}
