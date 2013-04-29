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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * XML request and fake response processing routines
 *
 *
 *
 */
public class XMLUtils {

    private XMLUtils() {
    }

    public static Document parseDocument(InputStream is) {
        try {
            final SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
            final Document doc = saxBuilder.build(is);
            return doc;
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse document", e);
        }
    }

    public static byte[] serializeDocument(Document doc) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // serialize XML
        serializeXML(bos, doc);
        return bos.toByteArray();
    }

    public static void serializeXML(OutputStream os, Document doc) {
        OutputStreamWriter osw = null;

        try {
            osw = new OutputStreamWriter(os, "UTF-8");
            final XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
            outputter.output(doc, osw);
        } catch (Exception e) {
            throw new RuntimeException("Cannot write file", e);
        } finally {
            IOUtils.closeQuietly(osw);
        }
    }

    public static Document parseResource(String resourcePath) {
        InputStream is = null;

        try {
            is = XMLUtils.class.getResourceAsStream(resourcePath);
            if(is == null) {
                throw new RuntimeException("Cannot get resource: " + resourcePath);
            }

            final Document doc = parseDocument(is);
            return doc;
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse document", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

}
