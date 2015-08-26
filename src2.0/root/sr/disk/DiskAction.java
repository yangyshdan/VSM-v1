package root.sr.disk;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.sr.ddm.DdmService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;

public class DiskAction extends SecurityAction{
	DdmService service = new DdmService();
	
	public ActionResult doAjaxStoragePage(){
		Integer subSystemID = getIntParameter("subSystemID");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",SrContant.SR_NumPerPage);
		Integer diskgroupId = getIntParameter("diskgroupId");
		DBPage page = service.getDiskPage(curPage, numPerPage, subSystemID, diskgroupId);
		this.setAttribute("subDiskPage", page);
		this.setAttribute("diskgroupId", diskgroupId);
		this.setAttribute("subSystemID", subSystemID);
		return new ActionResult("/WEB-INF/views/sr/disk/ajaxDisk.jsp");
	}
	
	public void doExportDiskConfigData(){
		Integer subSystemID = getIntParameter("subSystemID");
		Integer diskgroupId = getIntParameter("diskgroupId");
		List<DataRow> rows = service.getDiskList(subSystemID, diskgroupId);
		if(rows != null && rows.size() > 0){
			String[] title = new String[]{"名称","磁盘容量(MB)","磁盘速度","类型","更新时间"};
			String[] keys = new String[]{"name","ddm_cap","ddm_speed","ddm_type","update_timestamp"};
			getResponse().setCharacterEncoding("GBK");
			String dateStr;
			int index = keys.length - 1;
			for(DataRow dr : rows){
				dateStr = dr.getString(keys[index]);
				if(dateStr != null){
					dr.set(keys[index], dateStr.replace('-', '/'));
				}
			}
			CSVHelper.createCSVToPrintWriter(getResponse(), "disk", rows, title, keys);
		}
		else {
			CSVHelper.createCSVToPrintWriter(getResponse(), "disk", new ArrayList<DataRow>(0), 
					new String[]{"暂无数据可导出"}, new String[]{});
		}
	}
	
}
