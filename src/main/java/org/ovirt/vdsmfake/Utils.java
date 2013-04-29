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
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

/**
 *
 *
 */
public class Utils {

    final static Random RND = new Random();
    final static String DATE_FORMAT = "yyy-MM-dd'T'HH:mm:ss z"; // 2013-02-10T19:09:11 GMT
    final static char[] MAC_VALS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private Utils() {
    }

    public static String getExceptionText(Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        new Exception().printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    public static String getIpAddress() {
        final String ip = "10.34." + getRandIntegerAsString(1, 253) + "." + getRandIntegerAsString(1, 253);
        return ip;
    }

    public static String getMacAddress() {
        // 80:C1:6E:6C:51:54
        final StringBuilder b = new StringBuilder();
        for(int i=1; i <= 17;i++) {
            if(i % 3 == 0) {
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
        final StringBuilder b = new StringBuilder();
        for (int i=0;i < length;i++) {
            b.append(RND.nextInt(10));
        }
        return b.toString();
    }



}
