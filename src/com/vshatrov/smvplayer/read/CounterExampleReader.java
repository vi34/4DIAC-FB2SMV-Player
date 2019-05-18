package com.vshatrov.smvplayer.read;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.fordiac.ide.model.libraryElement.CompositeFBType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CounterExampleReader {

	final String DATA_DELIMETER = ",";
	final String NAME_DELIMETER = "[.]";
	
	public CounterExample readCSV(String filePath) throws IOException {
		CounterExample result = new CounterExample(); 
		List<String> lines = Files.readAllLines(Paths.get(filePath));
		String[] states = lines.get(0).replaceAll("-1[.]", "").split(DATA_DELIMETER);
		result.states = Arrays.copyOfRange(states, 1, states.length);
		result.vars = new CounterExample.VarQualifier[lines.size() - 1];

		for (int i = 1; i < lines.size(); i++) {
			String[] var = lines.get(i).split(DATA_DELIMETER);
			List<String> nameParts = Arrays.asList(var[0].split(NAME_DELIMETER));
			CounterExample.VarQualifier varQualifier = new CounterExample.VarQualifier();
			varQualifier.FQN = var[0];
			varQualifier.parts = nameParts;
			varQualifier.data = Arrays.copyOfRange(var, 1, var.length);
			result.vars[i - 1] = varQualifier;
		}
		sort(result);
		return result;
	}

	private void sort(CounterExample result) {
		Arrays.sort(result.vars, Comparator.comparing(v -> v.FQN));
	}


}
