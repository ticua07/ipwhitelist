package ar.ticua;

import java.net.InetAddress;

public class IpUtils {

	private IpUtils() {}

	public static boolean matches(String ip, String cidr) {
		try {
			   String[] parts = cidr.split("/");

			   InetAddress address = InetAddress.getByName(ip);
			   InetAddress network = InetAddress.getByName(parts[0]);

			   int prefixLength = parts.length == 2
						 ? Integer.parseInt(parts[1])
						 : address.getAddress().length * 8;

			   byte[] addrBytes = address.getAddress();
			   byte[] netBytes = network.getAddress();

			   int fullBytes = prefixLength / 8;
			   int remainingBits = prefixLength % 8;

			for (int i = 0; i < fullBytes; i++) {
				if ((addrBytes[i] & 0xFF) != (netBytes[i] & 0xFF)) {
					return false;
				}
			}

			if (remainingBits > 0) {
				int mask = (-1) << (8 - remainingBits);
				if ((addrBytes[fullBytes] & mask) != (netBytes[fullBytes] & mask)) {
					return false;
				}
			}
			   return true;
		  } catch (Exception e) {
			   return false;
		  }
	 }

}