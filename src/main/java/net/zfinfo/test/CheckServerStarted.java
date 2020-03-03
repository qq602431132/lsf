package net.zfinfo.test;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

class CheckServerStarted implements Closeable {
	private final SocketChannel channel;
	private final InetSocketAddress address;
	private boolean connStarted = false;

	public CheckServerStarted(InetSocketAddress address) throws IOException {
		channel = SocketChannel.open();
		channel.configureBlocking(false);

		this.address = address;
	}

	public static void main(String[] args) {
		try {
			new CheckServerStarted(new InetSocketAddress("127.0.0.1",1234)).check(60, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @throws IOException
	 * 
	 */
	private void startConn() throws IOException {
		channel.connect(address);
		connStarted = true;
	}

	private boolean check(int timeout, boolean retry) {
		for (int i = 0; i < timeout; i++) {
			try {
				final boolean justStarted = !connStarted;
				if (justStarted) {
					startConn();
				}

				if (justStarted || (i > 0)) {
					// Wait a second between attempts/give the connection some time to get
					// established.
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

				if (channel.finishConnect()) {
					System.out.println("Server started - accepting connections on " + address.toString());
					return true;
				}
			} catch (IOException e) {
				System.out.println("Connect attempt failed : " + e.getMessage());
				if (!retry) {
					break;
				}

				// Try to start the connection again if it failed.
				connStarted = false;
			}
		}

		System.out.println("Connect attempt to " + address.toString() + " failed, ran out of time/attempts");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() {
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}