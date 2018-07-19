import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;

public class Sample {
	private static final String queueName = "ALERTS";
	/*
	 * private static final String queueManager = "DIMQ"; private static final
	 * int mqPort = 12579; private static final String mqHost =
	 * "192.168.210.116";
	 */

	private static final String queueManager = "DEVIntegration";
	private static final int mqPort = 11581;
	private static final String mqHost = "192.168.211.95";

	public static void main(String[] args) throws Exception {

		simulateSendRecieve(mqHost, mqPort, queueManager, queueName);
		MQIbm m = new MQIbm();
		
		//m.postMessage(mqHost, mqPort, queueManager, queueName, "QWERTY 12345 67890");
		
		boolean isRunning = m.isMQRunning(queueManager, mqHost, mqPort);
		System.out.println(isRunning);
		
	}
	
	public static void simulateSendRecieve (String host, int port, String queueManager, String queueName)
			throws Exception {
		
		MQQueueConnectionFactory cf = new MQQueueConnectionFactory();
		// Config
		cf.setHostName(host);
		cf.setPort(port);
		cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
		cf.setUseConnectionPooling(false);
		
		cf.setQueueManager(queueManager);
//		/cf.setChannel("SYSTEM.DEF.SVRCONN");
      
		MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection("mqm", "mqm");
		
		connection.start();
		
		MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		MQQueue queue = (MQQueue) session.createQueue(queueName);
		
		for (int i=0;i<5;i++) {
			sendMessage(session, queue, "Testing  <><><><><> "+i, i);
		}
			
		browseMessages(session, queue);
		
		for (int i=0;i<5;i++) {
			Message message = recieveMessage(session, queue, "STATUS = 'WAIT'");
			if (message != null)
				System.out.println(((TextMessage)message).getText() + " Priority => "+message.getJMSPriority() );
			else
				System.out.println("No Message Found");
		}
		
		/*for (int i=0;i<10;i++) {
			Message message = recieveMessage(session, queue, null);
			if (message != null)
				System.out.println(message.getJMSMessageID() + ", " + message.getJMSType() + ", " + message.getStringProperty("STATUS"));
			else
				System.out.println("No Message Found");
		}*/
		
		//browseMessages(session, queue);
		
		//message = recieveMessage(session, queue, "STATUS = 'WAIT'");
		//System.out.println(message);
		
		connection.close();
	}
	
	private static void sendMessage (MQQueueSession session, MQQueue queue, String msg, int msgPriority) throws JMSException {
		QueueSender sender = session.createSender(queue);
		sender.setPriority(msgPriority);
		
		Message message = session.createTextMessage(msg);
		//message.setJMSPriority(msgPriority);
		message.setStringProperty("STATUS", "WAIT");
		sender.send(message);
		
		sender.close();
	}
	
	private static Message recieveMessage (MQQueueSession session, MQQueue queue, String selector) throws JMSException {
		QueueReceiver receiver = session.createReceiver(queue, selector);
		Message message = receiver.receive();
		receiver.close();
		return message;
	}
	
	private static void browseMessages (QueueSession session, Queue queue) throws JMSException {
		
		QueueBrowser browser = session.createBrowser(queue);
		
		int i = 1;
		
		Enumeration enums = browser.getEnumeration();
		while (enums.hasMoreElements()) {
			Object objMsg = enums.nextElement();
            if (objMsg instanceof TextMessage) {
                TextMessage message = (TextMessage) objMsg;

                System.out.println("Text message: " + i + ". MSG:" + message.getText() + " MSG id:"
                        + message.getJMSMessageID() + " MSG dest:" + message.getJMSDestination() + "Priority "+ message.getJMSPriority());

            } else if (objMsg instanceof ObjectMessage) {

                ObjectMessage message = (ObjectMessage) objMsg;

                System.out.println("Object Message: " + i + ". MSG" + " MSG id:" + message.getJMSMessageID()
                        + " MSG dest:" + message.getJMSDestination());

            }
            
            i++;
		}
	}
}
