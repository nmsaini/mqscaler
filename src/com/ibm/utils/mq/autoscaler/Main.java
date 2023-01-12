package com.ibm.utils.mq.autoscaler;

import com.ibm.mq.MQException;

public class Main {

	public static void main(String[] args) {
		String propsFileName = System.getenv("MQSCALER_PROPSFILE");
		MQScalerProps propsFile = new MQScalerProps(propsFileName);
		String host = propsFile.readProperty("QM_HOST", "");
		String port = propsFile.readProperty("QM_PORT", "1414");
		String channel = propsFile.readProperty("QM_CHLNAME", "MY.SVRCONN");
		String qmname = propsFile.readProperty("QM_NAME", "QM1");
		String qNames = propsFile.readProperty("QM_METRICS_QS", "");
		
		int listenPort = Integer.parseInt(port);
		
		System.out.println("Connecting to QM: (" + qmname + ", " + host + ", " + listenPort + ", "+channel +")");
		System.out.println();
		
		try {
			QueueManager qmgr = new QueueManager(host, listenPort, channel, qmname);
						
			String[] q = qNames.split(",");
			
			for (String qmetrics : qmgr.getCurrentDepth(q)) {
				System.out.println(qmetrics);
			}
			
		}catch(MQException ignore) {
			ignore.printStackTrace();
		}
	}
}
