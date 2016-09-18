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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.IOUtils;
import org.ovirt.vdsmfake.domain.BaseObject;
import org.ovirt.vdsmfake.domain.DataCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serialize objects from/to file cache.
 *
 *
 *
 */
public class PersistUtils {

    private static final Logger log = LoggerFactory.getLogger(PersistUtils.class);

    public static void store(BaseObject baseObject, File f) {
        if (baseObject instanceof DataCenter && baseObject.getName().contains("?")){
            baseObject.setName(baseObject.getId());
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(baseObject);
        } catch (Exception e) {
            log.error("Cannot save object", e);
            throw new RuntimeException("Cannot save object", e);
        } finally {
            IOUtils.closeQuietly(oos);
            baseObject.setLastUpdate(f.lastModified());
        }
    }

    public static Object load(File f) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(f));
            final BaseObject baseObject = (BaseObject) ois.readObject();
            baseObject.setLastUpdate(f.lastModified());
            return baseObject;
        } catch (Exception e) {
            log.error("Cannot save object", e);
            throw new RuntimeException("Cannot load object", e);
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }
}
