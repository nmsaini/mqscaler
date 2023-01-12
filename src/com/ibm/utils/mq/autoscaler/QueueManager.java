package com.ibm.utils.mq.autoscaler;

import java.util.Vector;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;

public class QueueManager{

    private final String host;
    private final int port;
    private final String channel;
    private final String manager;
    private final MQQueueManager qmgr;

    public QueueManager(String host, int port, String channel, String manager) throws MQException {
        this.host = host;
        this.port = port;
        this.channel = channel;
        this.manager = manager;
        this.qmgr = createQueueManager();
    }

    public String getCurrentDepth(String queueName) throws MQException {
        MQQueue queue = qmgr.accessQueue(queueName, MQConstants.MQOO_INQUIRE | MQConstants.MQOO_INPUT_AS_Q_DEF, null, null, null);
        String queueMetrics = queueName + " " + queue.getCurrentDepth();
        queue.close();
        return queueMetrics;
    }
    
    public String[] getCurrentDepth(String[] queues) throws MQException {
    	
    	Vector<String> queueMetrics = new Vector<String>();
    	
    	for(String queue: queues) {
				queueMetrics.add(getCurrentDepth(queue.trim()));
    	}
    	
    	return ((String[])queueMetrics.toArray(new String[queueMetrics.size()]));
    }

    @SuppressWarnings("unchecked")
    private MQQueueManager createQueueManager() throws MQException {
        MQEnvironment.channel = channel;
        MQEnvironment.port = port;
        MQEnvironment.hostname = host;
        MQEnvironment.properties.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
        return new MQQueueManager(manager);
    }
}
