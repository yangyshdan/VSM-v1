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
import com.huiming.sr.constants.SrContant;
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
	
	/**
	 * TopN详细信息
	 * @return
	 */
	public ActionResult doTopnInfo() {
		Integer tid = getIntParameter("tid");
		DataRow row = service.getTopnInfo(tid);
		List<DataRow> devList = service.getdeviceList(row);
		//判断是否选择了多台设备,在取性能信息的时候分情况
		boolean flag = averageTopn(row.getString("fdevicetype"),row.getString("fdevice").split(","), row.getString("fname"));
		JSONArray array = null;
		if (flag) {
			array = service.getPrfJSON3(row);
		} else {
			array = service.getPrfJSON(row);
		}
		this.setAttribute("devList", devList);
		this.setAttribute("topnInfo", row);
		this.setAttribute("array", array);
		this.setAttribute("type", row.getString("fdevicetype"));
		return new ActionResult("/WEB-INF/views/topn/topnInfo.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doTopnSettingPrf() {
		Integer tid=getIntParameter("tid");
		DataRow historyConfig = service.getTopnInfo(tid);
		//获取设备列表
		JSONObject deviceList = new JSONObject();
		deviceList.put(SrContant.DEVTYPE_VAL_SVC, new JSONArray().fromObject(service.getStorageType(SrContant.DEVTYPE_VAL_SVC)));
		deviceList.put(SrContant.DEVTYPE_VAL_BSP, new JSONArray().fromObject(service.getStorageType(SrContant.DEVTYPE_VAL_BSP)));
		deviceList.put(SrContant.DEVTYPE_VAL_DS, new JSONArray().fromObject(service.getStorageType(SrContant.DEVTYPE_VAL_DS)));
		deviceList.put(SrContant.DEVTYPE_VAL_EMC, new JSONArray().fromObject(service.getStorageByType(SrContant.DEVTYPE_VAL_EMC)));
		deviceList.put(SrContant.DEVTYPE_VAL_HDS, new JSONArray().fromObject(service.getStorageByType(SrContant.DEVTYPE_VAL_HDS)));
		deviceList.put(SrContant.DEVTYPE_VAL_SWITCH, new JSONArray().fromObject(service.getSwitchList()));
//		deviceList.put("HOST", new JSONArray().fromObject(service.getHostList()));
//		deviceList.put("APPLICATION", new JSONArray().fromObject(service.getAppList()));
		
		//获取部件
		JSONObject fnameList = new JSONObject();
		fnameList.put(SrContant.DEVTYPE_VAL_SVC, new JSONArray().fromObject(service.getFnameList(SrContant.DEVTYPE_VAL_SVC)));
		fnameList.put(SrContant.DEVTYPE_VAL_BSP, new JSONArray().fromObject(service.getFnameList(SrContant.DEVTYPE_VAL_BSP)));
		fnameList.put(SrContant.DEVTYPE_VAL_DS, new JSONArray().fromObject(service.getFnameList(SrContant.DEVTYPE_VAL_DS)));
		fnameList.put(SrContant.DEVTYPE_VAL_EMC, new JSONArray().fromObject(service.getFnameList(SrContant.DEVTYPE_VAL_EMC)));
		fnameList.put(SrContant.DEVTYPE_VAL_HDS, new JSONArray().fromObject(service.getFnameList(SrContant.DEVTYPE_VAL_HDS)));
		fnameList.put(SrContant.DEVTYPE_VAL_SWITCH, new JSONArray().fromObject(service.getFnameList(SrContant.DEVTYPE_VAL_SWITCH)));
//		fnameList.put("HOST", new JSONArray().fromObject(service.getFnameList("HOST")));
//		fnameList.put("APPLICATION", new JSONArray().fromObject(service.getFnameList("APPLICATION")));
		
		//获取性能指标
		JSONObject fprfidList = new JSONObject();
		fprfidList.put(SrContant.DEVTYPE_VAL_SVC, new JSONArray().fromObject(service.getFprffildList(SrContant.DEVTYPE_VAL_SVC)));
		fprfidList.put(SrContant.DEVTYPE_VAL_BSP, new JSONArray().fromObject(service.getFprffildList(SrContant.DEVTYPE_VAL_BSP)));
		fprfidList.put(SrContant.DEVTYPE_VAL_DS, new JSONArray().fromObject(service.getFprffildList(SrContant.DEVTYPE_VAL_DS)));
		fprfidList.put(SrContant.DEVTYPE_VAL_EMC, new JSONArray().fromObject(service.getFprffildList(SrContant.DEVTYPE_VAL_EMC)));
		fprfidList.put(SrContant.DEVTYPE_VAL_HDS, new JSONArray().fromObject(service.getFprffildList(SrContant.DEVTYPE_VAL_HDS)));
		fprfidList.put(SrContant.DEVTYPE_VAL_SWITCH, new JSONArray().fromObject(service.getFprffildList(SrContant.DEVTYPE_VAL_SWITCH)));
//		fprfidList.put("HOST", new JSONArray().fromObject(service.getFprffildList("HOST")));
//		fprfidList.put("APPLICATION", new JSONArray().fromObject(service.getFprffildList("APPLICATION")));
		
		this.setAttribute("fprfidList", fprfidList);
		this.setAttribute("fnameList", fnameList);
		this.setAttribute("deviceList", deviceList);
		this.setAttribute("historyConfig", historyConfig);
		
		return new ActionResult("/WEB-INF/views/topn/editPage.jsp");
	}
	
	/**
	 * 删除TopN性能图设置信息
	 * @return
	 */
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
	
	/**
	 * 保存TopN性能图设置信息
	 */
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
		if(startTime != null && startTime.length() > 0){
			row.set("starttime", startTime);
		}
		if(endTime != null && endTime.length() > 0){
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
	 * 判断是否选择了多台设备,如果选择了多台设备后面就要取每台设备的平均值
	 * @param deviceType
	 * @param devices
	 * @param fname
	 */
	private boolean averageTopn(String deviceType,String[] devices,String fname){
		boolean flag = false;
		//For HOST
		if (deviceType.equals(SrContant.DEVTYPE_VAL_HOST)) {
			if (devices.length > 1) {
				flag = true;
			} else {
				if (fname.equals(SrContant.SUBDEVTYPE_PHYSICAL)) {
					flag = false;
				} else {
					flag = true;
				}
			}
		//For APPLICATION
		} else if (deviceType.equals(SrContant.DEVTYPE_VAL_APPLICATION)) {
			if (devices.length > 1) {
				flag = true;
			} else {
				flag = false;
			}
		//For BSP/DS/SVC
		} else if (deviceType.equals(SrContant.DEVTYPE_VAL_SVC)
				|| deviceType.equals(SrContant.DEVTYPE_VAL_BSP)
				|| deviceType.equals(SrContant.DEVTYPE_VAL_HDS)) {
			if (devices.length > 1) {
				flag = true;
			} else {
				if (fname.equals(SrContant.SUBDEVTYPE_STORAGE)) {
					flag = false;
				} else {
					flag = true;
				}
			}
		//For EMC/HDS
		} else if (deviceType.equals(SrContant.DEVTYPE_VAL_EMC)
				|| deviceType.equals(SrContant.DEVTYPE_VAL_HDS)) {
			if (devices.length > 1) {
				flag = true;
			} else {
				if (fname.equals(SrContant.SUBDEVTYPE_STORAGE)) {
					flag = false;
				} else {
					flag = true;
				}
			}
		//For SWITCH
		} else if (deviceType.equals(SrContant.DEVTYPE_VAL_SWITCH)) {
			if (devices.length > 1) {
				flag = true;
			} else {
				if (fname.equals(SrContant.SUBDEVTYPE_SWITCH)) {
					flag = false;
				} else {
					flag = true;
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
