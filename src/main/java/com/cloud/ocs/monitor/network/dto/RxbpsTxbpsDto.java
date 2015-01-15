package com.cloud.ocs.monitor.network.dto;

/**
 * 用于传输rxbps和txbps的Dto
 * 
 * @author Wang Chao
 * 
 * @date 2015-1-9 下午9:11:16
 * 
 */
public class RxbpsTxbpsDto {

	private long rxbps;
	private long txbps;

	public long getRxbps() {
		return rxbps;
	}

	public void setRxbps(long rxbps) {
		this.rxbps = rxbps;
	}

	public long getTxbps() {
		return txbps;
	}

	public void setTxbps(long txbps) {
		this.txbps = txbps;
	}

	@Override
	public String toString() {
		return "RxbpsTxbpsDto [rxbps=" + rxbps + ", txbps=" + txbps + "]";
	}

}
