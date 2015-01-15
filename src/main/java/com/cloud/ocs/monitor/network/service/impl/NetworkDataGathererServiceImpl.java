package com.cloud.ocs.monitor.network.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.hyperic.sigar.NetConnection;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.stereotype.Service;

import com.cloud.ocs.monitor.network.dto.RxbpsTxbpsDto;
import com.cloud.ocs.monitor.network.service.NetworkDataGathererService;
import com.cloud.ocs.monitor.utils.SigarUtil;

/**
 * 用于统计网卡网络相关数据的service实现类
 * 
 * @author Wang Chao
 *
 * @date 2015-1-8 下午11:44:08
 *
 */
@Service
public class NetworkDataGathererServiceImpl implements NetworkDataGathererService {

	private final static Logger LOGGER = Logger.getLogger(NetworkDataGathererServiceImpl.class.getName());
	
	private static final int LADDR_LEN = 50;
	private static final int RADDR_LEN = 50;
	
	private static final String GATEWAY_ADDRESS = "::ffff:10.1.1.1";
	private static final String SERVICE_PORT = "3868";
	
	@Override
	public RxbpsTxbpsDto getRxbpsAndTxbps(String interfaceName) {
		RxbpsTxbpsDto result = null;
		
		try {
			Sigar sigar = SigarUtil.sigar;
			
			long start = System.currentTimeMillis();
			NetInterfaceStat statStart = sigar.getNetInterfaceStat(interfaceName);
			Thread.sleep(1000);
			long end = System.currentTimeMillis();      
			NetInterfaceStat statEnd = sigar.getNetInterfaceStat(interfaceName);
			
			if (statStart != null && statEnd != null) {
				result = new RxbpsTxbpsDto();
				long rxBytesStart = statStart.getRxBytes();
				long rxBytesEnd = statEnd.getRxBytes();
				
				long txBytesStart = statStart.getTxBytes();
				long txBytesEnd = statEnd.getTxBytes();
				
				long rxbps = (rxBytesEnd - rxBytesStart)*8/(end-start)*1000;
				long txbps = (txBytesEnd - txBytesStart)*8/(end-start)*1000;
				
				result.setRxbps(rxbps);
				result.setTxbps(txbps);
			}
		} catch (SigarException e) {
			LOGGER.info(e.toString());
		} catch (InterruptedException e) {
			LOGGER.info(e.toString());
		}
		
		return result;
	}

	@Override
	public Long getRequestNum() {
		Long result = 0L;
		try {
			// default
			int flags = NetFlags.CONN_CLIENT | NetFlags.CONN_SERVER | NetFlags.CONN_PROTOCOLS;
			int proto_flags = 0;
			proto_flags |= NetFlags.CONN_TCP;
			//proto_flags |= NetFlags.CONN_UDP;
//			proto_flags |= NetFlags.CONN_RAW;
//			proto_flags |= NetFlags.CONN_UNIX;

			if (proto_flags != 0) {
				flags &= ~NetFlags.CONN_PROTOCOLS;
				flags |= proto_flags;
			}

			NetConnection[] connections = SigarUtil.sigar.getNetConnectionList(flags);
			String constantLocalAddress = "::ffff:" + this.getVmIpAddress() + ":" + SERVICE_PORT;
//			System.out.println("constantLocalAddress: " + constantLocalAddress);
//			System.out.println("***************************");

			for (int i = 0; i < connections.length; i++) {
				NetConnection conn = connections[i];
				String proto = conn.getTypeString();
				String state;
				
				if (proto.toLowerCase().equals("udp")) {
					continue;
				}

				if (conn.getType() == NetFlags.CONN_UDP) {
					state = "";
				} else {
					state = conn.getStateString();
				}

				String localAddressPort = formatAddress(conn.getType(),
						conn.getLocalAddress(), conn.getLocalPort(), LADDR_LEN,
						true);
//				System.out.println("localAddressPort: " + localAddressPort);
				String foreignAddressPort = formatAddress(conn.getType(),
						conn.getRemoteAddress(), conn.getRemotePort(),
						RADDR_LEN, true);
				System.out.println("foreignAddressPort: " + foreignAddressPort);
				String foreignAddress = foreignAddressPort.substring(0, foreignAddressPort.lastIndexOf(':'));
//				System.out.println("foreignAddress: " + foreignAddress);
//				
//				System.out.println("state: " + state);
//				System.out.println("===========================");
				
				if (localAddressPort.equals(constantLocalAddress) && foreignAddress.equals(GATEWAY_ADDRESS)
						&& state.equals("ESTABLISHED")) {
					result++;
				}
				
			}
		} catch (RuntimeException e) {
			LOGGER.info(e.toString());
		} catch (SigarException e) {
			LOGGER.info(e.toString());
		} 
		
		return result;
	}
	
	private String getVmIpAddress() {
		NetInterfaceConfig config = null;
		try {
			config = SigarUtil.sigar.getNetInterfaceConfig(null);
		} catch (SigarException e) {
			e.printStackTrace();
		}
		
		return config.getAddress();
	}
	
	private String formatAddress(int proto, String ip, long portnum, int max,
			boolean isNumeric) {

		String port = formatPort(proto, portnum, isNumeric);
		String address;

		// 0.0.0.0 或null, 显示为*
		if (NetFlags.isAnyAddress(ip)) {
			address = "*";
		} else if (isNumeric) {
			address = ip;
		} else {
			try {
				address = InetAddress.getByName(ip).getHostName();
			} catch (UnknownHostException e) {
				address = ip;
			}
		}

		max -= port.length() + 1;
		if (address.length() > max) {
			address = address.substring(0, max);
		}

		return address + ":" + port;
	}
	
	private String formatPort(int proto, long port, boolean isNumeric) {
		// 端口为0时,显示为*
		if (port == 0) {
			return "*";
		}
		if (!isNumeric) {
			String service = SigarUtil.sigar.getNetServicesName(proto, port);
			if (service != null) {
				return service;
			}
		}
		return String.valueOf(port);
	}

}
