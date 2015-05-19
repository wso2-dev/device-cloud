/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.arduino.firealarm.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;

import java.io.*;
import java.util.Map;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Contains utility methods used by Arduino plugin.
 */
public class FireAlarmUtils {

    private static Log log = LogFactory.getLog(FireAlarmUtils.class);

    public static String getDeviceProperty(Map<String, String> deviceProperties, String property) {

        String deviceProperty = deviceProperties.get(property);

        if (deviceProperty == null) {
            return "";
        }

        return deviceProperty;
    }

    /**
     * Creates and returns a zip archive given a source folder.
     * zip archive will be created on the same source folder.
     *
     * @param srcFolder source path
     * @return  zip archive file
     * @throws DeviceManagementException
     */
    public static synchronized File createZipArchive(String srcFolder) throws DeviceManagementException{
        try {
            String zipArchivePath = srcFolder + ".zip";
            removeFileIfExists(zipArchivePath);

            final int BUFFER = 2048;
            BufferedInputStream origin = null;

            FileOutputStream dest = new FileOutputStream(new File(zipArchivePath));

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            File sourceFile = new File(srcFolder);
            String subdirList[] = sourceFile.list();
            for (String subDir : subdirList) {
                // get a list of files from current directory
                File f = new File(srcFolder + "/" + subDir);
                if (f.isDirectory()) {
                    String files[] = f.list();

                    for (int i = 0; i < files.length; i++) {
                        log.info("Adding: " + files[i]);
                        FileInputStream fi = new FileInputStream(srcFolder + "/" + subDir + "/" + files[i]);
                        origin = new BufferedInputStream(fi, BUFFER);
                        ZipEntry entry = new ZipEntry(subDir + "/" + files[i]);
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, BUFFER)) != -1) {
                            out.write(data, 0, count);
                            out.flush();
                        }
                    }

                } else { //it is just a file
                    FileInputStream fi = new FileInputStream(f);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(subDir);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                        out.flush();
                    }

                }
            }
            if(origin!=null){origin.close();}
            out.flush();
            out.close();
            return  new File(zipArchivePath);
        } catch (FileNotFoundException e) {
            String msg = "File not found error occurred while creating zip archive for`"+ srcFolder +"`";
            log.error(msg);
            throw new DeviceManagementException(msg, e);
        } catch (IOException e) {
            String msg = "I/O error occurred while creating zip archive for`"+ srcFolder +"`";
            log.error(msg);
            throw new DeviceManagementException(msg, e);
        }
    }

    /**
     * Deletes a given file.
     * @param sourceFile file to be removed
     * @return True if file removed
     */
    private static boolean removeFileIfExists(String sourceFile){
        File f = new File(sourceFile);

        try {
            return f.delete();
        }catch(SecurityException e){
            String msg = "Permission error while deleting zip archive `"+ sourceFile +"`";
            log.error(msg);
            //doesn't raise the error
        }

        return false;
    }
}
