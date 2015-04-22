/*
* Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.iot.fileloader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.wso2.iot.datasource.DBUtils;


public class ResourceFileLoader {
	private File file;
	public ResourceFileLoader(String fileName){
		String path =  DBUtils.class.getClassLoader().getResource("").getPath();
		String fullPath = null;
		try {
			fullPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
		}
		String pathArr[] = fullPath.split("/WEB-INF/classes/");
		String filePath = pathArr[0] + fileName;

		file = new File(filePath);
		
	}
	
	public File getFile(){
		
		return file;
	}

}
