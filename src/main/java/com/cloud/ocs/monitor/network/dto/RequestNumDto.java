package com.cloud.ocs.monitor.network.dto;

/**
 * 用于表示请求数/已经处理完成的请求数的Dto
 * 
 * @author Wang Chao
 * 
 * @date 2015-1-13 上午10:19:54
 * 
 */
public class RequestNumDto {

	private Long requestNum;
	private Long completeRequestNum;

	public Long getRequestNum() {
		return requestNum;
	}

	public void setRequestNum(Long requestNum) {
		this.requestNum = requestNum;
	}

	public Long getCompleteRequestNum() {
		return completeRequestNum;
	}

	public void setCompleteRequestNum(Long completeRequestNum) {
		this.completeRequestNum = completeRequestNum;
	}

}
