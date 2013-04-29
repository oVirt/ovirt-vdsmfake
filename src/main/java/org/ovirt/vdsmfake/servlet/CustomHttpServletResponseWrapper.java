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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 *
 *
 */
public class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper {

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final ServletOutputStream servletOutputStream;

    public CustomHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        try {
            servletOutputStream = response.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException("Cannot get output stream", e);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        final ServletOutputStream fakeOutputStream =
                new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        bos.write(b);
                    }
                };

        return fakeOutputStream;
    }

    public void writeDataToStream() {
        try {
            IOUtils.copy(new ByteArrayInputStream(bos.toByteArray()), servletOutputStream);
        } catch (Exception e) {
            throw new RuntimeException("Cannot copy data", e);
        }
    }

    public byte[] getOutputStreamData() {
        return bos.toByteArray();
    }
}
