/*
 * 
 */
package com.krawl.system;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// TODO: Auto-generated Javadoc
/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@Data

/*
 * (non-Javadoc)
 * 
 * @see java.lang.Object#toString()
 */
@ToString
public class ZookeeperNodeManager implements Watcher, Runnable, StatCallback {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private @Getter @Setter String znode;
	private @Getter @Setter String hostPort;
	private @Getter @Setter ZooKeeper zk;
	private @Getter @Setter String filename;
	private @Getter @Setter String exec[];
	private @Getter @Setter Process child;
	private @Getter @Setter Boolean dead;
	private @Getter @Setter byte prevData[];

	static class StreamWriter extends Thread {

		/** The os. */
		OutputStream os;

		/** The is. */
		InputStream is;

		/**
		 * Instantiates a new stream writer.
		 *
		 * @param is
		 *            the is
		 * @param os
		 *            the os
		 */
		StreamWriter(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
			start();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			byte b[] = new byte[80];
			int rc;
			try {
				while ((rc = is.read(b)) > 0) {
					os.write(b, 0, rc);
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Instantiates a new node manager.
	 *
	 * @param hostPort
	 *            the host port
	 * @param znode
	 *            the znode
	 * @param filename
	 *            the filename
	 * @param exec
	 *            the exec
	 * @throws KeeperException
	 *             the keeper exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ZookeeperNodeManager(String hostPort, String znode, String filename, String exec[])
			throws KeeperException, IOException {
		this.filename = filename;
		this.hostPort = hostPort;
		this.exec = exec;
		this.znode = znode;
		zk = new ZooKeeper(hostPort, 3000, this);
		zk.exists(znode, true, this, null);
	}

	/**
	 * Closing.
	 *
	 * @param rc
	 *            the rc
	 */
	public void closing(int rc) {
		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * Exists.
	 *
	 * @param data
	 *            the data
	 */
	public void exists(byte[] data) {
		if (data == null) {
			if (child != null) {
				System.out.println("Killing process");
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
				}
			}
			child = null;
		} else {
			if (child != null) {
				System.out.println("Stopping child");
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				FileOutputStream fos = new FileOutputStream(filename);
				fos.write(data);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Starting child");
				child = Runtime.getRuntime().exec(exec);
				new StreamWriter(child.getInputStream(), System.out);
				new StreamWriter(child.getErrorStream(), System.err);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void process(WatchedEvent event) {
		String path = event.getPath();
		if (event.getType() == Event.EventType.None) {
			// We are are being told that the state of the
			// connection has changed
			switch (event.getState()) {
			case SyncConnected:
				break;
			case Expired:
				dead = true;
				this.closing(KeeperException.Code.SessionExpired);
				break;
			case AuthFailed:
				break;
			case ConnectedReadOnly:
				break;
			case Disconnected:
				break;
			case SaslAuthenticated:
				break;
			default:
				break;
			}
		} else {
			if (path != null && path.equals(znode)) {
				// Something has changed on the node, let's find out
				zk.exists(znode, true, this, null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.zookeeper.AsyncCallback.StatCallback#processResult(int,
	 * java.lang.String, java.lang.Object, org.apache.zookeeper.data.Stat)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		boolean exists;
		switch (rc) {
		case Code.Ok:
			exists = true;
			break;
		case Code.NoNode:
			exists = false;
			break;
		case Code.SessionExpired:
		case Code.NoAuth:
			dead = true;
			this.closing(rc);
			return;
		default:
			// Retry errors
			zk.exists(znode, true, this, null);
			return;
		}

		byte b[] = null;
		if (exists) {
			try {
				b = zk.getData(znode, false, null);
			} catch (KeeperException e) {
				// We don't need to worry about recovering now. The watch
				// callbacks will kick off any exception handling
				e.printStackTrace();
			} catch (InterruptedException e) {
				return;
			}
		}
		if ((b == null && b != prevData) || (b != null && !Arrays.equals(prevData, b))) {
			this.exists(b);
			prevData = b;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			synchronized (this) {
				while (!dead) {
					wait();
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
