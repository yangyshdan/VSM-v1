package root.topn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.service.topn.TopnService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class TopnAction extends SecurityAction{
	TopnService service = new TopnService();
	
	public ActionResult doTopnPage() {
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getTopnPage(curPage, numPerPage, null, null, null);
		this.setAttribute("dbPage", page);
		return new ActionResult("/WEB-INF/views/topn/topnList.jsp");
	}
	
	public ActionResult doAjaxTopnPage() {
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name = checkStr(getStrParameter("name"));
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		page = service.getTopnPage(curPage, numPerPage, name, startTime, endTime);
		this.setAttribute("dbPage", page);
		this.setAttribute("name", name);
		this.setAttribute("startTime", startTime);
		this.setAttribute("endTime", endTime);
		return new ActionResult("/WEB-INF/views/topn/ajaxTopn.jsp");
	}
	
	public ActionResult doTopnInfo() {
		Integer tid = getIntParameter("tid");
		DataRow row = service.getTopnInfo(tid);
		List<DataRow> devList = service.getdeviceList(row);
		boolean flag=averageTopn(row.getString("fdevicetype"),row.getString("fdevice").split(","), row.getString("fname"));
		JSONArray array =null;
		if(flag){
			array = service.getPrfJSON3(row);
		}else{
			array = service.getPrfJSON(row);
		}
		
		this.setAttribute("devList", devList);
		this.setAttribute("topnInfo", row);
		this.setAttribute("array", array);
		this.setAttribute("type", row.getString("fdevicetype").toLowerCase());
		return new ActionResult("/WEB-INF/views/topn/topnInfo.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doTopnSettingPrf() {
		Integer tid=getIntParameter("tid");
		DataRow historyConfig = service.getTopnInfo(tid);
		JSONObject deviceList = new JSONObject();
		//获得所有设备
//		deviceList.put("EMC", new JSONArray().fromObject(service.getEMCStorage()));
		deviceList.put("SVC", new JSONArray().fromObject(service.getStorageType("SVC")));
		deviceList.put("HOST", new JSONArray().fromObject(service.getHostList()));
		deviceList.put("APPLICATION", new JSONArray().fromObject(service.getAppList()));
		deviceList.put("BSP", new JSONArray().fromObject(service.getStorageType("BSP")));
		deviceList.put("DS", new JSONArray().fromObject(service.getStorageType("DS")));
		deviceList.put("SWITCH", new JSONArray().fromObject(service.getSwitchList()));
		JSONObject fnameList = new JSONObject();
		fnameList.put("SVC", new JSONArray().fromObject(service.getFnameList("SVC")));
		fnameList.put("HOST", new JSONArray().fromObject(service.getFnameList("HOST")));
		fnameList.put("APPLICATION", new JSONArray().fromObject(service.getFnameList("APPLICATION")));
		fnameList.put("BSP", new JSONArray().fromObject(service.getFnameList("BSP")));
		fnameList.put("DS", new JSONArray().fromObject(service.getFnameList("DS")));
//		fnameList.put("EMC", new JSONArray().fromObject(service.getFnameList("EMC")));
		fnameList.put("SWITCH", new JSONArray().fromObject(service.getFnameList("SWITCH")));
		JSONObject fprfidList = new JSONObject();
		//获得性能指标
		fprfidList.put("SVC", new JSONArray().fromObject(service.getFprffildList("SVC")));
		fprfidList.put("HOST", new JSONArray().fromObject(service.getFprffildList("HOST")));
		fprfidList.put("APPLICATION", new JSONArray().fromObject(service.getFprffildList("APPLICATION")));
		fprfidList.put("BSP", new JSONArray().fromObject(service.getFprffildList("BSP")));
		fprfidList.put("DS", new JSONArray().fromObject(service.getFprffildList("DS")));
//		fprfidList.put("EMC", new JSONArray().fromObject(service.getFprffildList("EMC")));
		fprfidList.put("SWITCH", new JSONArray().fromObject(service.getFprffildList("SWITCH")));
		
		this.setAttribute("fprfidList", fprfidList);
		this.setAttribute("fnameList", fnameList);
		this.setAttribute("deviceList", deviceList);
		this.setAttribute("historyConfig", historyConfig);
		
		return new ActionResult("/WEB-INF/views/topn/editPage.jsp");
	}
	
	public ActionResult doDeleteSettingPrf() {
		Integer tid = getIntParameter("tid");
		service.deleteTopn(tid);
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		page = service.getTopnPage(curPage, numPerPage, name, startTime, endTime);
		this.setAttribute("dbPage", page);
		this.setAttribute("name", name);
		this.setAttribute("startTime", startTime);
		this.setAttribute("endTime", endTime);
		return new ActionResult("/WEB-INF/views/topn/ajaxTopn.jsp");
	}
	
	public void doTopnPrf(){
		Integer tid = getIntParameter("tid");
		String name = checkStr(getStrParameter("name"));
		Integer topnCount = getIntParameter("top_count");
		String timescopeType = getStrParameter("timescope_type");
		String timeLength = getStrParameter("time_length");
		String timeType = getStrParameter("time_type");
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		String timeSize = getStrParameter("timeSize");
		String devType = getStrParameter("fdevicetype");
		String[] devices = checkStrArray(getStrArrayParameter("fdevice"), "multiselect-all");
		String fname = getStrParameter("fname");
		String[] prfid = getStrArrayParameter("fprfid");
		String fprfview = getStrParameter("viewname");
		StringBuffer devsb = new StringBuffer();
		for (int i = 0; i < devices.length; i++) {
			devsb.append(devices[i]);
			if (i < devices.length - 1) {
				devsb.append(",");
			}
		}
		StringBuffer prfsb = new StringBuffer();
		for (int i = 0; i < prfid.length; i++) {
			prfsb.append(prfid[i]);
			if (i < prfid.length - 1) {
				prfsb.append(",");
			}
		}
		DataRow row = new DataRow();
		row.set("name", name);
		row.set("fdevicetype", devType);
		row.set("fname", fname);
		row.set("fdevice", devsb.toString());
		row.set("fprfid", prfsb.toString());
		row.set("top_count", topnCount);
		row.set("timescope_type", timescopeType);
		row.set("time_length", timeLength);
		row.set("time_type", timeType);
		row.set("time_size", timeSize);
		if(startTime!=null && startTime.length()>0){
			row.set("starttime", startTime);
		}
		if(endTime!=null && endTime.length()>0){
			row.set("endtime", endTime);
		}
		row.set("fprfview", fprfview);
		row.set("create_time", new Date());
		JSONObject obj = new JSONObject();
		try {
			if(tid!=null && tid>0){
				service.updatePrfField(row, tid);
				obj.put("tid", tid);
			}else{
				tid = service.getTid();
				row.set("tid", tid);
				service.addTopn(row);
				obj.put("tid", tid);
			}
			obj.put("state", "true");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("state", "false");
		}
		ResponseHelper.print(getResponse(),obj);
	}
	
	/**
	 * 如果有多台设备取每台设备的平均值
	 * @param deviceType
	 * @param devices
	 * @param fname
	 */
	private boolean averageTopn(String deviceType,String[] devices,String fname){
		
		boolean flag=false;
		
		if(deviceType.equals("HOST")){
			if(devices.length>1){
				flag=true;
			}else{
				if(fname.equals("Physical")){
					flag=false;
				}else{
					flag=true;
				}
			}
			
		}
		else if(deviceType.equals("APPLICATION")){
			
			if(deviceType.length()>1){
				flag=true;
			}else{
				flag=false;
			}
			
		}
		else if(deviceType.equals("SVC")){
			
			if(devices.length>1){
				flag=true;
			}else{
				if(fname.equals("Storage")){
					flag=false;
				}else{
					flag=true;
				}
			}
			
		}
		else if(deviceType.equals("SWITCH")){
			
			if(devices.length>1){
				flag=true;
			}else{
				if(fname.equals("Switch")){
					flag=false;
				}else{
					flag=true;
				}
			}
		}
		
		return flag;
	}
	
	private String[] checkStrArray(String[] str, String mach) {
		if (str == null || str.length == 0) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (String string : str) {
			if (!string.equals(mach)) {
				list.add(string);
			}
			
		}
		return list.toArray(new String[list.size()]);
	}
	
	private String checkStr(String str){
		String strs = "";
		try {
			strs =  new String(str.replaceAll("&amp;nbsp;"," ").getBytes("iso-8859-1"),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}
	
}
