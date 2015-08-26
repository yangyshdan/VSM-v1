package root.zone;

import java.util.ArrayList;
import java.util.List;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.zone.ZoneService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ZoneAction extends SecurityAction{
	ZoneService service = new ZoneService();
	
	/**
	 * 加载Zone信息列表
	 * @return
	 */
	public ActionResult doZonePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		//获取用户可见的Zone
		String limitIds = (String) getSession().getAttribute(WebConstants.ZONE_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = service.getZonePage(curPage,numPerPage,null,null,null,null,null,limitIds);
		}
		setAttribute("zonePage", page);
		return new ActionResult("/WEB-INF/views/zone/zoneList.jsp");
	}
	
	/**
	 * 分页查询Zone数据
	 * @return
	 */
	public ActionResult doAjaxZonePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String zsetId = getStrParameter("zsetId");
		String name = getStrParameter("name");
		String wwn = getStrParameter("wwn");
		String active = getStrParameter("active");
		String zoneType = getStrParameter("zoneType");
		//获取可见的Zone
		Integer zoneZetId = null;
		if (StringHelper.isNotEmpty(zsetId) && StringHelper.isNotBlank(zsetId)) {
			zoneZetId = Integer.parseInt(zsetId);
		}
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_ZONE, null, zoneZetId);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = service.getZonePage(curPage, numPerPage, name, wwn, active, zoneType, zsetId, limitIds);
		}
		this.setAttribute("zonePage", page);
		this.setAttribute("name", name);
		this.setAttribute("wwn", wwn);
		this.setAttribute("active", active);
		this.setAttribute("zoneType", zoneType);
		this.setAttribute("zsetId", zsetId);
		return new ActionResult("/WEB-INF/views/zone/ajaxZone.jsp");
	}

	/**
	 * Zone详细信息页面
	 * @return
	 */
	public ActionResult doZoneInfo(){
		Integer zoneId = getIntParameter("zoneId");
		DataRow row = service.getZoneInfo(zoneId);
		setAttribute("zoneInfo", row);
		return new ActionResult("/WEB-INF/views/zone/zoneInfo.jsp");
	}
	
	/**
	 * 导出Zone配置信息数据
	 */
	public void doExportZoneConfigData(){
		String name = getStrParameter("name");
		String wwn = getStrParameter("wwn");
		String active = getStrParameter("active");
		String zoneType = getStrParameter("zoneType");
		//获取用户可见的Zone
		String limitIds = (String) getSession().getAttribute(WebConstants.ZONE_LIST);
		List<DataRow> rows = service.getZoneList(name, wwn, active,zoneType, limitIds);
		if (rows != null && rows.size() > 0) {
			List<DataRow> rows2 = new ArrayList<DataRow>();
			for (DataRow dataRow : rows) {
				String type = dataRow.getString("zone_type");
				String isactive = dataRow.getString("active");
				if ("1".equals(type)) {
					dataRow.set("zone_type", "Soft");
				} else if ("2".equals(type)) {
					dataRow.set("zone_type", "Hard");
				} else if ("3".equals(type)) {
					dataRow.set("zone_type", "None");
				}
				if ("0".equals(isactive)) {
					dataRow.set("active", "否");
				} else {
					dataRow.set("active", "是");
				}
				rows2.add(dataRow);
			}
			String[] title = new String[]{"名称","类型","Fabric网络","WWNN","是否活动","描述"};
			String[] keys = new String[]{"the_display_name","zone_type","fabric_name","fabric_wwn","active","description"};
			getResponse().setCharacterEncoding("GBK");
			CSVHelper.createCSVToPrintWriter(getResponse(), "Zone_Config_Data", rows2, title, keys);
		}
	}
}