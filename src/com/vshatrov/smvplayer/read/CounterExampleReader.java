package com.vshatrov.smvplayer.read;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.fordiac.ide.model.libraryElement.CompositeFBType;

public class CounterExampleReader {

	final String DATA_DELIMETER = ",";
	final String NAME_DELIMETER = ".";
	private CompositeFBType topFB;
	
	public CounterExample readCSV(String filePath) throws IOException {
		CounterExample result = new CounterExample(); 
		List<String> lines = Files.readAllLines(Paths.get(filePath));
		String[] states = lines.get(0).split(DATA_DELIMETER);
		result.length = states.length - 1;
		for (int i = 1; i < lines.size(); i++) {
			String[] var = lines.get(i).split(DATA_DELIMETER);
			String name = var[0];
			String[] nameParts = name.split(NAME_DELIMETER);
			if (isTopLevelFB(nameParts[0])) {
				
			}
		}
		return result;
	}

	private boolean isTopLevelFB(String smvName) {
		return smvName.startsWith(this.topFB.getName()) && smvName.endsWith("_inst");
	}

	public void setFB(CompositeFBType cfbt) {
		this.topFB = cfbt;
	}
	
}
