package root.zset;

import java.util.ArrayList;
import java.util.List;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.zset.ZsetService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ZsetAction extends SecurityAction{
	ZsetService service = new ZsetService();
	
	/**
	 * 加载ZoneZet信息列表
	 * @return
	 */
	public ActionResult doZsetPage() {
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		//获取用户可见的ZoneZet
		String limitIds = (String) getSession().getAttribute(WebConstants.ZONEZET_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = service.getzsetPage(null,null, null,curPage, numPerPage, limitIds);
		}
		setAttribute("zsetPage", page);
		return new ActionResult("/WEB-INF/views/zset/zsetList.jsp");
	}
	
	/**
	 * 分页查询ZoneZet数据
	 * @return
	 */
	public ActionResult doAjaxZsetPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name  = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String active  = getStrParameter("active").replaceAll("&amp;nbsp;", " ");
		String fabricId  = getStrParameter("fabricId");
		setAttribute("name", name);
		setAttribute("active", active);
		//获取可见的ZoneZet
		Integer fabId = null;
		if (StringHelper.isNotEmpty(fabricId) && StringHelper.isNotBlank(fabricId)) {
			fabId = Integer.parseInt(fabricId);
		}
		String limitIds = getUserDefinedDeviceIds(SrContant.DEVTYPE_VAL_ZONESET, null, fabId);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = service.getzsetPage(name,active,fabricId, curPage, numPerPage, limitIds);
		}
		setAttribute("zsetPage", page);
		setAttribute("fabricId", fabricId);
		return new ActionResult("/WEB-INF/views/zset/ajaxZset.jsp");
	}
	
	/**
	 * ZoneZet详细页面
	 * @return
	 */
	public ActionResult doZsetInfo(){
		String zsetId = getStrParameter("zsetId");
		DataRow row = service.getZsetInfo(zsetId);
		//获取该ZoneZet下可见的Zone
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_ZONE, null, Integer.parseInt(zsetId));
		DBPage page = service.getZonePage(zsetId, 1, WebConstants.NumPerPage, limitIds);
		this.setAttribute("zsetId", zsetId);
		this.setAttribute("zsetInfo", row);
		this.setAttribute("zonePage", page);
		this.setAttribute("zoneCount", page.getTotalRows());
		return new ActionResult("/WEB-INF/views/zset/zsetInfo.jsp");
	}
	
	/**
	 * 导出Zone数据
	 */
	public void doExportZoneConfigData(){
		String zsetId = getStrParameter("zsetId");
		//获取该ZoneZet下可见的Zone
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_ZONE, null, Integer.parseInt(zsetId));
		List<DataRow> rows = service.getZoneList(zsetId,limitIds);
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
	
	/**
	 * 导出ZoneZet数据
	 */
	public void doExportZsetConfigData(){
		String name  = getStrParameter("name");
		String active  = getStrParameter("active");
		//获取用户可见的ZoneZet
		String limitIds = (String) getSession().getAttribute(WebConstants.ZONEZET_LIST);
		List<DataRow> rows = service.getZsetList(name,active,limitIds);
		if (rows != null && rows.size() > 0) {
			List<DataRow> rows2 = new ArrayList<DataRow>();
			for (DataRow dataRow : rows) {
				String isactive = dataRow.getString("active");
				if ("0".equals(isactive)) {
					dataRow.set("active", "否");
				} else {
					dataRow.set("active", "是");
				}
				rows2.add(dataRow);
			}
			String[] title = new String[]{"名称","Zone数量","Fabric网络","是否活动"};
			String[] keys = new String[]{"the_display_name","the_zone_count","f_name","active"};
			getResponse().setCharacterEncoding("GBK");
			CSVHelper.createCSVToPrintWriter(getResponse(), "ZoneZet_Config_Data", rows2, title, keys);
		}
	}
}
