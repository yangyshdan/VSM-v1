package root.alert;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.NumericHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.AlertRuleService;

public class SmartEngine {
	private AlertRuleService ruleService = new AlertRuleService();
	
	public void getDiagnostics(String id1,String id2,List<DataRow> targets,DataRow prfData,String diagnostic,List<String> list){
		String a1 = checkKPI(id1, prfData, targets);
		String a2 = checkKPI(id2, prfData, targets);
		
		StringBuffer buffer = new StringBuffer();
		if(StringHelper.isNotEmpty(a1) && StringHelper.isNotEmpty(a2)){
//			getTargetById(targets, a1).getString("fdevtype")+"["+prfData.getString("ele_name")+"] ";
			buffer.append(a1+"。<br/>在该时段同时发生"+a2);
			buffer.append("，该事件对前者"+getTargetById(targets, id1).getString("ftitle")+"告警有直接影响。<br/>建议优化 "+diagnostic+"配置及性能，以优化系统性能。");
		}
		if(StringHelper.isNotEmpty(buffer.toString())){
			list.add(buffer.toString());
		}
	}
	
	public List<String> execute(DataRow prfData){
		List<String> list = new ArrayList<String>();
		List<DataRow> targets = ruleService.getTargets(8);
		
//		system backend disk bottleneck
		//SVC
		getDiagnostics("A47", "A63", targets, prfData, prfData.getString("ele_name")+"的后端磁盘组",list);
		//DS8K
		getDiagnostics("A236", "A254", targets, prfData, prfData.getString("ele_name")+"的后端磁盘组",list);
//		system write cache not enough
		//SVC
		getDiagnostics("A47", "A40", targets, prfData, prfData.getString("ele_name")+"的写缓存",list);
		//DS8K
		getDiagnostics("A236", "A227", targets, prfData, prfData.getString("ele_name")+"的写缓存",list);
		
//		system read cache not enough
		//SVC
		getDiagnostics("A47", "A39", targets, prfData, prfData.getString("ele_name")+"的读缓存",list);
		//DS8K
		getDiagnostics("A236", "A224", targets, prfData, prfData.getString("ele_name")+"的读缓存",list);
		
//		system port performance bottleeneck
		//SVC
		getDiagnostics("A47", "A75", targets, prfData, prfData.getString("ele_name")+"的端口",list);
		//DS8K
		getDiagnostics("A236", "A263", targets, prfData, prfData.getString("ele_name")+"的端口",list);
		
//		system fabric performance
		//DS8K
		getDiagnostics("A236", "A266", targets, prfData, prfData.getString("ele_name")+"的光纤模块",list);
		return list;
	}
	
	private String checkKPI(String id,DataRow row,List<DataRow> targets){
		StringBuffer buffer = new StringBuffer();
		DataRow target = getTargetById(targets, id);
		int i = 0;
		if(target != null){
		if (target.getDouble("faveminvalue") > 0 && row.getDouble(id.toLowerCase()) >= target.getDouble("faveminvalue")) {
			buffer.append("系统"+target.getString("ftitle") );
			if(target.getDouble("favemaxvalue") > 0 && row.getDouble(id.toLowerCase()) >= target.getDouble("favemaxvalue")){
				buffer.append("平均值超过了阀值"+ target.getDouble("favemaxvalue") +target.getString("funits"));
				i = 1;
			}else{
				buffer.append("平均值超过了阀值"+ target.getDouble("faveminvalue") +target.getString("funits"));
				i = 0;
			}
			buffer.append(",达到"+ NumericHelper.format(row.getDouble(id.toLowerCase())) + target.getString("funits"));
			if(i > 0){
				buffer.append("的【错误】级别事件");
			}else{
				buffer.append("的【警告】级别事件");
			}
		}
		}
		return buffer.toString();
	}

	private DataRow getTargetById(List<DataRow> targets, String id) {
		DataRow target = null;
		if (targets != null && targets.size() > 0) {
			for (DataRow tar : targets) {
				if (tar.getString("ffieldid").equals(id)) {
					target = tar;
					break;
				}
			}
		}
		return target;
	}

}
