import java.util.Hashtable;

import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import com.ibm.jms.JMSMessage;
import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.MQC;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueReceiver;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;

public class MQIbm {

	private String channel = "SYSTEM.DEF.SVRCONN";

	QueueConnection queueConnection;
	QueueSession queueSession;

	MQQueueManager qMgr = null;
	int openOptions = CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_INPUT_SHARED
			+ CMQC.MQOO_INQUIRE + CMQC.MQOO_BROWSE + CMQC.MQOO_OUTPUT
			+ CMQC.MQOO_FAIL_IF_QUIESCING;

	public boolean isMQRunning(String queueManager, String mqHost, int mqPort) {
		try {
			MQQueueConnectionFactory mqQcf = new MQQueueConnectionFactory();
			mqQcf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			mqQcf.setUseConnectionPooling(false);
			mqQcf.setQueueManager(queueManager);
			mqQcf.setHostName(mqHost);
			mqQcf.setPort(mqPort);
			mqQcf.setChannel(channel);

			queueConnection = mqQcf.createQueueConnection("mqm", "mqm");
			queueSession = queueConnection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed connecting to QueueManager.");
		}

		if (queueSession != null)
			return true;
		else
			return false;
	}

	public void getAllAvailableQueues() {

	}

	public int getQueueDepth(String queueManager, String mqHost, int mqPort,
			String queueName) {
		int noOfMesssages = 0;
		try {
			Hashtable properties = new Hashtable();

			properties.put(CMQC.HOST_NAME_PROPERTY, mqHost);
			properties.put(CMQC.PORT_PROPERTY, mqPort);
			properties.put(CMQC.CHANNEL_PROPERTY, channel);
			properties.put(CMQC.USER_ID_PROPERTY, "mqm");
			properties.put(CMQC.PASSWORD_PROPERTY, "mqm");

			qMgr = new MQQueueManager(queueManager, properties);

			com.ibm.mq.MQQueue mqQuue = qMgr
					.accessQueue(queueName, openOptions);
			noOfMesssages = mqQuue.getCurrentDepth();
			System.out.println("Current depth " + noOfMesssages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noOfMesssages;
	}

	public void postMessage(String host, int port, String queueManager, String queueName, String msgTxt)
			throws Exception {
		
		
		
		MQQueueConnectionFactory cf = new MQQueueConnectionFactory();
		// Config
	      cf.setHostName(host);
	      cf.setPort(port);
	      cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
	      cf.setQueueManager(queueManager);
	      cf.setChannel("SYSTEM.DEF.SVRCONN");
	      
	      MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection("mqm", "mqm");
	      MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	      MQQueue queue = (MQQueue) session.createQueue(queueName);
	      MQQueueSender sender =  (MQQueueSender) session.createSender(queue);
	      MQQueueReceiver receiver = (MQQueueReceiver) session.createReceiver(queue);

	      long uniqueNumber = System.currentTimeMillis() % 1000;
	      JMSTextMessage message = (JMSTextMessage) session.createTextMessage(msgTxt + uniqueNumber);
	      message.setStringProperty("STATUS", "WAIT");

	      // Start the connection
	      connection.start();

	      //sender.send(message);
	      //System.out.println("Sent message:\\n" + message);

	      JMSMessage receivedMessage = (JMSMessage) receiver.receive();
	      System.out.println("\\nReceived message:\\n" + receivedMessage);

	      sender.close();
	      receiver.close();
	      session.close();
	      connection.close();

	      System.out.println("\\nSUCCESS\\n");
		
	}

	public boolean sendMessage(String queueManager, String mqHost, int mqPort,
			String queueName) {
		int beforeDepth = 0;
		int currentDepth = 0;
		try {

			com.ibm.mq.MQQueue queue = qMgr.accessQueue(queueName, openOptions);
			MQMessage message = new MQMessage();
			message.setStringProperty("STATUS", "WAIT");
			message.writeString("Writing Message to MQ !!!!!");
			queue.put(message);

			currentDepth = getQueueDepth(queueManager, mqHost, mqPort,
					queueName);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed to send message to Queue.");
		}
		if (currentDepth > beforeDepth) {
			return true;
		} else
			return false;

	}

	public boolean receiveMessage(String queueManager, String mqHost,
			int mqPort, String queueName) {
		int beforeDepth = 0;
		int currentDepth = 0;
		try {

			beforeDepth = getQueueDepth(queueManager, mqHost, mqPort, queueName);
			com.ibm.mq.MQQueue queue = qMgr.accessQueue(queueName, openOptions);
			QueueReceiver queueReceiver = queueSession
					.createReceiver(queueSession.createQueue(queueName));

			com.ibm.mq.MQMsg2 msg2 = new com.ibm.mq.MQMsg2();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = CMQC.MQOO_INPUT_SHARED;
			gmo.matchOptions = MQC.MQMO_NONE;
			gmo.waitInterval = 100;

			queue.getMsg2(msg2, gmo);
			byte[] data = msg2.getMessageData();
			System.out.println("Receiving Message 1 : " + (new String(data)));

			currentDepth = getQueueDepth(queueManager, mqHost, mqPort,
					queueName);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed to receive message from Queue.");
		}
		if (currentDepth < beforeDepth) {
			return true;
		} else
			return false;

	}
}