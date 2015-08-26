package root.library;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.library.LibraryService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class LibraryAction extends SecurityAction {
	BaseprfService baseService = new BaseprfService();
	LibraryService service=new LibraryService();
	public ActionResult doLibraryPage(){
		DBPage libraryPage = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",25);
		libraryPage = service.getLibraryPage(curPage, numPerPage,null);
		List<DataRow> labaryList=libraryPage.getData();
		List<DataRow> reslibraryList=service.getResLibraryList();
		List<DataRow> list = new ArrayList<DataRow>();
		for(DataRow row:labaryList){
			for(DataRow row1:reslibraryList){
				if(row.getInt("tape_library_id")==row1.getInt("tapelib_id")){
					row.set("power_status", row1.get("power_status"));
					row.set("tape_status", row1.get("tape_status"));
					list.add(row);	
				}	
			}
		}
		labaryList.removeAll(list);
		for(DataRow row2 : labaryList){
			row2.set("tape_status","");
			row2.set("power_status", "");
			list.add(row2);
		}
		libraryPage.setData(list);
		this.setAttribute("libraryPage", libraryPage);
		return new ActionResult("/WEB-INF/views/library/libraryList.jsp");
	}
	public ActionResult doAjaxLibraryPage(){
		DBPage libraryPage = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",25);
		String displayName=getStrParameter("displayName");
		libraryPage = service.getLibraryPage(curPage, numPerPage,displayName);
		List<DataRow> labaryList=libraryPage.getData();
		List<DataRow> reslibraryList=service.getResLibraryList();
		List<DataRow> list = new ArrayList<DataRow>();
		for(DataRow row:labaryList){
			for(DataRow row1:reslibraryList){
				if(row.getInt("tape_library_id")==row1.getInt("tapelib_id")){
					row.set("power_status", row1.get("power_status"));
					row.set("tape_status", row1.get("tape_status"));
					list.add(row);	
				}	
			}
		}
		labaryList.removeAll(list);
		for(DataRow row2 : labaryList){
			row2.set("tape_status","");
			row2.set("power_status", "");
			list.add(row2);
		}
		libraryPage.setData(list);
		this.setAttribute("libraryPage", libraryPage);
		this.setAttribute("displayName", displayName);
		return new ActionResult("/WEB-INF/views/library/ajaxLibrary.jsp");
	}
	public void doExportLibraryConfigData(){
		String displayName=getStrParameter("displayName");
		List<DataRow> libraryList=service.getLibraryList(displayName);
		List<DataRow> reslibraryList=service.getResLibraryList();
		List<DataRow> list = new ArrayList<DataRow>();
		for(DataRow row:libraryList){
			for(DataRow row1:reslibraryList){
				if(row.getInt("tape_library_id")==row1.getInt("tapelib_id")){
					row.set("power_status", row1.get("power_status"));
					row.set("tape_status", row1.get("tape_status"));
					list.add(row);	
				}	
			}
		}
		libraryList.removeAll(list);
		for(DataRow row2 : libraryList){
			row2.set("tape_status","");
			row2.set("power_status", "");
			list.add(row2);
		}
		if(list!=null && list.size()>0){
			String[] title = new String[]{"名称","工作状态","电源状态","磁带状态","描述","更新时间"};
			String[] keys = new String[]{"the_display_name","the_operational_status","power_status","tape_status","description","update_timestamp"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(),"Library", list, title, keys);
		}
	}
	public ActionResult doLibraryInfo(){
		Integer libraryId = getIntParameter("libraryId");
		DataRow row = service.getLibraryInfo(libraryId);
		DataRow row1 = service.getResLibraryInfo(libraryId);
		this.setAttribute("libraryInfo", row);
		this.setAttribute("reslibraryInfo", row1);
		this.setAttribute("libraryId", libraryId);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1, libraryId.toString(), null,null,null, "Library", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		return new ActionResult("/WEB-INF/views/library/libraryInfo.jsp");
	}
}
