package com.project.service;

import java.util.ArrayList;
import java.util.List;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.AES;
import com.project.storage.entity.Info;
import com.project.web.WebConstants;

public class StorageConfigService extends BaseService {
	
	/**
	 * 获取存储系统配置信息
	 * @param storageType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Info> getStorageConfigList(String storageType) {
		StringBuffer sb = new StringBuffer("select * from t_storage_config where 1 = 1");
		List<Object> args = new ArrayList<Object>();
		List<DataRow> resultList = new ArrayList<DataRow>();
		if (StringHelper.isNotEmpty(storageType) && StringHelper.isNotBlank(storageType)) {
			sb.append(" and storage_type = ? ");
			args.add(storageType);
		}
		//查询信息
		resultList = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), args.toArray());
		List<Info> infoList = new ArrayList<Info>();
		if (resultList != null && resultList.size() > 0) {
			for (DataRow row : resultList) {
				//设置信息
				Info info = new Info();
				info.setSubSystemID(row.getInt("id"));
				info.setSystemName(row.getString("name"));
				info.setType(row.getString("storage_type"));
				info.setIpAddress(row.getString("ctl01_ip"));
				info.setIp1Address(row.getString("ctl02_ip"));
				info.setUsername(row.getString("user"));
				info.setPassword(new AES().decrypt(row.getString("password"), "UTF-8"));
				info.setNativePath(row.getString("native_cli_path"));
				info.setState(row.getInt("state"));
				infoList.add(info);
			}
		}
		return infoList;
	}

}
