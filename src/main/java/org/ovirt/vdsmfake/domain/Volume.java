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
package org.ovirt.vdsmfake.domain;

/**
 *
 *
 */
public class Volume extends BaseObject {

    /**
     *
     */
    private static final long serialVersionUID = 7419502790269158359L;

    // String sdUUID,
    // String spUUID,
    // String volUUID; ~ id
    String imgUUID;
    String size;
    Integer volFormat;
    Integer preallocate;
    Integer diskType;
    String desc;
    String srcImgUUID;
    String srcVolUUID;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getVolFormat() {
        return volFormat;
    }

    public void setVolFormat(Integer volFormat) {
        this.volFormat = volFormat;
    }

    public Integer getPreallocate() {
        return preallocate;
    }

    public void setPreallocate(Integer preallocate) {
        this.preallocate = preallocate;
    }

    public Integer getDiskType() {
        return diskType;
    }

    public void setDiskType(Integer diskType) {
        this.diskType = diskType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSrcImgUUID() {
        return srcImgUUID;
    }

    public void setSrcImgUUID(String srcImgUUID) {
        this.srcImgUUID = srcImgUUID;
    }

    public String getSrcVolUUID() {
        return srcVolUUID;
    }

    public void setSrcVolUUID(String srcVolUUID) {
        this.srcVolUUID = srcVolUUID;
    }

    public String getImgUUID() {
        return imgUUID;
    }

    public void setImgUUID(String imgUUID) {
        this.imgUUID = imgUUID;
    }

}
