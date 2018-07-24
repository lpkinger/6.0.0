package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import com.uas.mobile.model.Stats;

public interface StatsService {
	 Map<String,List<Stats>> getStats();
	 Stats getStats(int id,String config); 
}
