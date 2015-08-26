package com.project.x86monitor;

import java.util.UUID;

import org.apache.log4j.Logger;

import system.DateTime;

import com.huiming.service.x86monitor.DataCollectService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

import csharpwmi.CSharpWMIClass;
import csharpwmi.ICSharpWMIClass;
import csharpwmi.TResComputersystem;
import csharpwmi.TResHypervisor;
import csharpwmi.TResVirtualmachine;
import csharpwmi.Tndevicelog;

/**
 * @category 收集配置数据
 * @author huiming02
 *
 */
public class DataCollectConfig {
	static final Logger logger = Logger.getLogger(DataCollectConfig.class);
	private DataCollectService<TResComputersystem> tResCom = 
		new DataCollectService<TResComputersystem>(WebConstants.DB_DEFAULT);
	
	private DataCollectService<TResHypervisor> tResHyp = 
		new DataCollectService<TResHypervisor>(WebConstants.DB_DEFAULT);
	 
	private DataCollectService<TResVirtualmachine> tResVm = 
		new DataCollectService<TResVirtualmachine>(WebConstants.DB_DEFAULT);
	
	private static final String exclusions[] = new String[]{"clrHandle"};
	
	private ICSharpWMIClass cswmi = null;
	private DeviceInfo deviceInfo = null;
		
	public ICSharpWMIClass getICSharpWMIClass(){
		return cswmi;
	}
	
	public DataCollectConfig(ICSharpWMIClass cswmi, DeviceInfo deviceInfo){
		try{
			if(cswmi == null){
				if(deviceInfo != null){
					MyUtilities.initBridge(this.getClass());
					cswmi = new CSharpWMIClass(
							new String[]{ deviceInfo.getIpAddress() },
							deviceInfo.getUsername(),
							deviceInfo.getPassword(),
							deviceInfo.getAuthentication(),
							deviceInfo.getImpersonate());
					cswmi.Connection();
					deviceInfo.setCurrentIP(cswmi.GetCurrentIP());
				}
			}
			else if(!cswmi.IsConnect()){
				cswmi.Connection();
			}
			this.cswmi = cswmi;
			this.deviceInfo = deviceInfo;
		}catch(Exception ex){
			logger.error("连接IP地址为" + deviceInfo.getIpAddress() + "出错", ex);
		}
		
	}
	
