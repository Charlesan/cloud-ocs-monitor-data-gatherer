package com.cloud.ocs.monitor.memory.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.ocs.monitor.memory.service.MemoryService;

@Controller
@RequestMapping(value="/gatherer/memory")
public class MemoryController {
	
	@Resource
	private MemoryService memoryService;
	
	@RequestMapping(value="/getMemoryUsage", method=RequestMethod.GET)
	@ResponseBody
	public Double getMemoryUsage() {
		return memoryService.getMemoryUsage();
	}

}
