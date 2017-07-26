package edu.nd.dronology.monitoring.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class BenchmarkLogger {

	static boolean ACTIVE = false;

	static {

	}

	public static void init() {
		if (!ACTIVE) {
			return;
		}
		setRunCounter();
		Timer timer = new Timer();
		Date date = new Date();
		long t = date.getTime();
		Date afterAddingTenMins = new Date(t + (WARMUP_TIME));
		long start = System.currentTimeMillis();

		reportStatic("", 0, "Task Report Scheduled");
		reportMonitor("", "", 0, "Task Report Scheduled");
		timer.schedule(new WriterTask(), afterAddingTenMins, BENCHMARK_FRAME);

	}

	static List<Long> listLength = new ArrayList<>();
	static List<Long> listTime = new ArrayList<>();

	private static final String FOLDER_NAME = "E:\\reports\\";
	private static final String FILE_NAME_STATIC = "run[X]_ST.txt";
	private static final String FILE_NAME_MONITOR = "run[X]_RT.txt";
	private static final String FILE_NAME_FREQUENCY = "run[X]_FR.txt";
	private static final String FILE_NAME_TRUST = "run[X]_AT.txt";
	private static final String FILE_NAME_UAV_TRUST = "run[X]_UT.txt";
	public static final String SEPARATOR = ";";
	static SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	private static List<String> staticList = Collections.synchronizedList(new ArrayList<>());
	private static List<String> monitorList = Collections.synchronizedList(new ArrayList<>());
	private static List<String> trustList = Collections.synchronizedList(new ArrayList<>());
	private static List<String> trustUAVList = Collections.synchronizedList(new ArrayList<>());
	private static List<String> frequencyList = Collections.synchronizedList(new ArrayList<>());

	static final int BENCHMARK_FRAME = 30000;
	public static final int WARMUP_TIME = 0 * 60000;

	public static synchronized void reportStatic(String uavid, long duration, String result) {
		if (!ACTIVE) {
			return;
		}
		long logTime = System.currentTimeMillis();
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String date = df2.format(new Date(logTime));
			stringBuilder.append(date);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(logTime));
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(uavid);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(result);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(duration));

			staticList.add(stringBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reportTrust(String uavid, String assumptionid, double rating, long duration) {
		if (!ACTIVE) {
			return;
		}
		long logTime = System.currentTimeMillis();
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String date = df2.format(new Date(logTime));
			stringBuilder.append(date);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(logTime));
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(uavid);
			stringBuilder.append(SEPARATOR);
			// writer.append(passed);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(duration));

			trustList.add(stringBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static synchronized void reportUAVTrust(String uavid, double reputation, long duration) {
		if (!ACTIVE) {
			return;
		}
		long logTime = System.currentTimeMillis();
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String date = df2.format(new Date(logTime));
			stringBuilder.append(date);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(logTime));
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(uavid);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Double.toString(reputation));
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(duration));

			trustUAVList.add(stringBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reportFrequency(String uavid, double frequency) {
		if (!ACTIVE) {
			return;
		}
		long logTime = System.currentTimeMillis();
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String date = df2.format(new Date(System.currentTimeMillis()));
			stringBuilder.append(date);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(logTime));
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(uavid);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Double.toString(frequency));
			frequencyList.add(stringBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void reportMonitor(String uavid, String assumptonid, long time, String result) {
		if (!ACTIVE) {
			return;
		}
		long logTime = System.currentTimeMillis();
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String date = df2.format(new Date(System.currentTimeMillis()));
			stringBuilder.append(date);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(logTime));
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(uavid);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(assumptonid);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(result);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(Long.toString(time));
			monitorList.add(stringBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int run = 0;

	private static void setRunCounter() {
		while (true) {
			File f = new File(FOLDER_NAME + FILE_NAME_STATIC.replace("X", Integer.toString(run)));
			if (f.exists()) {
				run++;
			} else {
				break;
			}
		}
		while (true) {
			File f = new File(FOLDER_NAME + FILE_NAME_MONITOR.replace("X", Integer.toString(run)));
			if (f.exists()) {
				run++;
			} else {
				break;
			}
		}
	}

	static class WriterTask extends TimerTask {
		private int last = 0;
		private int runCounter = 0;
		private long lastTime;

		@Override
		public void run() {
			List<String> staticWrite;

			List<String> monitorWrite;
			List<String> trustWrite;
			List<String> frequencyWrite;
			List<String> trustUAVWrite;
			synchronized (staticList) {
				staticWrite = new ArrayList<>(staticList);
				staticList.clear();
			}
			synchronized (monitorList) {
				monitorWrite = new ArrayList<>(monitorList);
				monitorList.clear();
			}
			synchronized (trustList) {
				trustWrite = new ArrayList<>(trustList);
				trustList.clear();
			}
			
			synchronized (trustUAVList) {
				trustUAVWrite = new ArrayList<>(trustUAVList);
				trustUAVList.clear();
			}
			synchronized (frequencyList) {
				frequencyWrite = new ArrayList<>(frequencyList);
				frequencyList.clear();
			}

			String staticString = staticWrite.stream().collect(Collectors.joining(System.getProperty("line.separator")))
					+ System.getProperty("line.separator");
			String monitorString = monitorWrite.stream().collect(
					Collectors.joining(System.getProperty("line.separator"))) + System.getProperty("line.separator");
			String trustString = trustWrite.stream().collect(Collectors.joining(System.getProperty("line.separator")))
					+ System.getProperty("line.separator");
			String frequencyString = frequencyWrite.stream().collect(
					Collectors.joining(System.getProperty("line.separator"))) + System.getProperty("line.separator");
			
			String uavTrustString = trustUAVWrite.stream().collect(
					Collectors.joining(System.getProperty("line.separator"))) + System.getProperty("line.separator");

			try {

				Files.write(Paths.get(FOLDER_NAME + FILE_NAME_STATIC.replace("X", Integer.toString(run))),
						staticString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				Files.write(Paths.get(FOLDER_NAME + FILE_NAME_MONITOR.replace("X", Integer.toString(run))),
						monitorString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				Files.write(Paths.get(FOLDER_NAME + FILE_NAME_TRUST.replace("X", Integer.toString(run))),
						trustString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				Files.write(Paths.get(FOLDER_NAME + FILE_NAME_FREQUENCY.replace("X", Integer.toString(run))),
						frequencyString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				Files.write(Paths.get(FOLDER_NAME + FILE_NAME_UAV_TRUST.replace("X", Integer.toString(run))),
						uavTrustString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// print();

		}
	}

}
