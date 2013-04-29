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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 *
 */
public class HttpUtils {

    static final String CONTENT_TYPE = "Content-Type";
    static final String CONTENT_DISPOSITION = "Content-Disposition";
    static final int CONNECTION_TIMEOUT = 30000;
    static final int SO_TIMEOUT = 300000;

    final static HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());

    static {
        client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
        client.getHttpConnectionManager().getParams().setSoTimeout(SO_TIMEOUT);
        client.getHttpConnectionManager().getParams().setMaxTotalConnections(30);
    }

    public static void forwardRequest(String targetServer, RequestData requestData, ResponseData responseData) {
        final PostMethod postMethod = new PostMethod(targetServer);

        try {
            // prepare request
            // postMethod.setRequestHeader(CONTENT_TYPE, requestData.getContentType());
            postMethod.setRequestHeader(CONTENT_TYPE, "text/xml");
            postMethod.setRequestEntity(new ByteArrayRequestEntity(requestData.getXmlData()));

            // call server
            final int result = client.executeMethod(postMethod);

            final byte[] resultBody = postMethod.getResponseBody();

            // process results
            responseData.setSuccess(result == HttpStatus.SC_OK && resultBody != null);
            responseData.setXmlData(resultBody);
        } catch (Exception e) {
            throw new RuntimeException("Cannot forward request", e);
        } finally {
            try {
                postMethod.releaseConnection();
            } catch (Exception e2) {
            }
        }
    }
}
