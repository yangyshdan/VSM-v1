package root.fabric;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.fabric.FabricService;
import com.huiming.service.zset.ZsetService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class FabricAction extends SecurityAction{
	FabricService service = new FabricService();
	
	/**
	 * 获取Fabric网络列表
	 * @return
	 */
	public ActionResult doFabricPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		//获取用户可见的Fabric网络
		String limitIds = (String) getSession().getAttribute(WebConstants.FABRIC_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = service.getfabricPage(null, null, curPage, numPerPage, limitIds);
		}
		setAttribute("fabricPage", page);
		return new ActionResult("/WEB-INF/views/fabric/fabricList.jsp");
	}
	
	/**
	 * Fabric网络分页查询
	 * @return
	 */
	public ActionResult doAjaxFabricPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String status = getStrParameter("status");
		//获取用户可见的Fabric网络
		String limitIds = (String) getSession().getAttribute(WebConstants.FABRIC_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = service.getfabricPage(name, status, curPage, numPerPage, limitIds);
		}
		setAttribute("name", name);
		setAttribute("status", status);
		setAttribute("fabricPage", page);
		return new ActionResult("/WEB-INF/views/fabric/ajaxFabric.jsp");
	}
	
	/**
	 * 导出Fabric网络配置数据
	 */
	public void doExportFabricConfigData(){
		String name  = getStrParameter("name");
		String status = getStrParameter("status");
		//获取用户可见的Fabric网络
		String limitIds = (String) getSession().getAttribute(WebConstants.FABRIC_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			List<DataRow> rows = service.getfabricList(name,status,limitIds);
			if (rows != null && rows.size() > 0) {
				List<DataRow> rows2 = new ArrayList<DataRow>();
				for (DataRow dataRow : rows) {
					String isactive = dataRow.getString("supports_zoning");
					if ("0".equals(isactive)) {
						dataRow.set("supports_zoning", "否");
					} else {
						dataRow.set("supports_zoning", "是");
					}
					rows2.add(dataRow);
				}
				String[] title = new String[]{"名称","WWN","ZoneSet名称 ","Zone数量","SAN交换机数 ","端口数 ","已连接端口数 ","状态 "};
				String[] keys = new String[]{"the_display_name","fabric_wwn","zset_name","the_zone_count","the_switch_count","the_port_count","the_connected_port_count","the_propagated_status"};
				getResponse().setCharacterEncoding("GBK");
				CSVHelper.createCSVToPrintWriter(getResponse(), "Fabric_Config_Data", rows2, title, keys);
			}
		}
	}
	
	/**
	 * Fabric网络详细页面
	 * @return
	 */
	public ActionResult doFabricInfo(){
		DBPage page = null;
		String fabricId = getStrParameter("fabricId");
		DataRow row = service.getFabricInfo(fabricId);
		//获取该Fabric网络下可见的交换机
		String limitIds1 = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_SWITCH, null, Integer.parseInt(fabricId));
		page = service.getSwitchPage(fabricId, 1, WebConstants.NumPerPage, limitIds1);
		//获取该Fabric网络下可见的ZoneZet
		String limitIds2 = getUserDefinedDeviceIds(SrContant.DEVTYPE_VAL_ZONESET, null, Integer.parseInt(fabricId));
		DBPage zsetPage = new ZsetService().getzsetPage(null, null, fabricId, 1, WebConstants.NumPerPage, limitIds2);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage = deviceService.getLogPage(1, WebConstants.NumPerPage, -1, fabricId,null,fabricId, null, "Fabric", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		
		this.setAttribute("switchPage", page);
		this.setAttribute("switchCount", page.getTotalRows());
		this.setAttribute("zsetCount", zsetPage.getTotalRows());
		this.setAttribute("zsetPage", zsetPage);
		this.setAttribute("fabricId", fabricId);
		this.setAttribute("fabricInfo", row);
		return new ActionResult("/WEB-INF/views/fabric/fabricInfo.jsp");
	}
	
	/**
	 * 加载交换机列表
	 * @return
	 */
	public ActionResult doAjaxSwitchPage(){
		DBPage page = null;
		String fabricId = getStrParameter("fabricId");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		//获取该Fabric网络下可见的交换机
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_SWITCH, null, Integer.parseInt(fabricId));
		page = service.getSwitchPage(fabricId, curPage, numPerPage, limitIds);
		this.setAttribute("switchPage", page);
		this.setAttribute("fabricId", fabricId);
		return new ActionResult("/WEB-INF/views/fabric/ajaxSwitch.jsp");
	}
	
}
