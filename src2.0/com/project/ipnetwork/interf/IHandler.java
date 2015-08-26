package com.project.ipnetwork.interf;

import java.io.BufferedReader;
import java.io.IOException;

import com.project.x86monitor.JsonData;

public interface IHandler {
	void handle(BufferedReader br, JsonData result) throws IOException;
}
