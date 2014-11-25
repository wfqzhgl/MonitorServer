package com.it.config.csvip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeSet;

public class LookupIP {
	private NavigableSet<IPCompare> ipSet;
	private GeoInfo[] geoInfoArr;

	public LookupIP(NavigableSet<IPCompare> ipSet, Map<GeoInfo, Integer> geoInfoMap) {
		this.ipSet = new TreeSet<IPCompare>(ipSet);
		geoInfoArr = new GeoInfo[geoInfoMap.size()];
		for (Entry<GeoInfo, Integer> entry : geoInfoMap.entrySet()) {
			geoInfoArr[entry.getValue()] = entry.getKey();
		}
	}

	public GeoInfo getLocation(String ip) throws UnknownHostException {
		IPCompare ipi = new IPCompare(ip);
		IPCompare ii = ipSet.ceiling(ipi);

		if (ipi.compareTo(ii) == 0) {
			return geoInfoArr[ii.getGeoInfoIndex()];
		} else {
			return null;
		}
	}

	public GeoInfo getLocation(long ip) {
		IPCompare ipi = new IPCompare(ip);
		IPCompare ii = ipSet.tailSet(ipi).first();

		if (ipi.compareTo(ii) == 0) {
			return geoInfoArr[ii.getGeoInfoIndex()];
		} else {
			return null;
		}
	}

	public GeoInfo getLocation(InetAddress ip) throws UnknownHostException {
		IPCompare ipi = new IPCompare(ip);
		IPCompare ii = ipSet.tailSet(ipi).first();

		if (ipi.compareTo(ii) == 0) {
			return geoInfoArr[ii.getGeoInfoIndex()];
		} else {
			return null;
		}
	}
}
