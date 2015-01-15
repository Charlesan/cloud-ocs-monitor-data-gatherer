package com.cloud.ocs.monitor.network.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hyperic.sigar.NetConnection;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;
import org.hyperic.sigar.Tcp;
import org.hyperic.sigar.util.PrintfFormat;

import com.cloud.ocs.monitor.utils.SigarUtil;

public class NetInfo {

	private Sigar sigar;

	private SigarProxy proxy;

	private StringBuilder info = new StringBuilder();

	private void sigarInit(boolean isProxy) {
		//sigar = new Sigar();
		sigar = SigarUtil.sigar;
		if (isProxy)
			proxy = SigarProxyCache.newInstance(this.sigar);
	}

	private void shutdown() {
		//this.sigar.close();
	}

	public String getInfo() {
		return info.toString();
	}

	public void clearInfo() {
		if (null != info)
			info.delete(0, info.length());
	}

	private void println(String arg) {
		info.append(arg + "\n");
	}

	public String sprintf(String format, Object[] items) {
		return new PrintfFormat(format).sprintf(items);
	}

	public void printf(String format, Object[] items) {
		println(sprintf(format, items));
	}

	public void netInfo() throws SigarException {
		clearInfo();
		println("============Network information================");

		try {
			sigarInit(false);

			NetInterfaceConfig config = this.sigar.getNetInterfaceConfig(null);
			println("current interface....." + config.getName());
			println("ip address / netmask...." + config.getAddress() + "/"
					+ config.getNetmask());
			println("mac address..." + config.getHwaddr());

			org.hyperic.sigar.NetInfo info = this.sigar.getNetInfo();

			println("default gateway......." + info.getDefaultGateway());
			println("host name............." + info.getHostName());
			println("domain name..........." + info.getDomainName());
			println("primary dns..........." + info.getPrimaryDns());
			println("secondary dns........." + info.getSecondaryDns());
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			shutdown();
		}
	}

	public void intfInfo(int style) throws SigarException {
		clearInfo();
		println("============Network interface information================");

		try {
			sigarInit(true);

			String[] ifNames = this.proxy.getNetInterfaceList();

			for (int i = 0; i < ifNames.length; i++) {
				try {
					if (1 == style)
						ifconfig(ifNames[i]);
					else if (2 == style)
						ifDisp(ifNames[i]);
				} catch (SigarException e) {
					println(ifNames[i] + "\t" + e.getMessage());
				}
			}

		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			shutdown();
		}
	}

	public void ifDisp(String name) throws SigarException {
		String status = null;
		NetInterfaceConfig ifconfig = this.sigar.getNetInterfaceConfig(name);

		long flags = ifconfig.getFlags();

		if (NetFlags.getIfFlagsString(flags).contains("UP"))
			status = "up";
		else
			status = "down";

		println("------------------" + ifconfig.getName() + "\t" + status
				+ "-----------------------------");
		println(ifconfig.getDescription());
		println(ifconfig.getAddress() + "\t" + ifconfig.getNetmask());

		println("intf type:" + ifconfig.getType());

		String hwaddr = "";
		if (!NetFlags.NULL_HWADDR.equals(ifconfig.getHwaddr())) {
			hwaddr = " HWaddr " + ifconfig.getHwaddr();
		}
		println(hwaddr);

	}

	public void ifconfig(String name) throws SigarException {
		NetInterfaceConfig ifconfig = this.sigar.getNetInterfaceConfig(name);
		long flags = ifconfig.getFlags();

		String hwaddr = "";
		if (!NetFlags.NULL_HWADDR.equals(ifconfig.getHwaddr())) {
			hwaddr = " HWaddr " + ifconfig.getHwaddr();
		}

		if (!ifconfig.getName().equals(ifconfig.getDescription())) {
			println(ifconfig.getDescription());
		}

		println(ifconfig.getName() + "\t" + "Link encap:" + ifconfig.getType()
				+ hwaddr);

		String ptp = "";
		if ((flags & NetFlags.IFF_POINTOPOINT) > 0) {
			ptp = "  P-t-P:" + ifconfig.getDestination();
		}

		String bcast = "";
		if ((flags & NetFlags.IFF_BROADCAST) > 0) {
			bcast = "  Bcast:" + ifconfig.getBroadcast();
		}

		println("\t" + "inet addr:" + ifconfig.getAddress() + ptp + // unlikely
				bcast + "  Mask:" + ifconfig.getNetmask());

		println("\t" + NetFlags.getIfFlagsString(flags) + " MTU:"
				+ ifconfig.getMtu() + "  Metric:" + ifconfig.getMetric());
		try {
			NetInterfaceStat ifstat = this.sigar.getNetInterfaceStat(name);

			println("\t" + "RX packets:" + ifstat.getRxPackets() + " errors:"
					+ ifstat.getRxErrors() + " dropped:"
					+ ifstat.getRxDropped() + " overruns:"
					+ ifstat.getRxOverruns() + " frame:" + ifstat.getRxFrame());

			println("\t" + "TX packets:" + ifstat.getTxPackets() + " errors:"
					+ ifstat.getTxErrors() + " dropped:"
					+ ifstat.getTxDropped() + " overruns:"
					+ ifstat.getTxOverruns() + " carrier:"
					+ ifstat.getTxCarrier());
			println("\t" + "collisions:" + ifstat.getTxCollisions());

			long rxBytes = ifstat.getRxBytes();
			long txBytes = ifstat.getTxBytes();

			println("\t" + "RX bytes:" + rxBytes + " ("
					+ Sigar.formatSize(rxBytes) + ")" + "  " + "TX bytes:"
					+ txBytes + " (" + Sigar.formatSize(txBytes) + ")");
		} catch (SigarException e) {
		}

		println("");
	}

