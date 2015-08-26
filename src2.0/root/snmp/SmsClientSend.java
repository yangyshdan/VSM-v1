package root.snmp;

import com.xuanwu.sms.entity.BatchRequest;
import com.xuanwu.sms.SmsClient;
import com.xuanwu.sms.ISmsClient;
import com.xuanwu.sms.entity.SmsMT;
import java.util.*;
import java.text.*;
import java.io.FileInputStream;

public class SmsClientSend {
    ISmsClient smsClient =  null;
    BatchRequest _batchRequest =  null;
    SmsMT[] _smss = new SmsMT[1];
    String _SmsIP = null;
    String _SmsUser = null;
    String _SmsPasswd = null;
    String _BatchName =  null;
    String _IsLongSms = null;
    String _SmsType = null;
    int _SmsPort;
    
   public SmsClientSend()
        throws Exception
    {
        //读取参数
        Properties prop = new Properties() ;
        String tmp;
        String path = this.getClass().getResource("/").getPath();
		path=path.substring(1, path.indexOf("lib")).replaceAll("%20", " ")+"webapps/dongguan_sr/sms/sms.xml";
        FileInputStream fi = new FileInputStream(path) ;
        prop.loadFromXML(fi);
        fi.close() ;
        if ( (_SmsIP = prop.getProperty("SmsIP") )== null )  
        	throw new Exception("not define 'SmsIP'") ;
        if ( (tmp = prop.getProperty("SmsPort") )== null )  
        	throw new Exception("not define 'SmsPort'") ;
        _SmsPort = Integer.parseInt( tmp );
        if ( (_SmsUser = prop.getProperty("SmsUser") )== null )  
        	throw new Exception("not define 'SmsUser'") ;
        if ( (_SmsPasswd = prop.getProperty("SmsPasswd") )== null )  
        	throw new Exception("not define 'SmsPasswd'") ;
        if ( (_BatchName = prop.getProperty("BatchName") )== null )  
        	throw new Exception("not define 'BatchName'") ;
        if ( (_IsLongSms = prop.getProperty("IsLongSms") )== null )  
        	throw new Exception("not define 'IsLongSms'") ;
        if ( (_SmsType = prop.getProperty("SmsType") )== null )  
        	throw new Exception("not define 'SmsType'") ;
        if( _IsLongSms.equals("Y") ||_IsLongSms.equals("y") )
            _IsLongSms = "1";
        else _IsLongSms = "0" ;
        //初始化SMS组件客户端
        smsClient = new SmsClient(_SmsIP, _SmsPort,false);
        // 组合获取批次号对象 
        _batchRequest = new BatchRequest();
        // 用户名
        _batchRequest.setUser(_SmsUser);
        // 密码
        _batchRequest.setPsw(_SmsPasswd);
        // 批次名
        _batchRequest.setBatchname(_BatchName);
        // isdouble：是否过滤重复号码。0：否；1：是
        _batchRequest.setIsdouble(0);
        // islong：是否是长短信。0为普通短信；1为长短信
        _batchRequest.setIslong(Integer.parseInt(_IsLongSms));
        // 短信类型。1：默认信息；2：默认信息；3：动账短信
        _batchRequest.setMsgtype(Integer.parseInt(_SmsType));
        // 计划发送量
        _batchRequest.setPlannum(0);
    }
    public void SmsSend( String phone,String memo)
        throws Exception
    {
	    // 获取的批次号    成功：返回值批次号（36长的字符串）；失败：返回值<0
		String batchNum = smsClient.getBatchNum(_batchRequest);
	    if(batchNum != null && batchNum.length() == 36)
			System.out.println("批次号：[" + batchNum + "]");
		else 
		{
		    System.out.println("获取批次号失败，代号：" + batchNum);
	        throw new Exception("获取批次号失败，代号：" + batchNum );
	    }
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currtime = sdf.format( Calendar.getInstance().getTime() ); //当前时间
		Calendar cl = Calendar.getInstance();
		cl.setTime( Calendar.getInstance().getTime() );
        _smss[0] = new SmsMT( phone, memo,"58901", "5801", "5801",cl.getTime(), "0", "1", "000", "001", "002", "003", "004");
        int batchSendResult = smsClient.sendBatchMts(_smss, batchNum);
        System.out.println("发送短信，返回值为：[" + batchSendResult + "]");
        int batchCloseResult = smsClient.batchClose(batchNum);
	// 成功：返回值=1；失败：返回值<0
        System.out.println("发送批次完毕包，返回值为：[" + batchCloseResult + "]");
        if( batchSendResult <= 0 )
            throw new Exception("发送短信数小余等于0");
    }
    
    public static void  main(String args[])
    {
        if( args.length !=2 )
        {
            System.out.println("用法: java SmsClientSend 手机号码 短信内容\n");
            System.exit(-1);
        }
        try
        {
            SmsClientSend ss = new SmsClientSend();
            ss.SmsSend(args[0],args[1]);
        }
        catch( Exception e )
        {
            e.printStackTrace();
            System.exit(-2);
        }
    }
}
