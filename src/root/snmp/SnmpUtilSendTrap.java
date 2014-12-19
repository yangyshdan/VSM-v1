package root.snmp;
import java.io.IOException;  
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;  
  
import org.snmp4j.CommunityTarget;  
import org.snmp4j.PDU;  
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;  
import org.snmp4j.TransportMapping;  
import org.snmp4j.event.ResponseEvent;  
import org.snmp4j.mp.SnmpConstants;  
import org.snmp4j.smi.Address;  
import org.snmp4j.smi.GenericAddress;  
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;  
import org.snmp4j.smi.OctetString;  
import org.snmp4j.smi.VariableBinding;  
import org.snmp4j.transport.DefaultUdpTransportMapping;  
  
/** 
 * 本类用于向管理进程发送Trap信息 
 *  
 * @author gujin 
 * 
 */  
public class SnmpUtilSendTrap {  
  
    private Snmp snmp = null;  
  
    private Address targetAddress = null;  
  
    public void initComm(String address) throws IOException {  
  
        // 设置管理进程的IP和端口  
       targetAddress = GenericAddress.parse("udp:"+address);  
        TransportMapping transport = new DefaultUdpTransportMapping();  
       snmp = new Snmp(transport);  
        transport.listen();  
  
    }  
  
    /** 
     * 向管理进程发送Trap报文 
     *  
     * @throws IOException 
     */  
   public void sendPDU(String enterprise,String snmppublic,Map<String, String> msgs) throws IOException {  

      // 设置 target  
       CommunityTarget target = new CommunityTarget();  
       target.setAddress(targetAddress);  
 
       // 通信不成功时的重试次数  
        target.setRetries(2);  
       // 超时时间  
       target.setTimeout(1500);  
       target.setCommunity(new OctetString(snmppublic));
        // snmp版本  
        target.setVersion(SnmpConstants.version1); 
 
        InetAddress inet = null;
		try {
			inet = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       // 创建 PDU  
        PDUv1 pdu = new PDUv1();  
        pdu.setEnterprise(new OID(enterprise));
        pdu.setGenericTrap(6);
        pdu.setSpecificTrap(3);
        if(inet != null){
        	pdu.setAgentAddress(new IpAddress(inet.getAddress()));
        }
        for (String oid : msgs.keySet()) {
        	 pdu.add(new VariableBinding(new OID(oid),  
                     new OctetString(msgs.get(oid))));  
		}
        pdu.setType(PDU.V1TRAP);  
 
       // 向Agent发送PDU，并接收Response  
        ResponseEvent respEvnt = snmp.send(pdu, target);  
 
       // 解析Response  
       if (respEvnt != null && respEvnt.getResponse() != null) {  
           Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getResponse() .getVariableBindings();  
           for (int i = 0; i < recVBs.size(); i++) {  
               VariableBinding recVB = recVBs.elementAt(i);  
          }  
        }  
    }  
 
    /***
	 * 连接测试
	 * @param ip
	 */
	public boolean connTest(String ip) {
		boolean testResult=true;
		try {
			targetAddress = GenericAddress.parse("udp:" + ip + "/161");
			TransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			transport.listen();
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setAddress(targetAddress);
			// 通信不成功时的重传次数
			target.setRetries(2);
			// 超时时间（ 单位：milliseconds ）
			target.setTimeout(1500);
			// 设置或者获取版本号（支持V1，V2c，V3）
			target.setVersion(SnmpConstants.version2c);
			// 创建 PDU
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID("0.0")));
			// MIB的访问方式
			pdu.setType(PDU.GETNEXT);
			ResponseEvent respEvnt = snmp.send(pdu, target);
			// 解析Response
			if (respEvnt != null && respEvnt.getResponse() != null) {
			}else {
				testResult=false;
				System.out.println("不支持SNMP协议或超时");
			}
		} catch (Exception e) {
			testResult=false;
			System.out.println("不支持SNMP协议或超时");
		}
		return testResult;

	}
	
	public static void main(String[] args) {
		SnmpUtilSendTrap sendTrap = new SnmpUtilSendTrap();
		Map<String, String> map = new HashMap<String, String>();
		map.put("1.3.6.1.4.1.1981.1.4.3", "B-IMAGE");
		map.put("1.3.6.1.4.1.1981.1.4.4", "");
		map.put("1.3.6.1.4.1.1981.1.4.5", "4600");
		map.put("1.3.6.1.4.1.1981.1.4.6", "'Modify Portal Configuration' called by ' Navi User admin' (192.168.1.197) on 'Legacy Write Service' (Result: success).");
		map.put("1.3.6.1.4.1.1981.1.4.7", "FCN00120100205");
		try {
			sendTrap.initComm("192.168.1.106/162");
			sendTrap.sendPDU("1.3.6.1.4.1.1981","public", map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
} 
