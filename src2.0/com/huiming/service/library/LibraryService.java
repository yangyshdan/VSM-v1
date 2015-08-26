package com.huiming.service.library;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class LibraryService extends BaseService {
	public List<DataRow> getLibraryInfo() {
		String sql="select tape_library_id as id,the_display_name as name from v_res_tape_library";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	public DataRow getLibraryInfo(Integer libraryId){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_tape_library s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(libraryId!=0 && libraryId>0){
			sb.append("and s.tape_library_id = ? ");
			args.add(libraryId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString(),args.toArray());
	}
	public DataRow getResLibraryInfo(Integer libraryId){
		String sql="select s.* from t_res_tapelibrary s where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(libraryId!=0 && libraryId>0){
			sb.append("and s.tapelib_id = ? ");
			args.add(libraryId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
	//分页查询
	public DBPage getLibraryPage(int curPage,int numPerPage,String displayName){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_tape_library s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(displayName!=null&&displayName.length()>0){
			sb.append(" and s.the_display_name like ? ");
			args.add("%"+displayName+"%");
		}
		sb.append("order by s.tape_library_id desc");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	public List<DataRow> getLibraryList(String displayName){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_tape_library s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(displayName!=null&&displayName.length()>0){
			sb.append(" and s.the_display_name like ? ");
			args.add("%"+displayName+"%");
		}
		sb.append("order by s.tape_library_id desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	public List<DataRow> getResLibraryList() {
		String sql="select * from t_res_tapelibrary";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
}
