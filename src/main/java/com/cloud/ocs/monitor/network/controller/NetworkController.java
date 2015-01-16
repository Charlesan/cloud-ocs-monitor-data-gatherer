package com.cloud.ocs.monitor.network.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.ocs.monitor.network.dto.RxbpsTxbpsDto;
import com.cloud.ocs.monitor.network.service.NetworkDataGathererService;

@Controller
@RequestMapping(value="/gatherer/network")
public class NetworkController {
	
	@Resource
	private NetworkDataGathererService networkDataGathererService;
	
	@RequestMapping(value="/testInterface", method=RequestMethod.GET)
	public void testInterface() {
		for (int i = 0; i <= 13; i++) {
			System.out.println("eth" + i + ": " + networkDataGathererService.getRxbpsAndTxbps("eth" + i));
		}
//		try {
//			new NetworkData(SigarUtil.sigar);
//			NetworkData.newMetricThread();
//		} catch (SigarException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@RequestMapping(value="/getRxbpsTxbps", method=RequestMethod.GET)
	@ResponseBody
	public RxbpsTxbpsDto getRxbpsTxbps(@RequestParam("interfaceName") String interfaceName) {
//		JSONObject jsonObject = new JSONObject(networkDataGathererService.getRxbpsAndTxbps(interfaceName));
//		return jsonObject.toString();
		return networkDataGathererService.getRxbpsAndTxbps(interfaceName);
	}
	
	@RequestMapping(value="/getRequestNum", method=RequestMethod.GET)
	@ResponseBody
	public Long getRequestNum() {
		return networkDataGathererService.getRequestNum();
	}
}
