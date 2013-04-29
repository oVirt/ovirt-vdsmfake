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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

/**
 * Enable to read input servlet stream more then once.
 *
 *
 *
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    byte[] inputStreamData;

    public byte[] getInputStreamData() {
        return inputStreamData;
    }

    public void setInputStreamData(byte[] inputStreamData) {
        this.inputStreamData = inputStreamData;
    }

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            IOUtils.copy(request.getInputStream(), bos);
            inputStreamData = bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get request input stream", e);
        }
    }

    @Override
    public ServletInputStream getInputStream () throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inputStreamData);

        ServletInputStream inputStream = new ServletInputStream() {
            @Override
            public int read () throws IOException {
                return byteArrayInputStream.read();
            }
        };

        return inputStream;
    }

}
