package com.krawl.util;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krawl.KrawlApplication;
import com.krawl.data.controller.HostController;
import com.krawl.data.controller.TopicController;
import com.krawl.data.model.Host;
import com.krawl.data.model.Vertex;
import com.krawl.engine.QueueBridge;

// TODO: Auto-generated Javadoc
/**
 * The Class KrawlUtil.
 */
public class KrawlUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static ObjectMapper mapper = new ObjectMapper();
	private static Long index = new Long(1);

	/**
	 * Gets the queue pool size.
	 *
	 * @param qpool
	 *            the qpool
	 * @return the queue pool size
	 */
	public synchronized static void getQueuePoolSize(CopyOnWriteArrayList<BlockingQueue<URL>> qpool) {
		System.out.println("Pool:");
		int i = 0;
		for (BlockingQueue<URL> q : qpool) {
			System.out.print("[" + i++ + ", " + q.size() + "]");
		}
		System.out.println();
	}

	/**
	 * Gets the tiny file name.
	 *
	 * @return the tiny file name
	 */
	public static String getTinyFileName() {
		String id = Base64.getEncoder().encodeToString(ByteBuffer.allocate(Long.BYTES).putLong(index++).array());
		if (id.contains("/"))
			id = getTinyFileName();
		return id;
	}

	/**
	 * Write to json.
	 *
	 * @param v
	 *            the v
	 * @throws Exception
	 *             the exception
	 */
	public void writeToJson(Vertex v) throws Exception {
		URL url = v.getUrl();
		Path path = Paths
				.get(KrawlApplication.getKrawlConfig().getIndexPath() + "/" + url.getHost() + "/" + url.getPath());

		Path file = Paths.get(KrawlApplication.getKrawlConfig().getIndexPath() + "/" + url.getHost() + "/"
				+ url.getPath().substring(0, url.getPath().lastIndexOf("/") + 1) + v.getId()
				+ KrawlApplication.getKrawlConfig().getExtension());

		@SuppressWarnings("unused")
		long timediff = System.nanoTime();
		if (!Files.exists(Paths.get(KrawlApplication.getKrawlConfig().getIndexPath() + "/" + url.getHost()))) {
			try {
				Host host = new HostController().createHost(url);
				new TopicController().createTopic(url);
				QueueBridge.getBackQueueHostList().get(new Random().nextInt(QueueBridge.getBackQueuePool().size()))
						.add(host.getName());
			} catch (Exception e) {
				LOGGER.error(e.toString(), e);
			}
		}
		Files.createDirectories(path);
		mapper.writeValue(new File(file.toString()), v);
	}

	public static double getJaroWinklerDistance(final CharSequence first, final CharSequence second) {
		final double DEFAULT_SCALING_FACTOR = 0.1;

		if (first == null || second == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		final int[] mtp = matches(first, second);
		final double m = mtp[0];
		if (m == 0) {
			return 0D;
		}
		final double j = ((m / first.length() + m / second.length() + (m - mtp[1]) / m)) / 3;
		final double jw = j < 0.7D ? j : j + Math.min(DEFAULT_SCALING_FACTOR, 1D / mtp[3]) * mtp[2] * (1D - j);
		return Math.round(jw * 100.0D) / 100.0D;
	}

	private static int[] matches(final CharSequence first, final CharSequence second) {
		CharSequence max, min;
		if (first.length() > second.length()) {
			max = first;
			min = second;
		} else {
			max = second;
			min = first;
		}
		final int range = Math.max(max.length() / 2 - 1, 0);
		final int[] matchIndexes = new int[min.length()];
		Arrays.fill(matchIndexes, -1);
		final boolean[] matchFlags = new boolean[max.length()];
		int matches = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			final char c1 = min.charAt(mi);
			for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1, max.length()); xi < xn; xi++) {
				if (!matchFlags[xi] && c1 == max.charAt(xi)) {
					matchIndexes[mi] = xi;
					matchFlags[xi] = true;
					matches++;
					break;
				}
			}
		}
		final char[] ms1 = new char[matches];
		final char[] ms2 = new char[matches];
		for (int i = 0, si = 0; i < min.length(); i++) {
			if (matchIndexes[i] != -1) {
				ms1[si] = min.charAt(i);
				si++;
			}
		}
		for (int i = 0, si = 0; i < max.length(); i++) {
			if (matchFlags[i]) {
				ms2[si] = max.charAt(i);
				si++;
			}
		}
		int transpositions = 0;
		for (int mi = 0; mi < ms1.length; mi++) {
			if (ms1[mi] != ms2[mi]) {
				transpositions++;
			}
		}
		int prefix = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			if (first.charAt(mi) == second.charAt(mi)) {
				prefix++;
			} else {
				break;
			}
		}
		return new int[] { matches, transpositions / 2, prefix, max.length() };
	}

}
