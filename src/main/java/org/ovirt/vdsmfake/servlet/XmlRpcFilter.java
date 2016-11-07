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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.ContextHolder;
import org.ovirt.vdsmfake.XMLUtils;
import org.ovirt.vdsmfake.domain.VdsmManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class XmlRpcFilter implements Filter {

    static final String METHOD_CALL_NS = "api";

    private static final Logger log = LoggerFactory.getLogger(XmlRpcFilter.class);
    private static final Logger communicationLog = LoggerFactory.getLogger("org.ovirt.vdsmfake.communication");
    private static AtomicInteger logCounter = new AtomicInteger();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        try {
            final String method = ((HttpServletRequest)request).getMethod();
            final String newline = "\n";
            final String tabnewline = "\n\n\n\n";

            if ("GET".equals(method)) {
                if ("/hystrix.stream".equals(((HttpServletRequest) request).getServletPath())) {
                    chain.doFilter(request, response);
                    return;
                }
                StringBuilder output = new StringBuilder();
                output.append("Hello, this is VDSM Fake!").append(tabnewline);
                output.append(" ==== Statistics ======").append(newline);

                VdsmManager vdsmManager = VdsmManager.getInstance();
                output.append("running hosts: ").append(vdsmManager.getHostCount()).append(newline);
                output.append("running vms: ").append(vdsmManager.getRunningVmsCount()).append(newline);

                //print output
                response.setContentType("text/plain");
                response.getWriter().write(output.toString());
                return;
            }

            if (!"POST".equals(method)) {
                response.setContentType("text/plain");
                response.getWriter().write("Unsupported method: " + method);
                return;
            }

            ContextHolder.init();
            ContextHolder.setServerName(request.getServerName());

            final AppConfig conf = AppConfig.getInstance();

            final CustomHttpServletRequestWrapper wrapper =
                    new CustomHttpServletRequestWrapper((HttpServletRequest) request);

            final CustomHttpServletResponseWrapper responseWrapper =
                    new CustomHttpServletResponseWrapper((HttpServletResponse) response);

            // fix method call name - add namespace of method (required by Apache XMLRPC)
            final Document doc = XMLUtils.parseDocument(wrapper.getInputStream());
            final XPath xp = XPath.newInstance("/methodCall/methodName");
            final Element el = (Element) xp.selectSingleNode(doc);
            final String methodName = el.getText();

            final long tm = System.currentTimeMillis();

            // append namespace
            if (!conf.isProxyActive()) {
                el.setText(METHOD_CALL_NS + "." + el.getText());
            }

            // write back the new XML data
            wrapper.setInputStreamData(XMLUtils.serializeDocument(doc));

            log.info("[{}] {} starting...", new Object[] {request.getServerName(), methodName});

            chain.doFilter(wrapper, responseWrapper);

            // write communication into files
            if (conf.isLogDirSet() && conf.isMethodLoggingEnabled(methodName)) {
                int fIndex = logCounter.incrementAndGet();

                if (communicationLog.isDebugEnabled()) {
                    communicationLog.info(
                            (getPrefix(fIndex) + "_req_" + methodName), wrapper.getInputStreamData());
                    communicationLog.info(
                            (getPrefix(fIndex) + "_res_" + methodName), responseWrapper.getOutputStreamData());
                }
            }

            // write response
            responseWrapper.writeDataToStream();

            // log request info
            log.info("[{}] {} done. ({} ms, {} bytes)",
                    new Object[] {
                            request.getServerName(), methodName,
                            Long.valueOf(System.currentTimeMillis() - tm),
                            responseWrapper.getOutputStreamData().length});
        } catch (Exception e) {
            log.error("Cannot filter request", e);
            throw new ServletException("Error during request processing", e);
        } finally {
            ContextHolder.clear();
        }
    }

    private static String getPrefix(int num) {
        final StringBuilder b = new StringBuilder();
        final String numAsStr = Integer.toString(num);

        while (b.length() + numAsStr.length() <= 7) {
            b.append("0");
        }

        b.append(numAsStr);

        return b.toString();
    }

    @Override
    public void destroy() {
    }
}
