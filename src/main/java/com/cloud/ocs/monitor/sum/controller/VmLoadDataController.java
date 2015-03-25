package com.cloud.ocs.monitor.sum.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloud.ocs.monitor.sum.dto.VmLoadData;
import com.cloud.ocs.monitor.sum.service.VmLoadDataService;

@Controller
@RequestMapping(value="/gatherer/sum")
public class VmLoadDataController {

	@Resource
	private VmLoadDataService vmLoadDataService;
	
	@RequestMapping(value="/vmLoadData", method=RequestMethod.GET)
	public VmLoadData getVmLoadData() {
		return vmLoadDataService.getVmLoadData();
	}
}
