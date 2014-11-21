package com.it.config.load;

import java.util.List;

public class SchedulerConfigLoad {
	private List<ConfigLoad> lstConfigLoad;

	public List<ConfigLoad> getLstConfigLoad() {
		return lstConfigLoad;
	}

	public void setLstConfigLoad(List<ConfigLoad> lstConfigLoad) {
		this.lstConfigLoad = lstConfigLoad;
	}

	/**
	 * 顺序加载各个配置的配置信息
	 */
	public void load() {
		if (lstConfigLoad != null) {
			for (ConfigLoad configLoad : lstConfigLoad) {
				configLoad.load();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
