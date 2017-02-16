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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Utils {

    final static Random RND = new Random();
    final static String DATE_FORMAT = "yyy-MM-dd'T'HH:mm:ss z"; // 2013-02-10T19:09:11 GMT
    final static char[] MAC_VALS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static boolean randomDelay=false;
    private static Long appConfigDelay;
    private static int appConfigDelay_asInit=0;
    private static int minimum = 100;

    private static final Logger log = LoggerFactory.getLogger(Utils.class);


    public static String getExceptionText(Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        new Exception().printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String getUuid() {
        return UUID.randomUUID().toString();
    }


    public static String getMacAddress() {
        // 80:C1:6E:6C:51:54
        final StringBuilder b = new StringBuilder();
        for (int i = 1; i <= 17; i++) {
            if (i % 3 == 0) {
                b.append(":");
            } else {
                b.append(MAC_VALS[RND.nextInt(MAC_VALS.length)]);
            }
        }

        return b.toString();
    }

    public String getNetworkBridgeName() {
        return AppConfig.getInstance().getNetworkBridgeName();
    }

    public static String getDateTimeGMT() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    public static String getRandIntegerAsString(int from, int to) {
        return Integer.valueOf(from + RND.nextInt(to - from)).toString();
    }

    public static String getRandomNum(int length) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            b.append(RND.nextInt(10));
        }
        return b.toString();
    }

    public static String rangeParsser(List<String> values){
        return String.valueOf(RND.nextInt(Integer.valueOf(values.get(1).toString()) - Integer.valueOf(values.get(0).toString())) + Integer.valueOf(
                values.get(0).toString()));
    }

    public static String getCpuIdle(String idle){
        return String.valueOf(100 - Integer.valueOf(idle));
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
            AppConfig config = AppConfig.getInstance();
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

    public static int getInt(String val) {
        if (val == null || val.trim().length() == 0) {
            return 0;
        }

        try {
            return Integer.valueOf(val);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getLong(String val) {
        if (val == null || val.trim().length() == 0) {
            return 0;
        }

        try {
            return Long.valueOf(val);
        } catch (Exception e) {
            return 0;
        }
    }

    public static ArrayList<String> splitString(String s){
        return new ArrayList(Arrays.asList(s.split(",")));
    }

    public static String ipv4toIpv6(String ip){
        String[] octets = ip.split("\\.");
        byte[] octetBytes = new byte[4];
        for (int i = 0; i < 4; ++i) {
            octetBytes[i] = (byte) Integer.parseInt(octets[i]);
        }

        byte ipv4asIpV6addr[] = new byte[16];
        ipv4asIpV6addr[10] = (byte)0xff;
        ipv4asIpV6addr[11] = (byte)0xff;
        ipv4asIpV6addr[12] = octetBytes[0];
        ipv4asIpV6addr[13] = octetBytes[1];
        ipv4asIpV6addr[14] = octetBytes[2];
        ipv4asIpV6addr[15] = octetBytes[3];

        return Arrays.toString(ipv4asIpV6addr);
    }
}
