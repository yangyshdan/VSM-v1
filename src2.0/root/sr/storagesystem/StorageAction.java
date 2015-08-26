package root.sr.storagesystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.sr.diskgroup.DiskgroupService;
import com.huiming.service.sr.hostgroup.HostgroupService;
import com.huiming.service.sr.node.NodeService;
import com.huiming.service.sr.port.PortService;
import com.huiming.service.sr.storagesystem.StorageSystemService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class StorageAction extends SecurityAction{
	BaseprfService baseService = new BaseprfService();
	PortService portService = new PortService();
	NodeService nodeService = new NodeService();
	DiskgroupService diskgroupService = new DiskgroupService();
	HostgroupService hostService = new HostgroupService();
	StorageSystemService service = new StorageSystemService();
	
	@SuppressWarnings("static-access")
	public ActionResult doStoragePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",SrContant.SR_NumPerPage);
		StorageSystemService service = new StorageSystemService();
		//获取用户可见存储
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		page = service.getStoragePage(curPage,numPerPage,null,null,null,null,null,null,null,null,srLimitIds);
		this.setAttribute("dbPage", page);
		
		// 存储容量柱形图
		List<DataRow> rows = service.getStorageCapacityInfo(srLimitIds);
		JSONArray storageName = new JSONArray(); // 存储系统名称
		JSONArray allocatedCapacity = new JSONArray(); // 已用容量
		JSONArray availableCapacity = new JSONArray(); // 可用空间
		Map<Object,Object> mapAllocatedCapacity = new HashMap<Object,Object>();
		Map<Object,Object> mapAvailableCapacity = new HashMap<Object,Object>();
		for (DataRow row : rows) {
			storageName.add(row.getString("model"));
			allocatedCapacity.add((row.getInt("total_usable_capacity")-row.getInt("unallocated_usable_capacity"))/1024);
			availableCapacity.add(row.getInt("unallocated_usable_capacity")/1024);
		}
		mapAllocatedCapacity.put("name", "已用容量");
		mapAllocatedCapacity.put("data", allocatedCapacity);
		mapAllocatedCapacity.put("color", "#2677f0");
		mapAvailableCapacity.put("name", "可用容量");
		mapAvailableCapacity.put("data", availableCapacity);
		mapAvailableCapacity.put("color", "#00ff00");

		JSONArray jsArray = new JSONArray().fromObject(mapAllocatedCapacity);
		jsArray.add(mapAvailableCapacity);
		this.setAttribute("storageName", storageName);
		this.setAttribute("categories", jsArray);
		
		return new ActionResult("/WEB-INF/views/storage/storageList.jsp");
	}
	
	public ActionResult doAjaxStoragePage(){
		DBPage page = null;
		String name = getStrParameter("name");
		String ip = getStrParameter("ip");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",SrContant.SR_NumPerPage);
		StorageSystemService service = new StorageSystemService();
		//获取用户可见存储
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		page = service.getStoragePage(curPage, numPerPage,name,ip,null,null,null,null,null,null,srLimitIds);

		this.setAttribute("name", name);
		this.setAttribute("ip", ip);
		this.setAttribute("dbPage", page);
		return new ActionResult("/WEB-INF/views/storage/ajaxStorage.jsp");
	}
	
	/**
	 * 存储系统详细信息,性能,部件信息
	 * @return
	 */
	public ActionResult doStorageInfo(){
		//设置默认页码和数据量
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",SrContant.SR_NumPerPage);
		
		DataRow storage = null;
		String subSystemID = getStrParameter("subSystemID");    //存储系统ID
		if (subSystemID.length() <= 0) {
			this.addActionError("未找到相关信息，请稍后再试！");
		} else {
			storage = service.getStorageById(subSystemID);
			if (storage == null) {
				this.addActionError("未找到相关信息，请稍后再试！");
			} else {
				this.setAttribute("stotype", storage.getString("storage_type"));
				DBPage portPage = portService.getPortList(curPage, numPerPage, null, null, Long.parseLong(subSystemID));
				DBPage diskPage = diskgroupService.getDiskgroupList(curPage, numPerPage, null, null, Long.parseLong(subSystemID),null);
				DBPage poolPage = service.getPoolById(1, SrContant.SR_NumPerPage, subSystemID);
				DBPage volumePage = service.getVolumeById(1, SrContant.SR_NumPerPage, subSystemID);
				DBPage nodePage = nodeService.getNodePage(null, Integer.parseInt(subSystemID), 1, WebConstants.NumPerPage);
				DBPage storagegroupPage = hostService.getHostgroupPage(null, Integer.parseInt(subSystemID), 1, 20);
				this.setAttribute("portPage", portPage);
				this.setAttribute("portCount", portPage.getTotalRows());
				this.setAttribute("diskPage", diskPage);
				this.setAttribute("diskCount", diskPage.getTotalRows());
				this.setAttribute("poolPage", poolPage);
				this.setAttribute("poolCount", poolPage.getTotalRows());
				this.setAttribute("volumePage", volumePage);
				this.setAttribute("volumeCount", volumePage.getTotalRows());
				this.setAttribute("nodePage", nodePage);
				this.setAttribute("nodeCount", nodePage.getTotalRows());
				this.setAttribute("storagegroupPage", storagegroupPage);
				this.setAttribute("storagegroupCount", storagegroupPage.getTotalRows());
				this.setAttribute("storageInfo", storage);
				this.setAttribute("subSystemID", subSystemID);
			}
		}
		return new ActionResult("/WEB-INF/views/sr/storage/storageInfo.jsp");
	}
	
	public void doCapacityInfo(){
		String subSystemID = getStrParameter("subSystemID"); 
		DataRow row = service.getStorageById(subSystemID);
		Double totalCapecity = row.getDouble("total_usable_capacity");    //总容量
		Double unallocalCapecity = row.getDouble("unallocated_usable_capacity");  //可用容量
		String unallocalCap = new DecimalFormat("0.00").format(unallocalCapecity/1024);
		String alreadyUseCap = new DecimalFormat("0.00").format((totalCapecity-unallocalCapecity)/1024);
		String unallocalCapPercent = new DecimalFormat("0.00").format(unallocalCapecity/totalCapecity*100);
		String alreadyUseCapPercent = new DecimalFormat("0.00").format((totalCapecity-unallocalCapecity)/totalCapecity*100);
		
		JSONObject json = new JSONObject();
		json.put("usedSpace", Double.parseDouble(alreadyUseCap));
		json.put("availableSpace", Double.parseDouble(unallocalCap));
		json.put("perUsedSpace", Double.parseDouble(alreadyUseCapPercent));
		json.put("perAvailableSpace", Double.parseDouble(unallocalCapPercent));
		json.put("the_display_name", row.getString("model"));
		this.setAttribute("jsonVal", json);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
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
