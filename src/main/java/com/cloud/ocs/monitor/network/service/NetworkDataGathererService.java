package com.cloud.ocs.monitor.network.service;

import com.cloud.ocs.monitor.network.dto.RxbpsTxbpsDto;

/**
 * 用于统计网卡网络相关数据的service接口
 * 
 * @author Wang Chao
 *
 * @date 2015-1-8 下午11:43:22
 *
 */
public interface NetworkDataGathererService {
	
	/**
	 * 获取网卡每秒钟接收和发送bit的数量
	 * @param interfaceName 网卡名
	 * @return
	 */
	public RxbpsTxbpsDto getRxbpsAndTxbps(String interfaceName);
	
	public Long getRequestNum();
	
}
