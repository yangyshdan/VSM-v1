package root.disk;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.disk.DiskService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class DiskAction extends SecurityAction{
	DiskService service = new DiskService();
	@SuppressWarnings("static-access")
	public ActionResult doDiskPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		page = service.getDiskPage(curPage, numPerPage, null, null, null, subsystemId);
		List<DataRow> rows = service.getDiskCap(subsystemId);
		Map<Object,Object> map = new HashMap<Object,Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add(dataRow.getString("the_display_name"));
			dataJson.put("diskId", dataRow.getInt("physical_volume_id"));
			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("the_capacity"))));
			jarray.add(dataJson);
		}
		map.put("name", "容量");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);  
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("diskPage", page);
		this.setAttribute("subSystemID", subsystemId);
		return new ActionResult("/WEB-INF/views/disk/diskList.jsp");
	}
	
	public ActionResult doAjaxDiskPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		Integer startCap = getIntParameter("startCap");
		Integer endCap = getIntParameter("endCap");
		page = service.getDiskPage(curPage, numPerPage, name, startCap, endCap, subsystemId);
		this.setAttribute("diskPage", page);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("name", name);
		this.setAttribute("startCap", startCap);
		this.setAttribute("endCap", endCap);
		return new ActionResult("/WEB-INF/views/disk/ajaxDisk.jsp");
	}
	
	public ActionResult doDiskInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer diskId = getIntParameter("diskId");
		DataRow row = service.getDiskInfo(diskId);
		
		this.setAttribute("diskInfo", row);
		this.setAttribute("subSystemID", subsystemId);
		return new ActionResult("/WEB-INF/views/disk/diskInfo.jsp");
	}
	
	public void doExportDiskConfigData(){
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");;
		Integer startCap = getIntParameter("startCap");
		Integer endCap = getIntParameter("endCap");
		List<DataRow> rows = service.getDiskList(name, startCap, endCap, subsystemId);
		String subName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","阵列","厂商","型号","序列号","固件版本","运行状态","硬件状态","转速","容量(G)"};
			String[] keys = new String[]{"the_display_name","sub_name","diskgroup_name","vendor_name","model_name","serial_number","firmware_rev","the_operational_status","the_consolidated_status","speed","the_capacity"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-Disks", rows, title, keys);
		}
	}
	
}
