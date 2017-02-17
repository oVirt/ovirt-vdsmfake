package org.ovirt.vdsmfake.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ResultCodes {
    OK("OK", 0),
    DONE("done", 0),
    MACHINE_DESTROYED("Machine destroyed", 0),
    MACHINE_SHUTDOWN("Machine shutdown", 0),
    MIGRATION_STARTING("Starting Migration", 0),
    VM_NOT_FOUND("VM not found", 100),
    UNSUPPORTED("UNsupported command", 100);

    private Map<String, Object> mapValue;

    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";

    ResultCodes(String message, int code) {
        Map<String, Object>  inner = new HashMap<>(2);
        inner.put(CODE, code);
        inner.put(MESSAGE, message);

        Map<String, Object>  outer = new HashMap<>(1);
        outer.put(STATUS, inner);
        mapValue = Collections.unmodifiableMap(outer);
    }

    public Map<String, Object>  map() {
        return mapValue;
    }

    public Map<String, Object>  newMap() {
        return new HashMap<>(mapValue);
    }
}
