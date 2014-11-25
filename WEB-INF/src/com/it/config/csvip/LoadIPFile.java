package com.it.config.csvip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.it.config.IPParserLoad;
import com.it.config.load.ConfigLoad;

import au.com.bytecode.opencsv.CSVReader;

public class LoadIPFile {
	protected static Logger logger = Logger.getLogger(LoadIPFile.class);

	public static NavigableSet<IPCompare> loadFile(List<String> lstFile,
			Map<GeoInfo, Integer> geoInfoMap) {
		NavigableSet<IPCompare> set = new ConcurrentSkipListSet<IPCompare>();

		AtomicInteger geoInfoIndex = new AtomicInteger(0);

		for (String filename : lstFile) {
			File file = new File(filename);
			if (!file.exists()) {
				logger.error("========file  not exists:" + filename);
				continue;
			}

			try {
				CSVReader reader = new CSVReader(new BufferedReader(
						new InputStreamReader(new FileInputStream(file),
								Charset.forName("UTF-8"))), '\t');
				// new FileReader(file)), '\t');
				String[] fields = null;
				try {
					while ((fields = reader.readNext()) != null) {
						if (fields.length < 3) {
							logger.error("===== line format error:"
									+ Arrays.toString(fields));
							continue;
						}

						// startLong, endLong, startIP, endIP, country, region,
						// city, isp

						GeoInfo geo = new GeoInfo(fields[2],
								fields.length < 4 ? "" : fields[3],
								fields.length < 5 ? "" : fields[4],
								fields.length < 6 ? "" : fields[5]);
						Integer geoIndex = geoInfoMap.get(geo);
						if (geoIndex == null) {
							geoIndex = geoInfoIndex.getAndIncrement();
							geoInfoMap.put(geo, geoIndex);
						}
						long begin = IPParserLoad.ip2long(fields[0]);
						long end = IPParserLoad.ip2long(fields[1]);
						IPCompare ip = new IPCompare(begin, end, geoIndex);
						// tmp.setStart(ip.getEnd() + 1);
						// tmp.setEnd(tmp.getStart());
						// SortedSet<IPInfo> subSet = set.subSet(ip, tmp);
						SortedSet<IPCompare> subSet = set.tailSet(ip);
						for (IPCompare ii : subSet) {
							if (ip.compareTo(ii) < 0) {
								break;
							}
							if (ii.getStart() < ip.getStart()) {
								if (ii.getEnd() < ip.getEnd()) {
									if (ii.getEnd() < ip.getStart()) {
										// ii:1~5, ip:6~10
										continue;
									} else {
										// ii:1~7, ip:5~10/7~10
										set.remove(ii);

										ii.setEnd(ip.getStart() - 1);
										set.add(ii);
									}
								} else if (ii.getEnd() < ip.getEnd()) {
									// ii:1~10, ip:5~10
									set.remove(ii);

									ii.setEnd(ip.getStart() - 1);
									set.add(ii);
								} else {
									// ii:1~15, ip:5~10
									set.remove(ii);

									IPCompare other = new IPCompare(ii);
									other.setStart(ip.getEnd() + 1);
									set.add(other);

									ii.setEnd(ip.getStart() - 1);
									set.add(ii);
								}
							} else if (ii.getStart() == ip.getStart()) {
								if (ii.getEnd() <= ip.getEnd()) {
									// ii:1~10, ip:1~10/1~15
									set.remove(ii);
								} else if (ii.getEnd() > ip.getEnd()) {
									// ii:1~10, ip:1~5
									set.remove(ii);

									ii.setStart(ip.getEnd() + 1);
									set.add(ii);
								}
							} else {// ii.getStart() > ip.getStart()
								if (ii.getEnd() > ip.getEnd()) {
									if (ii.getStart() > ip.getEnd()) {
										// ii:6~10, ip:1~5
										continue;
									} else {
										// ii:5~10/7~10, ip:1~7
										set.remove(ii);

										ii.setStart(ip.getEnd() + 1);
										set.add(ii);
									}
								} else {
									// ii:5~10/5~15, ip:1~15
									set.remove(ii);
								}
							}
						}
						set.add(ip);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return set;
	}
}
