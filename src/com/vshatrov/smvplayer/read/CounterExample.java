package com.vshatrov.smvplayer.read;

import java.util.List;

public class CounterExample {
	public VarQualifier[] vars;
	public String[] states;

	public static class VarQualifier {
		public String FQN;
		public List<String> parts;
		public boolean mapped;
		public boolean explicit;
		public String[] data;
	}
}
