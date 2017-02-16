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
package org.ovirt.vdsmfake.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.StringSerializer;
import org.apache.xmlrpc.serializer.TypeSerializer;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.HttpUtils;
import org.ovirt.vdsmfake.RequestData;
import org.ovirt.vdsmfake.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 *
 *         spec: http://xmlrpc.scripting.com/spec.html
 *         config: http://ws.apache.org/xmlrpc/advanced.html
 *         discussion: http://sourceforge.net/apps/phpbb/aria2/viewtopic.php?f=2&t=87&start=20
 *
 */
public class XmlRpcServlet extends org.apache.xmlrpc.webserver.XmlRpcServlet {

    private static final Logger log = LoggerFactory.getLogger(XmlRpcServlet.class);
    private static final Random RND = new Random(System.currentTimeMillis());

    @Override
    public void init(ServletConfig pConfig) throws ServletException {
        super.init(pConfig);
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException, ServletException {
        final AppConfig config = AppConfig.getInstance();

        if (config.isProxyActive()) {
            final RequestData requestData = new RequestData();
            final ResponseData responseData = new ResponseData();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();

            IOUtils.copy(pRequest.getInputStream(), bos);
            requestData.setXmlData(bos.toByteArray());

            // call remote server
            HttpUtils.forwardRequest(config.getTargetServerUrl(), requestData, responseData);

            if (responseData.getXmlData() != null) {
                IOUtils.write(responseData.getXmlData(), pResponse.getOutputStream());
            }
        } else {
            super.doPost(pRequest, pResponse);

            // wait if required in config
            if (config.getConstantDelay() > 0 || config.getRandomDelay() > 0) {
                try {
                    long ts = config.getConstantDelay();
                    if (config.getRandomDelay() > 0) {
                        ts += RND.nextInt((int) config.getRandomDelay());
                    }
                    log.info("Sleeping for {} ms", ts);
                    Thread.sleep(ts);

                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected XmlRpcServletServer newXmlRpcServer(ServletConfig pConfig) throws XmlRpcException {
        XmlRpcServletServer server = super.newXmlRpcServer(pConfig);
        server.setTypeFactory(new MyTypeFactoryImpl(server));

        return server;
    }

    static class MyStringSerializer extends StringSerializer {
        @Override
        public void write(ContentHandler pHandler, Object pObject) throws SAXException {
            // Write <string> tag explicitly
            write(pHandler, STRING_TAG, pObject.toString());
        }
    }

    static class MyTypeFactoryImpl extends TypeFactoryImpl {
        public MyTypeFactoryImpl(XmlRpcController pController) {
            super(pController);
        }

        @Override
        public TypeSerializer getSerializer(XmlRpcStreamConfig pConfig, Object pObject) throws SAXException {
            if (pObject instanceof String) {
                return new MyStringSerializer();
            } else {
                return super.getSerializer(pConfig, pObject);
            }
        }
    }
}
