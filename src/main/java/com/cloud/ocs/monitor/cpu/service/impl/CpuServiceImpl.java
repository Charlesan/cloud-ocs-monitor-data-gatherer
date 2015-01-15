package com.cloud.ocs.monitor.cpu.service.impl;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
import org.springframework.stereotype.Service;

import com.cloud.ocs.monitor.cpu.service.CpuService;
import com.cloud.ocs.monitor.utils.SigarUtil;

@Service
public class CpuServiceImpl implements CpuService {

	@Override
	public double getCpuUsage() {
		double result = 0.0;
		try {
			CpuPerc perc = SigarUtil.sigar.getCpuPerc();
			result = perc.getCombined();
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return result;
	}

}
