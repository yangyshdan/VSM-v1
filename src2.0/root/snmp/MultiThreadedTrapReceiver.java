package root.snmp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;


/**
 * 本类用于监听代理进程的Trap信息
 * 
 * @author gujin
 * 
 */
public class MultiThreadedTrapReceiver implements CommandResponder{
	private MultiThreadedMessageDispatcher dispatcher;
	private Snmp snmp = null;
	private Address listenAddress;
	private ThreadPool threadPool;
	public MultiThreadedTrapReceiver() {
		// BasicConfigurator.configure();
	}

	private void init() throws UnknownHostException, IOException {
		threadPool = ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool,
				new MessageDispatcherImpl());
		listenAddress = GenericAddress.parse(System.getProperty(
				"snmp4j.listenAddress", "udp:196.1.1.97/162")); // 本地IP与监听端口
		TransportMapping transport;
		// 对TCP与UDP协议进行处理
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping(
					(UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping(
					(TcpAddress) listenAddress);
		}
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
				MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		snmp.listen();
	}

	public void run() {
		try {
			init();
			snmp.addCommandResponder(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	  public void processPdu(CommandResponderEvent respEvnt) {
		  Map<String, String> traps = new HashMap<String, String>();
	        // 解析Response  
	        if (respEvnt != null && respEvnt.getPDU() != null) {
	        	String peerAdd = respEvnt.getPeerAddress().toString();
	        	Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();  
	            for (int i = 0; i < recVBs.size(); i++) {  
	                VariableBinding recVB = recVBs.elementAt(i);  
	                traps.put(recVB.getOid().toString(), recVB.getVariable().toString());
	                System.out.println(recVB.getOid() + " : " + recVB.getVariable());
	            }  
	            SaveTrapThread thread = new SaveTrapThread();
				thread.run(peerAdd,traps);
	        }  
	  }
	/**
	 * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息 当接收到trap时，会自动进入这个方法
	 * 
	 * @param respEvnt
	 */
//	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
//	public void processPdu(CommandResponderEvent respEvnt) {
//		// 解析Response
//		if (respEvnt != null && respEvnt.getPDU() != null) {
//			System.out.println(respEvnt.toString());
//			PDU pdu = respEvnt.getPDU(); 
//			String peerAdd = respEvnt.getPeerAddress()+"";
//			int trapType = -2;
//			switch (pdu.getType()) {
//			case -92:
//				PDUv1 p = (PDUv1)pdu; 
//				trapType = p.getSpecificTrap();
//				break;
//			case -89:
//				trapType = -1; 
//				break;
//			default:
//				break;
//			}
//			Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();
//			if(peerAdd.length() > 0 && peerAdd.contains("/")){
//				peerAdd = peerAdd.split("/")[0];
//			}
//			
//			Map<String, String> traps = new HashMap<String, String>();
//			if(trapType != -2){
//				if(trapType != -1){
//					traps.put("trapType", trapType+"");
//				}
//				for (int i = 0; i < recVBs.size(); i++) {
//					VariableBinding recVB = recVBs.elementAt(i);
//					if(trapType==-1){
//						if(recVB.getVariable().toString().contains("1.3.6.1.4.1.1981.2")){
//							traps.put("trapType", "2");
//						}else if(recVB.getVariable().toString().contains("1.3.6.1.4.1.1981.3")){
//							traps.put("trapType", "3");
//						}else if(recVB.getVariable().toString().contains("1.3.6.1.4.1.1981.4")){
//							traps.put("trapType", "4");
//						}else if(recVB.getVariable().toString().contains("1.3.6.1.4.1.1981.5")){
//							traps.put("trapType", "5");
//						}else if(recVB.getVariable().toString().contains("1.3.6.1.4.1.1981.6")){
//							traps.put("trapType", "6");
//						}
//					}
//					traps.put(recVB.getOid().toString(), recVB.getVariable().toString());
//				}
//				SaveTrapThread thread = new SaveTrapThread();
//				thread.run(peerAdd, traps);
//			}
//		}
//	}
	public static void main(String[] args) {
		MultiThreadedTrapReceiver multiThreadedTrapReceiver = new MultiThreadedTrapReceiver();
		multiThreadedTrapReceiver.run();
	}
}