package com.cloud.ocs.monitor.cpu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.ocs.monitor.cpu.service.CpuService;

@Controller
@RequestMapping(value="/gatherer/cpu")
public class CpuController {
	
	@Resource
	private CpuService cpuService;

	@RequestMapping(value="/getCpuUsage", method=RequestMethod.GET)
	@ResponseBody
	public Double getCpuUsage() {
		return cpuService.getCpuUsage();
	}
}
