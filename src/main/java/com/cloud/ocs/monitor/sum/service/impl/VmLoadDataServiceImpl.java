package com.cloud.ocs.monitor.sum.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cloud.ocs.monitor.cpu.service.CpuService;
import com.cloud.ocs.monitor.memory.service.MemoryService;
import com.cloud.ocs.monitor.network.service.NetworkDataGathererService;
import com.cloud.ocs.monitor.sum.dto.VmLoadData;
import com.cloud.ocs.monitor.sum.service.VmLoadDataService;
import com.cloud.ocs.monitor.thread.service.ThreadService;

@Service
public class VmLoadDataServiceImpl implements VmLoadDataService {
	
	@Resource
	private CpuService cpuService;
	
	@Resource
	private MemoryService memoryService;
	
	@Resource
	private ThreadService threadService;
	
	@Resource
	private NetworkDataGathererService networkService;

	@Override
	public VmLoadData getVmLoadData() {
		VmLoadData loadData = new VmLoadData();
		
		loadData.setCpuUsagePercentage(cpuService.getCpuUsage());
		loadData.setMemoryUsagePercentage(memoryService.getMemoryUsage());
		loadData.setThreadNum(threadService.getActiveThreadNum());
		loadData.setTcpConnectionNum(networkService.getRequestNum());
		
		return loadData;
	}

}
