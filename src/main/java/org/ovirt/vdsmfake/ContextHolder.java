/**
 Copyright (c) 2012 Red Hat, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package org.ovirt.vdsmfake;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
public class ContextHolder {

    private static ThreadLocal<Map<String, String>> TL = new ThreadLocal<Map<String, String>>();

    static final String KEY_SERVER_NAME = "ServerName";

    private ContextHolder() {
    }

    public static void init() {
        TL.set(new HashMap<String, String>());
    }

    public static void clear() {
        TL.remove();
    }

    static String get(String key) {
        return TL.get().get(key);
    }

    public static String set(String key, String value) {
        return TL.get().put(key, value);
    }

    public static String getServerName() {
        return get(KEY_SERVER_NAME);
    }

    public static String setServerName(String value) {
        return set(KEY_SERVER_NAME, value);
    }

}
