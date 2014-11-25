package com.it.config.csvip;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class GeoInfo {

	private String country;
	private String region;
	private String city;
	private String isp;

	private int hashCode = 0;
	private EqualsBuilder equalsBuilder;
	private String toString;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = new HashCodeBuilder().append(country).append(region)
					.append(city).append(isp).toHashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj.getClass() != this.getClass()) {
			return false;
		}

		if (equalsBuilder == null) {
			equalsBuilder = new EqualsBuilder();
		}

		GeoInfo o = (GeoInfo) obj;

		return equalsBuilder.append(country, o.country)
				.append(region, o.region).append(city, o.city)
				.append(isp, o.isp).isEquals();
	}

	@Override
	public String toString() {
		if (toString == null) {
			toString = new ToStringBuilder(this,
					ToStringStyle.SHORT_PREFIX_STYLE).append(country)
					.append(region).append(city).append(isp).toString();
		}
		return toString;
		// StringBuilder sb = new StringBuilder();
		// sb.append(country).append('\t').append(region).append('\t')
		// .append(city).append('\t').append(isp);
		// return sb.toString();
	}

	public GeoInfo(String country, String region, String city, String isp) {
		this.country = country;
		this.region = region;
		this.city = city;
		this.isp = isp;
	}

	public GeoInfo(GeoInfo other) {
		this.country = other.country;
		this.region = other.region;
		this.city = other.city;
		this.isp = other.isp;
	}

	public String getCountry() {
		return country;
	}

	// public void setCountry(String country) {
	// this.country = country;
	// }

	public String getRegion() {
		return region;
	}

	// public void setRegion(String region) {
	// this.region = region;
	// }

	public String getCity() {
		return city;
	}

	// public void setCity(String city) {
	// this.city = city;
	// }

	public String getIsp() {
		return isp;
	}

	// public void setIsp(String isp) {
	// this.isp = isp;
	// }

}
