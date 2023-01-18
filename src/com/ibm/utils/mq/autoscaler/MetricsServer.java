package com.ibm.utils.mq.autoscaler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.sun.net.httpserver.HttpServer;
import com.ibm.mq.MQException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MetricsServer {
	static QueueManager _qmgr = null;
	static String _qNames = "";

	public MetricsServer() {
	}

	public static void main(String[] args) {
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);

		String propsFileName = System.getenv("MQSCALER_PROPSFILE");
		MQScalerProps propsFile = new MQScalerProps(propsFileName);
		String host = propsFile.readProperty("QM_HOST", "");
		String port = propsFile.readProperty("QM_PORT", "1414");
		String channel = propsFile.readProperty("QM_CHLNAME", "MY.SVRCONN");
		String qmname = propsFile.readProperty("QM_NAME", "QM1");
		_qNames = propsFile.readProperty("QM_METRICS_QS", "");
		int listenPort = Integer.parseInt(port);
		
		System.out.println("Connecting to QM: (" + qmname + ", " + host + ", " + listenPort + ", "+channel +")");
		System.out.println();
		
		try {
			_qmgr = new QueueManager(host, listenPort, channel, qmname);						
		}catch(MQException ignore) {
			ignore.printStackTrace();
		}

		try {
			System.out.println("Launching Server on 8888 port");
			HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0",8888), 0);
			server.createContext("/metrics", new MyHandler());
			server.setExecutor(threadPoolExecutor);
			System.out.println("starting");
			server.start();
			System.out.println("ready and serving 8888 port");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

        	String[] q = _qNames.split(",");
			
    		String NEWLINE = "\n";
    		String response = "";
    		
        	try {
				for (String qmetrics : _qmgr.getCurrentDepth(q)) {
					// change queueName to lower_case
					String metricName = "ibmmq_qdepth_" +qmetrics.replace('.','_');
					response += "# HELP "+ metricName + " shows the current queue depth"+NEWLINE;	// HELP
					response += "# TYPE "+ metricName + " gauge"+NEWLINE;	// TYPE					
					response += metricName + NEWLINE;
				}
			} catch (MQException e) {
				e.printStackTrace();
			}
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
