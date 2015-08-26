package root.sr.pool;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.sr.pool.PoolService;
import com.huiming.service.sr.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;

public class PoolAction extends SecurityAction {
	
	PoolService service = new PoolService();

	@SuppressWarnings({ "static-access"})
	//pool分页查询
	public ActionResult doPoolPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",SrContant.SR_NumPerPage);
		String system_id = getStrParameter("subSystemID");
		page = service.getPoolPage(curPage, numPerPage,null,null,null,system_id);
		
		List<DataRow> data=service.getCapacity(system_id);
		List<Object> name = new ArrayList<Object>();
		List<Object> capacity = new ArrayList<Object>();
		for(int i=0;i<data.size();i++){
			name.add("POOL "+data.get(i).getString("name"));
			capacity.add(data.get(i).getInt("total_usable_capacity")/1024);
			
			JSONArray name1 = new JSONArray().fromObject(name);
			JSONArray capacity1 = new JSONArray().fromObject(capacity);	
			
			Map<Object,Object> map =new HashMap<Object,Object>();
			map.put("name", "总容量");
			map.put("data", capacity1);
			
			JSONArray map1 = new JSONArray().fromObject(map);
			this.setAttribute("map1", map1);
			this.setAttribute("name1", name1);
			this.setAttribute("dbPage", page);
		}
		this.setAttribute("subSystemID", system_id);
		return new ActionResult("/WEB-INF/views/sr/pool/poolList.jsp");

	}
	//分页查询
	public ActionResult doAjaxPoolPage(){
		DBPage page = null;
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		name = name.replaceAll("POOL ", "");
		Long greatCapacity = getLongParameter("greatCapacity");
		Long lessCapacity = getLongParameter("lessCapacity");
		if (greatCapacity == 0) {
			greatCapacity = null;
		}
		if (lessCapacity == 0) {
			lessCapacity = null;
		}
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",SrContant.SR_NumPerPage);
		String system_id = getStrParameter("subSystemID");
		String storageType = getStrParameter("storageType");
		page = service.getPoolPage(curPage, numPerPage,name,greatCapacity,lessCapacity,system_id);
		setAttribute("name", name);
		setAttribute("greatCapacity", greatCapacity);
		setAttribute("lessCapacity", lessCapacity);
		setAttribute("dbPage", page);
		setAttribute("subSystemID", system_id);
		setAttribute("storageType", storageType);
		return new ActionResult("/WEB-INF/views/sr/pool/ajaxPool.jsp");
	}
	/**
	 * 查询存储池的详细信息
	 * @return
	 */
	public ActionResult doPoolInfo(){
		DataRow pool = null;
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",SrContant.SR_NumPerPage);
		String poolId = getStrParameter("poolId");
		String subSystemID = getStrParameter("subSystemID");
		String storageType = getStrParameter("storageType");
		if (poolId.length() <= 0) {
			return null;
		} else {
			//根据id查询存储池的信息
			pool = service.getPoolById(poolId, subSystemID, null);
			//容量
			doCapacityInfo();
			VolumeService services = new VolumeService();
			page = services.getVolumePage(curPage, numPerPage, null, null, null, poolId, subSystemID);
		}
		setAttribute("poolInfo", pool);	
		setAttribute("dbPage", page);
		setAttribute("poolId", poolId);
		setAttribute("subSystemID", subSystemID);
		setAttribute("storageType", storageType);
		return new ActionResult("/WEB-INF/views/sr/pool/poolInfo.jsp");
	}
	
	public void doCapacityInfo(){
		String id = getStrParameter("poolId");
		String subsystemId = getStrParameter("subSystemID");
		DataRow row = service.getPoolById(id,subsystemId,null);//根据pool_id查询存储池的信息
		Double theSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("total_usable_capacity")/1024));;
		Double availableSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("unallocated_capacity")/1024));
		Double usedSpace = Double.parseDouble(new DecimalFormat("0.00").format((theSpace-availableSpace)));
		Double perUsedSpace = Double.parseDouble(new DecimalFormat("0.00").format(usedSpace/theSpace*100));
		Double perAvailableSpace = Double.parseDouble(new DecimalFormat("0.00").format(availableSpace/theSpace*100));
		
		JSONObject json = new JSONObject();
		json.put("usedSpace", usedSpace);
		json.put("availableSpace", availableSpace);
		json.put("perUsedSpace", perUsedSpace);
		json.put("perAvailableSpace", perAvailableSpace);
		json.put("the_display_name", "POOL "+row.getString("name"));
		this.setAttribute("jsonVal", json);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
	}
	
	
	//导出存储池配置信息
	public void doExportPoolConfigData(){
		String name = getStrParameter("name");
		Long greatCapacity = getLongParameter("greatCapacity");
		Long lessCapacity = getLongParameter("lessCapacity");
		String subSystemID = getStrParameter("subSystemID");
		
		List<DataRow> rows = service.doExportConfigDatas(name,greatCapacity,lessCapacity,subSystemID);
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"存储池名称","阵列类型","总逻辑容量(MB)","可用逻辑容量(MB)","LUN数量","后端磁盘数量"};
			String[] keys = new String[]{"name","raid_level","total_usable_capacity","unallocated_capacity","num_lun","num_backend_disk"};
			getResponse().setCharacterEncoding("GBK");
			CSVHelper.createCSVToPrintWriter(getResponse(), "PoolConfigData", rows, title, keys);
		}
	}	
	
	private void writeDataToPage(String data) {
		PrintWriter writer = null;
		try {
			getResponse().setCharacterEncoding("UTF-8");
			writer = getResponse().getWriter();
			writer.print(data);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		}
	}
}
