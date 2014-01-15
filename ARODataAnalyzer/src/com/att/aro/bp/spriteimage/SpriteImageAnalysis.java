package com.att.aro.bp.spriteimage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.att.aro.model.HttpRequestResponseInfo;
import com.att.aro.model.PacketInfo;
import com.att.aro.model.TCPSession;
import com.att.aro.model.HttpRequestResponseInfo.Direction;

public class SpriteImageAnalysis {
	private static Logger LOGGER = Logger.getLogger(SpriteImageAnalysis.class.getName());

	private static final int IMAGE_SIZE_LIMIT = 6144;
	private List<SpriteImageEntry> analysisResults = new ArrayList<SpriteImageEntry>();

	/**
	 * Represents SpriteImage analysis.
	 * 
	 */
	public SpriteImageAnalysis(List<TCPSession> tcpSessions) {

		if (null != tcpSessions) {
			// loop through TCP session
			for (TCPSession tcpSession : tcpSessions) {
				
				double lastTimeStamp = 0.0;
				HttpRequestResponseInfo lastReqRessInfo = null;
				HttpRequestResponseInfo secondReqRessInfo = null;
				boolean thirdOccurrenceTriggered = false;
				
				// loop through HTTP requests and responses
				for (HttpRequestResponseInfo reqRessInfo : tcpSession.getRequestResponseInfo()) {
					if (reqRessInfo.getDirection() == Direction.RESPONSE) {
						if (reqRessInfo.getContentType() != null) {
							PacketInfo pktInfo = reqRessInfo.getFirstDataPacket();
							if (pktInfo != null) {
								if (reqRessInfo.getContentType().contains("image/") && reqRessInfo.getContentLength() < IMAGE_SIZE_LIMIT) {
									if (lastTimeStamp == 0.0) {
										lastTimeStamp = pktInfo.getTimeStamp();
										lastReqRessInfo = reqRessInfo;
										continue;
									} else{ 
										if ((pktInfo.getTimeStamp() - lastTimeStamp) <= 5.0) {
											if (!thirdOccurrenceTriggered) {
												secondReqRessInfo = reqRessInfo;
												thirdOccurrenceTriggered = true;
												continue;
											} else {
												/* -At this stage 3 images found to be downloaded in 5 secs. store them.
												 * -fix for defect DE26829*/
												analyzeContent(lastReqRessInfo);
												analyzeContent(secondReqRessInfo);
												analyzeContent(reqRessInfo);
												/* -reset the variables to search more such images in this session
												 * -fix for defect DE26829 */
												
												lastTimeStamp = 0.0;
												lastReqRessInfo = null;
												secondReqRessInfo = null;
												thirdOccurrenceTriggered = false;
											}
										}
										lastTimeStamp = pktInfo.getTimeStamp();
										lastReqRessInfo = reqRessInfo;
										secondReqRessInfo = null;
										thirdOccurrenceTriggered = false;
									}
								} 
							}
						}
					}			
				}
			}
		}
	}

	private void analyzeContent(HttpRequestResponseInfo rr) {
		this.analysisResults.add(new SpriteImageEntry(rr));
	}

	public boolean isTestPassed() {
		return this.analysisResults.size() > 0 ? false : true;   
	}

	public int getNumberOfFilesToBeSprited() {
		return this.analysisResults.size();
	}

	public List<SpriteImageEntry> getResults() {
		return this.analysisResults;
	}
}
