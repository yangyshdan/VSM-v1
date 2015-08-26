package com.huiming.service.initial;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.service.storagesystem.StorageSystemService;
import com.project.web.WebConstants;

public class InitializeService extends BaseService{
	
	// whether exsist subSystem config info
	public boolean isExsistSystem(int id,String model){
		boolean isFlag = false;
		StorageSystemService service = new StorageSystemService();
		List<DataRow> rows = service.getStorageCapacityInfo();
		for (DataRow dataRow : rows) {
			if(id==dataRow.getInt("subsystem_id") && model.equalsIgnoreCase(dataRow.getString("model"))){
				isFlag =  true;
				break;
			}
		}
		return isFlag;
	}
	
	
	public void InitialStorageSystem(){
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("test.sql");
		BufferedReader reader=null;
		StringBuffer sb = new StringBuffer();
		String readLine=null;
		Connection conn = getConnection(WebConstants.DB_DEFAULT);
		Statement stat = null;
		try {
			reader = new BufferedReader(new InputStreamReader(input,"UTF-8"));
			stat = conn.createStatement();
			while((readLine = reader.readLine())!=null){
				stat.addBatch(readLine);
				sb.append(readLine);
				sb.append("\r\n");
			}
			input.close();
			stat.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
