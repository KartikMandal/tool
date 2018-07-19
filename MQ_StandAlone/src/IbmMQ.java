import java.util.Hashtable;

import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.MQC;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnectionFactory;

public class IbmMQ {
	/*
	private static final String QUEUE_NAME = "YodleeAlertsFastQueue";
	//private static final String QUEUE_NAME = "ALERTS";
    private static final String MQ_QUEUE_MANEGER = "NeoloreQueueManager";
    private static final int MQ_LISTENER_PORT = 1947;
    private static final String MQ_HOST = "192.168.210.116";
	*/
	/*private static final String MQ_QUEUE_MANEGER = "autonpr";
    private static final int MQ_LISTENER_PORT = 11432;
    private static final String MQ_HOST = "192.168.210.88";*/
    
    
    private static final String QUEUE_NAME = "ALERTS_FAST_QUEUE";
	//private static final String QUEUE_NAME = "ALERTS";
    private static final String MQ_QUEUE_MANEGER = "NextGenMQ";
    private static final int MQ_LISTENER_PORT = 11485;
    private static final String MQ_HOST = "192.168.56.112";
    
    private static String channel = "SYSTEM.DEF.SVRCONN";
    
    static QueueConnection queueConnection;
    static QueueSession queueSession;
    
    static MQQueueManager qMgr = null ;
    static int openOptions = CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_INPUT_SHARED + CMQC.MQOO_INQUIRE + CMQC.MQOO_BROWSE + CMQC.MQOO_OUTPUT + CMQC.MQOO_FAIL_IF_QUIESCING ;
    
    public static boolean isMQRunning(){
    	try{
	    	MQQueueConnectionFactory mqQcf = new MQQueueConnectionFactory();
	        mqQcf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
	        mqQcf.setUseConnectionPooling(false);
	        mqQcf.setQueueManager(MQ_QUEUE_MANEGER);
	        mqQcf.setHostName(MQ_HOST);
	        mqQcf.setPort(MQ_LISTENER_PORT);
	        mqQcf.setChannel(channel);
	        //mqQcf.set
		    
		    queueConnection = mqQcf.createQueueConnection("mqm","mqm");
	        queueSession = queueConnection.createQueueSession(false,
	                        Session.AUTO_ACKNOWLEDGE);
	        queueSession.getMessageListener();
	        
    	}catch(Exception ex){
				ex.printStackTrace();
				System.out.println("Failed connecting to QueueManager.");
		}
    	
    	if(queueSession!=null)
        	return true;
        else 
        	return false;
    }
    
    public static int getQueueDepth(){
    	int noOfMesssages = 0;
    	try{
	    	Hashtable properties = new Hashtable();
			
		    properties.put(CMQC.HOST_NAME_PROPERTY, MQ_HOST);
		    properties.put(CMQC.PORT_PROPERTY,  MQ_LISTENER_PORT);
		    properties.put(CMQC.CHANNEL_PROPERTY, channel);
		    properties.put(CMQC.USER_ID_PROPERTY, "mqm");
		    properties.put(CMQC.PASSWORD_PROPERTY, "mqm");
		    
		    qMgr = new MQQueueManager(MQ_QUEUE_MANEGER, properties);
			
		    com.ibm.mq.MQQueue mqQuue = qMgr.accessQueue(QUEUE_NAME, openOptions);
			noOfMesssages = mqQuue.getCurrentDepth();
			System.out.println("Current depth " + noOfMesssages);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return noOfMesssages;
    }
    
    public static boolean sendMessage(){
    	int beforeDepth = 0;
    	int currentDepth = 0;
    	try{
    		
    		beforeDepth = getQueueDepth();
			
    		System.out.println("sfdsgdfhgfgh---->>"+beforeDepth);
    		QueueSender queueSender = queueSession.createSender(queueSession
			                .createQueue(QUEUE_NAME));
			TextMessage message = queueSession.createTextMessage();
			XmlAsStringInJava mm=new XmlAsStringInJava();
			message.setText(mm.convertXMLFileToString("C:\\Users\\kmandal\\Desktop\\Goal_Completion.xml"));
			//queueSender.send(message);
			/*for (int i = 0; i < 5; i++) {
			 *
			        message.setText("This is message " + (i + 1));
			        System.out.println("Sending message: " + message.getText());
			        queueSender.send(message);
			}*/
			
			queueSender.close();
			currentDepth = getQueueDepth();
			System.out.println("sfdsgdfhgfgh---->>"+beforeDepth);
    	}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Failed to send message to Queue.");
		}
		if(currentDepth > beforeDepth){
			return true;
		}else
			return false;

    }
    
    public static boolean receiveMessage(){
    	int beforeDepth = 0;
    	int currentDepth = 0;
    	try{
    		
    		beforeDepth = getQueueDepth();
    		com.ibm.mq.MQQueue queue = qMgr.accessQueue(QUEUE_NAME, openOptions);
    		QueueReceiver queueReceiver = queueSession.createReceiver(queueSession
			                .createQueue(QUEUE_NAME));
    		
    		com.ibm.mq.MQMsg2 msg2 = new com.ibm.mq.MQMsg2();
    		MQGetMessageOptions gmo = new MQGetMessageOptions();
    	    gmo.options= CMQC.MQOO_INPUT_SHARED;
    	    gmo.matchOptions=MQC.MQMO_NONE;
    	    gmo.waitInterval=100;
    	    
    	    currentDepth = getQueueDepth();
    	    for (int i = 0; i <currentDepth; i++) {
				queue.getMsg2(msg2,gmo);
	    		byte[] data = msg2.getMessageData();
				System.out.println("Receiving Message 1 : " + (new String(data)));
    	    }	
			//currentDepth = getQueueDepth();
    	}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Failed to receive message from Queue.");
		}
		if(currentDepth < beforeDepth){
			return true;
		}else
			return false;

    }
    
    
    public void byteMessageConvert(){
    	
    }
    
    public static void main(String[] args) throws Exception {
            
            System.out.println("isMQRunning : " + isMQRunning());
            System.out.println("send message" + sendMessage());
            System.out.println("receive message" + receiveMessage());
            queueSession.close();
            queueConnection.close();

    }


}
