package root.usercon;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.taskdefs.condition.IsFalse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.DES;
import com.huiming.service.usercon.UserConService;
import com.huiming.web.base.ActionResult;
import com.project.hmc.core.HmcBase;
import com.project.hmc.engn.ComputerSystem;
import com.project.hmc.engn.VirtualMac;
import com.project.hmc.engn.VirtualMacsd;
import com.project.nmon.engn.Scp_Sftp;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class UserConAction extends SecurityAction{
	UserConService service = new UserConService();
	private VirtualMacsd virtualMacsd = new VirtualMacsd();
	
	public ActionResult doDefault(){
		DBPage hmcPage = null;
		DBPage viosPage = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		hmcPage = service.getHMCPage(null, null, curPage, numPerPage);
		viosPage = service.getVIOSUsPage(null, null, curPage, numPerPage);
		this.setAttribute("hmcPage", hmcPage);
		this.setAttribute("viosPage", viosPage);
		return new ActionResult("/WEB-INF/views/usercon/userconList.jsp");
	}
	
	public ActionResult doHMCPage(){
		DBPage hmcPage = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		String ipaddress = getStrParameter("ipaddress");
		String state = getStrParameter("hmc_state");
		hmcPage = service.getHMCPage(ipaddress, state, curPage, numPerPage);
		this.setAttribute("hmcPage", hmcPage);
		return new ActionResult("/WEB-INF/views/usercon/hmcPage.jsp");
	}
	
	public ActionResult doVIOSPage(){
		DBPage viosPage = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		String vname = getStrParameter("vname");
		String state = getStrParameter("vios_state");
		viosPage = service.getVIOSUsPage(vname, state, curPage, numPerPage);
		this.setAttribute("viosPage", viosPage);
		return new ActionResult("/WEB-INF/views/usercon/viosPage.jsp");
	}
	
	public void doHmcDel(){
		String id = getStrParameter("id");
		if(id!=null && id.length()>0){
			service.hmcDel(id);
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doViosDel(){
		String id = getStrParameter("id");
		if(id!=null && id.length()>0){
			service.viosDel(id);
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public ActionResult doEditHMCInfo(){
		String id = getStrParameter("id");
		DataRow row = null;
		if(id!=null && id.length()>0){
			row = service.getHMCInfo(id);
			row.set("password", new DES().decrypt(row.getString("password")));
		}
		this.setAttribute("hmcInfo", new JSONObject().fromObject(row));
		return new ActionResult("/WEB-INF/views/usercon/editHMC.jsp");
	}
	public ActionResult doEditVIOSInfo(){
		String id = getStrParameter("id");
		DataRow row =null;
		if(id!=null && id.length()>0){
			row = service.getVIOS(id);
			row.set("password", new DES().decrypt(row.getString("password")));
		}
		List<DataRow> rows = service.gethypervisorInfo();
		this.setAttribute("hypervisor", rows);
		this.setAttribute("viosInfo", new JSONObject().fromObject(row));
		this.setAttribute("voisId", id);
		return new ActionResult("/WEB-INF/views/usercon/editVIOS.jsp");
	}
	
	public void doSaveInfo(){
		String hmcId = getStrParameter("hmcId");
		String ipaddress = getStrParameter("ipaddress");
		String user = getStrParameter("user");
		String password = getStrParameter("password");
		DataRow row = new DataRow();
		row.set("id", hmcId);
		row.set("ip_address", ipaddress);
		row.set("user", user);
		row.set("password", new DES().encrypt(password));
		row.set("state", 0);
		service.addHMCInfo(row);
	}
	
	/**
	 * 校验HMC登录信息
	 */
	public void doTestAcct(){
		String hmcId = getStrParameter("hmcId");
		String ipaddress = getStrParameter("ipaddress");
		String user = getStrParameter("user").replaceAll("&amp;nbsp;", " ");
		String password = getStrParameter("password").replaceAll("&amp;nbsp;", " ");
		if(StringHelper.isEmpty(hmcId)){
			if(service.hasHMCInfo(user,ipaddress)){
				ResponseHelper.print(getResponse(), "has_user");
				return;
			}
		}
		ComputerSystem com = new ComputerSystem();
		VirtualMac virtualMac = new VirtualMac();
		HmcBase base = new HmcBase(ipaddress, 22, user, password);
		Session session = null;
		DataRow row = new DataRow();
		try {
			session = base.openConn();
			if(session!=null){
				row.set("id", hmcId);
				row.set("ip_address", ipaddress);
				row.set("user", user);
				row.set("password", new DES().encrypt(password));
				row.set("state", 1);
				service.addHMCInfo(row);
				//立即去采集配置信息
				com.getResult();
				virtualMacsd.getResult();
//				virtualMac.getResult();
				ResponseHelper.print(getResponse(), "true");
			}else{
				ResponseHelper.print(getResponse(), "false");
			}
		} catch (Exception e) {
			service.hmcDel(row.getString("id"));
			ResponseHelper.print(getResponse(), "unknow");
			e.printStackTrace();
		}finally{
			if(session!=null){
				session.close();
			}
			base.closeConn();
		}
	}
	
	/**
	 * 校验VIOS登录信息
	 */
	public void doTestAcctVios(){
		String isflag = "";
		String hypervisorId = getStrParameter("physical");
		String viosId = getStrParameter("viosId");
		String[] vid = getStrArrayParameter("virtual");
		String[] vmId = checkStrArray(vid,"multiselect-all");
		String user = getStrParameter("user");
		String password = getStrParameter("password").replaceAll("&amp;nbsp;", " ");
		for (int i = 0; i < vmId.length; i++) {
			DataRow row = service.getVirtualInfo(vmId[i]);
			if(row!=null && row.size()>0){
				String ip[] = row.getString("ip_address").replaceAll("&amp;nbsp;", " ").split(",");
				String ipaddress = "";
				if(ip.length>1){
					ipaddress = ip[1];
				}else{
					ipaddress = ip[0];
				}
				DataRow dataRow = new DataRow();
				dataRow.set("id", viosId);
				dataRow.set("hypervisor_id", hypervisorId);
				dataRow.set("vm_id", vmId[i]);
				dataRow.set("user", user);
				dataRow.set("password", new DES().encrypt(password));
				Scp_Sftp scp = new Scp_Sftp(ipaddress, 22, user, password);
				Connection con = null;
				try {
					con = scp.login();
					if(con!=null){
						dataRow.set("state", 1);
						isflag = "true";
					}else{
						dataRow.set("state", 0);
						isflag="false";
					}
					service.addVIOSInfo(dataRow);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(con!=null){
						con.close();
					}
				}
			}
		}
		ResponseHelper.print(getResponse(),isflag);
	}
	
	public void doLoadVirtual(){
		String id = getStrParameter("hypervisorId");
		JSONArray array = new JSONArray(); 
		List<DataRow> virtualList = service.getvirtualList(id);
		List<DataRow> viosList = service.getviosList(id);
		List<DataRow> removeList = new ArrayList<DataRow>();
		if(virtualList!=null && virtualList.size()>0){
			for (DataRow dataRow : virtualList) {
				if(viosList!=null && viosList.size()>0){
					for (DataRow dataRow2 : viosList) {
						if(dataRow.getString("ele_id").equals(dataRow2.getString("ele_id"))){
							//标记已经存在的
							removeList.add(dataRow);
						}
					}
				}
			}
			virtualList.removeAll(removeList);  //删除已经存在的元素
			for (DataRow dataRow : virtualList) {
				JSONObject obj = new JSONObject();
				obj.put("ele_id", dataRow.getString("ele_id"));
				obj.put("ele_name", dataRow.getString("ele_name"));
				array.add(obj);
			}
		}
		ResponseHelper.print(getResponse(), array);
	}
	
	
	private String[] checkStrArray(String[] str,String mach){
		if(str==null || str.length == 0){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (String string : str) {
			if(!string.equals(mach)){
				list.add(string);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	
	public static void main(String[] args) {
		System.out.println(new DES().decrypt("KsN3aX/vrqVObwEnrBDjYw=="));
		System.out.println(new DES().encrypt("111111"));
		System.out.println(new DES().decrypt("Lcii+80EXAM="));
	}
}
