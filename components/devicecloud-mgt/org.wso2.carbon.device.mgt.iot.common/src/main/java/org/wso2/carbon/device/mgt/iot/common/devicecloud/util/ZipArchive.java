package org.wso2.carbon.device.mgt.iot.common.devicecloud.util;

import java.io.File;

public class ZipArchive {
	private File zipFile = null;
	private String fileName = null;

	public ZipArchive(String fileName, File zipFile) {
		this.fileName = fileName;
		this.zipFile = zipFile;
	}

	public File getZipFile() {
		return zipFile;
	}

	public String getFileName() {
		return fileName;
	}
}
