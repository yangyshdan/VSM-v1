package root.zone;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.zone.ZoneService;
import com.huiming.service.zset.ZsetService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ZoneAction extends SecurityAction{
	ZoneService service = new ZoneService();
	public ActionResult doZonePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getZonePage(curPage, numPerPage, null, null, null,null,null);
		this.setAttribute("zonePage", page);
		return new ActionResult("/WEB-INF/views/zone/zoneList.jsp");
	}
	
	public ActionResult doAjaxZonePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String zsetId = getStrParameter("zsetId");
		String name = getStrParameter("name");
		String wwn = getStrParameter("wwn");
		String active = getStrParameter("active");
		String zoneType = getStrParameter("zoneType");
		page = service.getZonePage(curPage, numPerPage, name, wwn, active,zoneType,zsetId);
		this.setAttribute("zonePage", page);
		this.setAttribute("name", name);
		this.setAttribute("wwn", wwn);
		this.setAttribute("active", active);
		this.setAttribute("zoneType", zoneType);
		this.setAttribute("zsetId", zsetId);
		return new ActionResult("/WEB-INF/views/zone/ajaxZone.jsp");
	}
	
	public ActionResult doZone2ZsetPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String zsetId = getStrParameter("zsetId");
		ZsetService zsetService = new ZsetService();
		page = zsetService.getzonePage(zsetId, curPage, numPerPage);
		this.setAttribute("zonePage", page);
		this.setAttribute("zsetId", zsetId);
		return new ActionResult("/WEB-INF/views/zone/ajaxZone.jsp");
	}
	
	public ActionResult doZoneInfo(){
		Integer zoneId = getIntParameter("zoneId");
		DataRow row = service.getZoneInfo(zoneId);
		this.setAttribute("zoneInfo", row);
		return new ActionResult("/WEB-INF/views/zone/zoneInfo.jsp");
	}
	
	public void doExportZoneConfigData(){
		String name = getStrParameter("name");
		String wwn = getStrParameter("wwn");
		String active = getStrParameter("active");
		String zoneType = getStrParameter("zoneType");
		List<DataRow> rows = service.getZoneList(name, wwn, active,zoneType);
		if(rows!=null && rows.size()>0){
			List<DataRow> rows2 = new ArrayList<DataRow>();
			for (DataRow dataRow : rows) {
				String type = dataRow.getString("zone_type");
				String isactive = dataRow.getString("active");
				if("1".equals(type)){
					dataRow.set("zone_type", "Soft");
				}
				if("2".equals(type)){
					dataRow.set("zone_type", "Hard");
				}
				if("3".equals(type)){
					dataRow.set("zone_type", "None");
				}
				if("0".equals(isactive)){
					dataRow.set("active", "否");
				}else{
					dataRow.set("active", "是");
				}
				rows2.add(dataRow);
			}
			String[] title = new String[]{"名称","类型","是否活动","WWN","描述"};
			String[] keys = new String[]{"the_display_name","zone_type","active","fabric_wwn","description"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "ZONE-DATA", rows2, title, keys);
		}
	}
}