package com.att.aro.main;

import java.io.File;
import java.io.IOException;

import com.att.aro.main.DatacollectorBridge.Status;
import com.att.aro.model.TraceData;

public interface DataCollectorBridgeEnabledFrame {
	abstract TraceData getTraceData();
	abstract void clearTrace() throws IOException;
	abstract void openTrace(File f) throws IOException;
	abstract void dataCollectorStatusCallBack(Status s);
	abstract void dataCollectorStatusCallBack(boolean bStartItem, boolean bStopItem);
}
