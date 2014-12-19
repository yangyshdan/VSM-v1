package root.alert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class DeviceAlertAction extends SecurityAction{
	public ActionResult doDefault(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DeviceAlertService service = new DeviceAlertService();
		String resourceType = getStrParameter("type",null);
		setAttribute("dbPage", service.getLogPage(curPage, numPerPage, -1, null,null,null,null, resourceType, -1, -1, null, null));
		setAttribute("resourceType", resourceType);
		return new ActionResult("/WEB-INF/views/alert/deviceAlert.jsp");
	}
	
	public ActionResult doAjaxPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		int level = getIntParameter("level",-1);
		int logtype = getIntParameter("logType",-1);
		int state = getIntParameter("state",-1);
		String resourName = null;
		String resourceId=getStrParameter("resourceId");
		String topId=getStrParameter("topId");
		try {
			resourName = URLDecoder.decode(getStrParameter("resourceName"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String resourType = getStrParameter("resourceType");
		String startDate = URLDecoder.decode(getStrParameter("startDate"));
		String endDate = URLDecoder.decode(getStrParameter("endDate"));
		DeviceAlertService service = new  DeviceAlertService();
		//page = service.getLogPage(curPage, numPerPage, logtype,topId,null,resourceId,resourName, resourType, state, level, startDate, endDate);
		page = service.getLogPage(curPage, numPerPage, logtype,topId,null,null,resourName, resourType, state, level, startDate, endDate);
		setAttribute("dbPage", page);
		setAttribute("resourceId", resourceId);
		setAttribute("topId", topId);
		setAttribute("resourceType", resourType);
		return new ActionResult("/WEB-INF/views/alert/ajaxDAlert.jsp");
	}
	
	public ActionResult doPrepareEdit(){
		int id = getIntParameter("id",-1);
		DataRow row = null;
		if(id != -1){
			DeviceAlertService service = new DeviceAlertService();
			row = service.getAlertById(id);
		}
		setAttribute("dAlert", row);
		return new ActionResult("/WEB-INF/views/alert/dAlertInfo.jsp");
	}
	
	public void doDelAlert(){
		String ids = getStrParameter("ids","");
		if(StringHelper.isNotEmpty(ids)){
			try {
				DeviceAlertService service = new DeviceAlertService();
				service.deleteAlert(ids);
				ResponseHelper.print(getResponse(), "true");
			} catch (Exception e) {
				e.printStackTrace();
				ResponseHelper.print(getResponse(), "false");
			}
		}
		ResponseHelper.print(getResponse(), "false");
	}
	
//	public JSONObject getShareJson(){
//		DeviceAlertService service = new DeviceAlertService();
//		JSONObject shareJson = new JSONObject();
//		JSONArray levelShare = new JSONArray();
//		for (DataRow row : service.getLevelShare()) {
//			JSONObject share = new JSONObject();
//			switch (row.getInt("flevel")) {
//			case 0:
//				share.put("name","Info");
//				share.put("color", "#999999");
//				break;
//			case 1:
//				share.put("name","Warning");
//				share.put("color", "#EFB73E");
//				break;
//			case 2:
//				share.put("name","Critical");
//				share.put("color", "#BD4247");
//				break;
//			default:
//				break;
//			}
//			share.put("y",row.getInt("sharey"));
//			levelShare.add(share);
//		}
//		shareJson.put("levelShare", levelShare);
//		
//		//level & resource share
//		String[] typeArray = new String[]{"Storage","Switch","Port","SwitchPort","Volume","ArraySite","Node","MdiskGroup","Mdisk","IOGroup","Controller","Rank"};
//		JSONArray infoArray = new JSONArray();
//		JSONArray warnArray = new JSONArray();
//		JSONArray errorArray = new JSONArray();
//		List<DataRow> shares = service.getAllShare();
//		for (String type : typeArray) {
//			for (int level : new int[]{0,1,2}) {
//				int share = 0;
//				for (DataRow row : shares) {
//					if(row.getString("fresourcetype").equalsIgnoreCase(type)&&row.getInt("flevel") == level){
//						share = row.getInt("sharey");
//						shares.remove(row);
//						break;
//					}
//				}
//				switch (level) {
//				case 0:
//					infoArray.add(share);
//					break;
//				case 1:
//					warnArray.add(share);
//					break;
//				case 2:
//					errorArray.add(share);
//					break;
//
//				default:
//					break;
//				}
//			}
//		}
//		JSONArray series = new JSONArray();
//		JSONObject info = new JSONObject();
//		info.put("name", "Info");
//		info.put("data", infoArray);
//		info.put("color", "#999999");
//		series.add(info);
//		JSONObject warn = new JSONObject();
//		warn.put("name", "Warning");
//		warn.put("data", warnArray);
//		warn.put("color", "#EFB73E");
//		series.add(warn);
//		JSONObject error = new JSONObject();
//		error.put("name", "Critical");
//		error.put("data", errorArray);
//		error.put("color", "#BD4247");
//		series.add(error);
//		shareJson.put("categories", new JSONArray().fromArray(typeArray));
//		shareJson.put("series", series);
//		return shareJson;
//	}
	
	public void doDisposeAlert(){
		DeviceAlertService service = new DeviceAlertService();
		String ruleId = getStrParameter("fruleid");
		if(service.updateAlert(ruleId)){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public ActionResult doDeviceInfo() {
		DeviceAlertService service = new DeviceAlertService();
		String ruleId = getStrParameter("ruleId");
		String topId = getStrParameter("topId");
		String resourType = getStrParameter("resourceType").endsWith("undefined")?"":getStrParameter("resourceType");
		this.setAttribute("dAlert", service.getalertbyId(ruleId,topId));
		setAttribute("resourceType", resourType);
		return new ActionResult("/WEB-INF/views/alert/deviceAlertInfo.jsp");
	}
}