	public void execute(){
		/**
		 * 因为这是配置，当已经存在该IP时，那么就咨询一下当前连接上的IP地址是否已经存在数据库
		 */
		if(deviceInfo != null && cswmi != null && cswmi.IsConnect()){ //  配置信息不能为空，且CSWMI也必须连接上
			Long computerId = tResCom.getComputerId(cswmi.GetCurrentIP());
			if(computerId == null){ return; }
			TResComputersystem sytstem = cswmi.GetTResComputersystem();
			boolean isPhysical = WebConstants.HYPERVISOR.equalsIgnoreCase(deviceInfo.getToptype());
			// 计算机肯定有版本信息的
			if(sytstem == null || sytstem.getOsVersion() == null || sytstem.getName() == null){
				// 这种异常应该放到日志事件记录tndevicelog，并且关停任务
				DataCollectService<Tndevicelog> tndevicelog = new DataCollectService<Tndevicelog>(WebConstants.DB_DEFAULT);
				Tndevicelog dev = new Tndevicelog();
				dev.setFcount(1);
				dev.setFdescript(String.format("主机名称: %s, IP: %s", deviceInfo.getHostName(), deviceInfo.getIpAddress()));
				dev.setFdetail(String.format("该主机(名称: %s, IP: %s)能连接, 但是获取配置信息出错, 请检查Windows操作系统兼容性问题", 
						deviceInfo.getHostName(), deviceInfo.getIpAddress()));
				dev.setFlogtype(4);
				dev.setFtoptype(deviceInfo.getToptype());
				dev.setFtopname(deviceInfo.getHostName());
				dev.setFresourcename(dev.getFtopname());
				dev.setFfirsttime(DateTime.getNow());
				dev.setFlasttime(dev.getFfirsttime());
				dev.setFlevel("2");
				if(isPhysical){
					Long hypid = tResHyp.getHypervisorIdByComputerId(computerId);
					if(hypid > 0){
						dev.setFtopid(hypid.toString());  // 如果是物理机就填t_res_hypervisor的ID
						dev.setFtoptype(SrContant.SUBDEVTYPE_PHYSICAL);
						dev.setFresourceid(dev.getFtopid());
						dev.setFresourcetype(SrContant.SUBDEVTYPE_PHYSICAL);
						tndevicelog.insert(dev, exclusions, false);
						tndevicelog.disableTask(computerId, null);
					}
				}
				else {
					Long hypvm[] = tResHyp.getHypIdAndVMIdByComputerId(computerId);
					if(hypvm[0] > 0L && hypvm[1] > 0L){
						dev.setFtopid(hypvm[0].toString());  // 如果是物理机就填t_res_hypervisor的ID
						dev.setFtoptype(SrContant.SUBDEVTYPE_VIRTUAL);
						dev.setFresourceid(hypvm[1].toString());
						dev.setFresourcetype(SrContant.SUBDEVTYPE_VIRTUAL);
						tndevicelog.insert(dev, exclusions, false);
						tndevicelog.disableTask(computerId, null);
					}
				}
				logger.error(dev.getFdetail());
				return;
			}
			
			sytstem.setVendor(deviceInfo.getVendor());
			sytstem.setModel(deviceInfo.getModel());
			sytstem.setIsVirtual(!isPhysical);
			long hypId = -1L;
			if(isPhysical){ // 如果不是物理机查出来的肯定是-1
				hypId = tResVm.getHypervisorIdByComputerId(computerId);
			}
			
			if(computerId > 0){ // 如果查出来的结果不是0，那么就把更新当前，当前的computer_id返回
				try{
					sytstem.setComputerId(-100L); // 不要更新
					tResCom.update(sytstem, exclusions, false, "computer_id", computerId);
				}catch(Exception ex){
					logger.error("", ex);
				}
				if(isPhysical){  // 如果是物理机,就更新t_res_hypervisor
		        	TResHypervisor hypervisor = cswmi.GetTResHypervisor();
		        	if(hypId > 0){ // 更新
		        		try{
		        			hypervisor.setHostComputerId(-100L);
			        		hypervisor.setHypervisorId(-100L); // 不要更新
					        tResHyp.update(hypervisor, exclusions, false, "host_computer_id", computerId);
			        	}catch(Exception ex){
							logger.error("", ex);
						}
		        	}
		        	else { // 插入
		        		try{
		        			hypervisor.setHostComputerId(computerId);
		        			tResHyp.insert(hypervisor, exclusions, false);
		        		}catch(Exception ex){
							logger.error("", ex);
						}
		        	}
			        hypervisor = null;
		        }
		        else { // 如果是虚拟机,就更新t_res_virtualmachine
		        	if(hypId > 0){ // 更新
		        		try{
			        		TResVirtualmachine vm = cswmi.GetTResVirtualmachine(-100L, -100L);
				        	vm.setVmId(-100); // 不要更新
				        	tResVm.update(vm, exclusions, false, "computer_id", computerId);
		        		}catch(Exception ex){
		        			logger.error("", ex);
		        		}
		        	}
		        }
				return;
			}
			/**
			 * 理论上，不会有插入操作
			 */
			// 插入
			try{ //  physical
				computerId = Long.parseLong(tResCom.insert(sytstem, exclusions, false));
			}catch(Exception ex){
				logger.error("在收集IP地址为" + deviceInfo.getIpAddress() + "的主机出错", ex);
			}
	        if(isPhysical){  // 如果是物理机
	        	TResHypervisor hypervisor = cswmi.GetTResHypervisor();
	        	try{
			        hypervisor.setHostComputerId(computerId);
			        tResHyp.insert(hypervisor, exclusions, false);
	        	}catch(Exception ex){
					logger.error("", ex);
				}
		        hypervisor = null;
	        }
	        else {

				Long hypvmIds[] = tResVm.getHypIdAndVMIdByComputerId(computerId);
	        	if(hypId > 0L && hypvmIds[1] > 0L){
	        		try{
		        		TResVirtualmachine vm = cswmi.GetTResVirtualmachine(computerId, hypId);
			        	vm.setUid(UUID.randomUUID().toString().replace("-", ""));
			        	tResVm.insert(vm, exclusions, false);
		        	}catch(Exception ex){
						logger.error("", ex);
					}
	        	}
	        }
	        sytstem = null;
		}
		else {
			logger.error("IP地址为" + deviceInfo.getIpAddress() + "连接不上");
		}
	}
}
