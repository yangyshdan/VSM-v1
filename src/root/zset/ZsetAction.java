package root.zset;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.AlertService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.zset.ZsetService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ZsetAction extends SecurityAction{
	ZsetService service = new ZsetService();
	
	public ActionResult doZsetPage() {
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getzsetPage(null,null, null,curPage, numPerPage);
		this.setAttribute("zsetPage", page);
		return new ActionResult("/WEB-INF/views/zset/zsetList.jsp");
	}
	
	public ActionResult doAjaxZsetPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name  = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String active  = getStrParameter("active").replaceAll("&amp;nbsp;", " ");
		String fabricId  = getStrParameter("fabricId");
		this.setAttribute("name", name);
		this.setAttribute("active", active);
		page = service.getzsetPage(name,active,fabricId, curPage, numPerPage);
		this.setAttribute("zsetPage", page);
		this.setAttribute("fabricId", fabricId);
		return new ActionResult("/WEB-INF/views/zset/ajaxZset.jsp");
	}
	
	public ActionResult doZsetInfo(){
		String zsetId = getStrParameter("zsetId");
		DataRow row = service.getZsetInfo(zsetId);
		DBPage page = service.getzonePage(zsetId, 1, WebConstants.NumPerPage);
		this.setAttribute("zsetId", zsetId);
		this.setAttribute("zsetInfo", row);
		this.setAttribute("zonePage", page);
		this.setAttribute("zoneCount", page.getTotalRows());
		return new ActionResult("/WEB-INF/views/zset/zsetInfo.jsp");
	}
	
	public void doExportZoneConfigData(){
		String zsetId = getStrParameter("zestId");
		List<DataRow> rows = service.getzoneList(zsetId);
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
	
	public void doExportZsetConfigData(){
		String name  = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String active  = getStrParameter("active").replaceAll("&amp;nbsp;", " ");
		List<DataRow> rows = service.getzsetList(name,active);
		if(rows!=null && rows.size()>0){
			List<DataRow> rows2 = new ArrayList<DataRow>();
			for (DataRow dataRow : rows2) {
				String isactive = dataRow.getString("active");
				if("0".equals(isactive)){
					dataRow.set("active", "否");
				}else{
					dataRow.set("active", "是");
				}
				rows2.add(dataRow);
			}
			String[] title = new String[]{"名称","区域数","光纤","是否活动"};
			String[] keys = new String[]{"the_display_name","the_zone_count","f_name","active"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "ZONESET-DATA", rows2, title, keys);
		}
	}
}
