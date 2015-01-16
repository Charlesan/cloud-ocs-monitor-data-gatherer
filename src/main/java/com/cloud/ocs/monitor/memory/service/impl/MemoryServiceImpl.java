package com.cloud.ocs.monitor.memory.service.impl;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.springframework.stereotype.Service;

import com.cloud.ocs.monitor.memory.service.MemoryService;
import com.cloud.ocs.monitor.utils.SigarUtil;

@Service
public class MemoryServiceImpl implements MemoryService {

	@Override
	public double getMemoryUsage() {
		double result = 0.0;
		try {
			Mem mem = SigarUtil.sigar.getMem();
			result = mem.getUsedPercent();
		} catch (SigarException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
