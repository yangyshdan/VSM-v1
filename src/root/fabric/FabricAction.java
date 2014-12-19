package root.fabric;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.AlertService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.fabric.FabricService;
import com.huiming.service.zset.ZsetService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class FabricAction extends SecurityAction{
	FabricService service = new FabricService();
	public ActionResult doFabricPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getfabricPage(null, curPage, numPerPage);
		this.setAttribute("fabricPage", page);
		return new ActionResult("/WEB-INF/views/fabric/fabricList.jsp");
	}
	
	public ActionResult doAjaxFabricPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		page = service.getfabricPage(name, curPage, numPerPage);
		this.setAttribute("name", name);
		this.setAttribute("fabricPage", page);
		return new ActionResult("/WEB-INF/views/fabric/ajaxFabric.jsp");
	}
	
	public void doExportFabricConfigData(){
		String name  = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		List<DataRow> rows = service.getfabricList(name);
		if(rows!=null && rows.size()>0){
			List<DataRow> rows2 = new ArrayList<DataRow>();
			for (DataRow dataRow : rows2) {
				String isactive = dataRow.getString("supports_zoning");
				if("0".equals(isactive)){
					dataRow.set("supports_zoning", "否");
				}else{
					dataRow.set("supports_zoning", "是");
				}
				rows2.add(dataRow);
			}
			String[] title = new String[]{"名称","WWN","交换机数","端口数","已连接端口数","支持区域","状态"};
			String[] keys = new String[]{"the_display_name","fabric_wwn","the_switch_count","the_port_count","the_connected_port_count","supports_zoning","the_propagated_status"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "ZONESET-DATA", rows2, title, keys);
		}
	}
	
	public ActionResult doFabricInfo(){
		DBPage page = null;
		String fabricId = getStrParameter("fabricId");
		DataRow row = service.getFabricInfo(fabricId);
		page = service.getSwitchPage(fabricId, 1, WebConstants.NumPerPage);
		DBPage zsetPage = new  ZsetService().getzsetPage(null, null, fabricId, 1, WebConstants.NumPerPage);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1, fabricId,null,null, null, "Fabric", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		
		this.setAttribute("switchPage", page);
		this.setAttribute("switchCount", page.getTotalRows());
		this.setAttribute("zsetCount", zsetPage.getTotalRows());
		this.setAttribute("zsetPage", zsetPage);
		this.setAttribute("fabricId", fabricId);
		this.setAttribute("fabricInfo", row);
		return new ActionResult("/WEB-INF/views/fabric/fabricInfo.jsp");
	}
	
	public ActionResult doAjaxSwitchPage(){
		DBPage page = null;
		String fabricId = getStrParameter("fabricId");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		page = service.getSwitchPage(fabricId, curPage, numPerPage);
		this.setAttribute("switchPage", page);
		this.setAttribute("fabricId", fabricId);
		return new ActionResult("/WEB-INF/views/fabric/ajaxSwitch.jsp");
	}
	
	public void doExportSwitchConfigData(){
		String fabricId = getStrParameter("fabricId");
		List<DataRow> rows = service.getSwitchList(fabricId);
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","状态","域ID","IP地址","WWN","序列号","描述","更新时间"};
			String[] keys = new String[]{"the_display_name","the_propagated_status","domain","ip_address","switch_wwn","serial_number","description","update_timestamp"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "SWITCH-CONFIG-DATA", rows, title, keys);
		}
	}
	
}
