package com.cloud.ocs.monitor.thread.service.impl;

import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.SigarException;
import org.springframework.stereotype.Service;

import com.cloud.ocs.monitor.thread.service.ThreadService;
import com.cloud.ocs.monitor.utils.SigarUtil;

@Service
public class ThreadServiceImpl implements ThreadService {

	@Override
	public long getActiveThreadNum() {
		long result = 0L;
		try {
			ProcStat procStat = SigarUtil.sigar.getProcStat();
			result = procStat.getThreads();
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return result;
	}

}
