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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Utils {

    private static final Random RND = new Random();
    private static final String DATE_FORMAT = "yyy-MM-dd'T'HH:mm:ss z"; // 2013-02-10T19:09:11 GMT
    private static final char[] MAC_VALS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static boolean randomDelay;
    private static Long appConfigDelay;
    private static int appConfigDelay_asInit;
    private static int minimum = 100;

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    public static String getMacAddress() {
        // 80:C1:6E:6C:51:54
        StringBuilder b = new StringBuilder();
        for (int i = 1; i <= 17; i++) {
            if (i % 3 == 0) {
                b.append(":");
            } else {
                b.append(MAC_VALS[RND.nextInt(MAC_VALS.length)]);
            }
        }

        return b.toString();
    }

    public static String getDateTimeGMT() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    public static String getRandomNum(int length) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            b.append(RND.nextInt(10));
        }
        return b.toString();
    }

    public static String rangeParser(List<String> values){
        return String.valueOf(
                RND.nextInt(Integer.parseInt(values.get(1)) - Integer.parseInt(values.get(0)))
                        + Integer.parseInt(values.get(0)));
    }

    public static String getCpuIdle(String idle){
        return Integer.toString(100 - Integer.parseInt(idle));
    }


    public static String getRandomInRange(int high, int min){
        if (min == 0){
            min = 1;
        }
        return String.valueOf(RND.nextInt(high - min) + min);
    }

    public static String ipGenerator() {
        return RND.nextInt(256) + "." + RND.nextInt(256) + "." + RND.nextInt(256) + "." + RND.nextInt(256);
    }

    //Default
    public static void getLatency(){
        try{
            getLatency((long) minimum);
        }catch (Exception e){
            log.error("failed to get latency error is: {}", e);
        }
    }

    public static void getLatency(Long latency) {
        //run latency
        Long innerLatency;
        if (latency != (long) minimum) {
            innerLatency = latency;
        }
        else {
            innerLatency = getDelayFromXml();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(innerLatency);
        }catch (InterruptedException e){
            log.error("failed to to run getLatency error is:{}", e);
        }
    }


    public static long getDelayFromXml() {
        //getting delay from web.xml file
        long delay = (long) minimum;
        if (appConfigDelay != null) {
            try {
                if (randomDelay == true){
                    if (appConfigDelay_asInit <= 0) {
                        appConfigDelay_asInit = Integer.parseInt(String.valueOf(appConfigDelay));
                    }
                    delay =  Long.parseLong(getRandomInRange(minimum, appConfigDelay_asInit));
                }
                else {
                    delay = appConfigDelay;
                }
                return delay;
            }catch (NumberFormatException e) {
                log.error("failed to get delay from xml, make sure delay is in ms {}", e);
            }
        }
        else {
            //get delay from xml and cache it.
            AppConfig config =
                    CDI.current().select(AppConfig.class, AppLifecycleListener.DefaultLiteral.INSTANCE).get();
            //get minimum
            if (config.getDelayMinimum() > minimum){
                minimum = (int) config.getDelayMinimum();
            }
            //get maximum
            if (config.getConstantDelay() > 0) {
                appConfigDelay = config.getConstantDelay();
            } else if (config.getRandomDelay() > 0){
                appConfigDelay = config.getRandomDelay();
                randomDelay = true;
            }else{
                delay = (long) minimum;
            }
        }
        return delay;
    }



}
