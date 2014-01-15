/*
 *  Copyright 2013 AT&T
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.att.aro.model;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PcapHelper {
	ExternalProcessRunner runner;
	private static final Logger logger = Logger.getLogger(PcapHelper.class.getName());
	
	public PcapHelper(){
		runner = new ExternalProcessRunner();
	}
	public PcapHelper(ExternalProcessRunner runner){
		this.runner = runner;
	}
	/**
	 * extract date of the first packet received in the pcap/cap file. For Mac OS only.
	 * @param filepath full path to the packet file pcap/cap
	 * @return instance of Date if availale or null
	 */
	public Date getFirstPacketDate(String filepath){
		String cmd = "tcpdump -tt -r "+filepath;
		String data = "";
		try {
			data = runner.runCmd(new String[]{"bash","-c",cmd});
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
		if(data.length() > 1){
			String dtstring = getFirstPacketTime(data);
			if(dtstring.length() > 0){
				logger.info("found packet date string: "+dtstring);
				double d = Double.parseDouble(dtstring);
				long tick = (long)(d*1000);
				Date dt = new Date(tick);
				
				return dt;
			}
		}
		return null;
	}
	private String getFirstPacketTime(String data){
		String sub = "";
		Pattern pattern = Pattern.compile("\\d{10}\\.\\d{6}");
	    Matcher matcher = pattern.matcher(data);
	    if(matcher.find()){
	    	int start = matcher.start();
	    	int end = matcher.end();
	    	sub = data.substring(start, end);
	    }
		return sub;
	}
}