	private static final int LADDR_LEN = 20;
	private static final int RADDR_LEN = 35;

	private static boolean wantPid = true;

	private String formatPort(int proto, long port, boolean isNumeric) {
		// 端口为0时,显示为*
		if (port == 0) {
			return "*";
		}
		if (!isNumeric) {
			String service = this.sigar.getNetServicesName(proto, port);
			if (service != null) {
				return service;
			}
		}
		return String.valueOf(port);
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

	public static boolean isAnyAddress(String address) {
		return address == null || address.equals("0.0.0.0")
				|| address.equals("::");
	}

	public static boolean isLoopback(String address) {
		return address.equals("localhost") || address.equals("127.0.0.1")
				|| address.equals("::1");
	}

	public void outputTcpStats() throws SigarException {
		clearInfo();
		println("============connection information================");

		try {
			sigarInit(false);
			Tcp stat = this.sigar.getTcp();
			final String dnt = "    ";
			println(dnt + stat.getActiveOpens()
					+ " active connections openings");
			println(dnt + stat.getPassiveOpens()
					+ " passive connection openings");
			println(dnt + stat.getAttemptFails()
					+ " failed connection attempts");
			println(dnt + stat.getEstabResets() + " connection resets received");
			println(dnt + stat.getCurrEstab() + " connections established");
			println(dnt + stat.getInSegs() + " segments received");
			println(dnt + stat.getOutSegs() + " segments send out");
			println(dnt + stat.getRetransSegs() + " segments retransmited");
			println(dnt + stat.getInErrs() + " bad segments received.");
			println(dnt + stat.getOutRsts() + " resets sent");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			shutdown();
		}

	}

	// XXX currently weak sauce. should end up like netstat command.
	public void connections(boolean isNumeric) throws SigarException {
		clearInfo();
		println("============Display connection deatil================");

		try {
			sigarInit(false);

			// default
			int flags = NetFlags.CONN_CLIENT | NetFlags.CONN_SERVER
					| NetFlags.CONN_PROTOCOLS;
			System.out.println("flags is " + flags);
			int proto_flags = 0;
			proto_flags |= NetFlags.CONN_TCP;
			//proto_flags |= NetFlags.CONN_UDP;
			proto_flags |= NetFlags.CONN_RAW;
			proto_flags |= NetFlags.CONN_UNIX;

			if (proto_flags != 0) {
				flags &= ~NetFlags.CONN_PROTOCOLS;
				flags |= proto_flags;
			}
			System.out.println("flags is " + flags);

			NetConnection[] connections = this.sigar
					.getNetConnectionList(flags);

			String[] HEADER = new String[] { "Proto", "Local Address",
					"Foreign Address", "State", "" };
			//printf(" ls \t ls \t ls \t ls \t ls", HEADER);
			printConnectionDetail(HEADER);

			for (int i = 0; i < connections.length; i++) {
				NetConnection conn = connections[i];
				String proto = conn.getTypeString();
				String state;

				if (conn.getType() == NetFlags.CONN_UDP) {
					state = "";
				} else {
					state = conn.getStateString();
				}

				String[] items = new String[5];
				items[0] = proto;
				items[1] = formatAddress(conn.getType(),
						conn.getLocalAddress(), conn.getLocalPort(), LADDR_LEN,
						isNumeric);
				items[2] = formatAddress(conn.getType(),
						conn.getRemoteAddress(), conn.getRemotePort(),
						RADDR_LEN, isNumeric);
				items[3] = state;

				String process = null;
				if (wantPid &&
				// XXX only works w/ listen ports
						(conn.getState() == NetFlags.TCP_LISTEN)) {
					try {
						long pid = this.sigar.getProcPort(conn.getType(),
								conn.getLocalPort());
						if (pid != 0) { // XXX another bug
							String name = this.sigar.getProcState(pid)
									.getName();
							process = pid + "/" + name;
						}
					} catch (SigarException e) {
					}
				}

				if (process == null) {
					process = "";
				}

				items[4] = process;

				//printf(" ls \t ls \t ls \t ls \t ls", items);
				printConnectionDetail(items);

			}

		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			shutdown();
		}
	}
	
	private void printConnectionDetail(String[] item) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < item.length; i++) {
			strBuilder.append(item[i] + "\t");
		}
		System.out.println(strBuilder.toString());
	}

	public static void main(String[] args) throws SigarException {
		NetInfo one = new NetInfo();

		one.intfInfo(2);
		System.out.println(one.getInfo());

		one.netInfo();
		System.out.println(one.getInfo());

		one.connections(true);
		System.out.println(one.getInfo());

		one.outputTcpStats();
		System.out.println(one.getInfo());

	}
}
