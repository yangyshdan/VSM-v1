package com.huiming.service.virtualPlat;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class VirtualPlatService extends BaseService {
	
	/**
	 * 保存虚拟平台(HYPERVISOR)信息
	 * @param row
	 */
	public void saveVirtualPlatInfo(DataRow row) {
		String physicId = row.getString("hypervisor_id");
		int vpId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select id from t_res_virtualplatform where hypervisor_id = ?", new Object[]{physicId});
		//更新
		if (vpId > 0) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_virtualplatform", row, "id", vpId);
		//新增
		} else {
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_virtualplatform", row);
		}
	}

	/**
	 * 分页获取虚拟平台信息列表
	 * @param name
	 * @param type
	 * @param physicalName
	 * @param curPage
	 * @param numPerPage
	 * @param limitIds
	 * @return
	 */
	public DBPage getVirtualPlatInfoPage(String name,String type,String physicalName,int curPage,int numPerPage,String limitIds) {
		StringBuffer sb = new StringBuffer("select * from t_res_virtualplatform where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sb.append("and id in (" + limitIds + ") ");
		}
		if (StringHelper.isNotEmpty(name) && StringHelper.isNotBlank(name)) {
			sb.append("and name like ? ");
			args.add("%" + name + "%");
		}
		if (StringHelper.isNotEmpty(type) && StringHelper.isNotBlank(type)) {
			sb.append("and type = ? ");
			args.add(type);
		}
		if (StringHelper.isNotEmpty(physicalName) && StringHelper.isNotBlank(physicalName)) {
			sb.append("and hypervisor_name like ? ");
			args.add("%" + physicalName + "%");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(), args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取指定的虚拟平台(HYPERVISOR)信息
	 * @param virtualPlatId
	 * @return
	 */
	public DataRow getVirtualPlatInfo(String virtualPlatId) {
		String sql = "select vp.*,cs.processor_count as physcpu,cs.ram_size as physmem from t_res_virtualplatform vp,t_res_hypervisor h,t_res_computersystem cs "
			+ "where vp.id = ? and vp.hypervisor_id = h.hypervisor_id and h.host_computer_id = cs.computer_id";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{virtualPlatId});
	}
	
}
