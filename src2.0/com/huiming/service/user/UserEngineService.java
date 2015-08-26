package com.huiming.service.user;

import java.util.ArrayList;
import java.util.List;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class UserEngineService extends BaseService {
	
	/**
	 * 获取用户定义的设备列表
	 * @param userId
	 * @param deviceType
	 * @param storageType
	 * @param parentId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDefinedDevicesByUser(Long userId,String deviceType,String storageType,Integer parentId) {
		StringBuffer sb = new StringBuffer("select rm.* from tsuserrole ur,tsrolemenu rm where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (userId != null && userId.intValue() > 0) {
			sb.append(" and ur.fuserid = ? and ur.froleid = rm.froleid");
			params.add(userId);
		}
		if (StringHelper.isNotEmpty(deviceType) && StringHelper.isNotBlank(deviceType)) {
			sb.append(" and rm.fdevtype = ?");
			params.add(deviceType);
		}
		//区分存储系统类型
		if (StringHelper.isNotEmpty(storageType) && StringHelper.isNotBlank(storageType)) {
			//SVC,DS,BSP(TPC);HDS,EMC(SR)
			if (storageType.equals(SrContant.DBTYPE_TPC) || storageType.equals(SrContant.DBTYPE_SR)) {
				sb.append(" and rm.menu_id like ?");
				params.add("%" + storageType + "%");
			//HDS,EMC
			} else {
				sb.append(" and rm.os_type = ?");
				params.add(storageType);
			}
		}
		if (parentId != null && parentId > 0) {
			sb.append(" and rm.parentid like ?");
			params.add("%" + parentId + "%");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), params.toArray());
	}
	
	/**
	 * 将设置的设备生成指定格式(返回结果格式:1,2,3)
	 * @param deviceList
	 * @return
	 */
	public String generatelimitDevIds(List<DataRow> deviceList) {
		String limitDevIds = null;
		if (deviceList != null && deviceList.size() > 0) {
			for (int i = 0; i < deviceList.size(); i++) {
				DataRow row = deviceList.get(i);
				long devId = row.getLong("fmenuid");
				if (i == 0) {
					limitDevIds = "" + devId;
				} else {
					limitDevIds = limitDevIds + "," + devId;
				}
			}
		}
		return limitDevIds;
	}
	
	/**
	 * 获取设置的设备
	 * @param userId
	 * @param deviceType
	 * @param storageType
	 * @param parentId
	 * @return
	 */
	public String getUserDefinedDevIds(Long userId,String deviceType,String storageType,Integer parentId) {
		List<DataRow> list = getDefinedDevicesByUser(userId, deviceType, storageType, parentId);
		String limitDevIds = generatelimitDevIds(list);
		return limitDevIds;
	}

}
