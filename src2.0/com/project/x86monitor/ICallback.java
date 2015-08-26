package com.project.x86monitor;

import com.huiming.base.jdbc.DataRow;

public interface ICallback {
	boolean filter(String exclusion);
	boolean filter(DataRow data);
}
