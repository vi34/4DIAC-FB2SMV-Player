package com.vshatrov.smvplayer;

import com.vshatrov.smvplayer.read.CounterExample;
import com.vshatrov.smvplayer.read.CounterExampleReader;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import javax.inject.Inject;
import java.io.IOException;

public class CounterExampleView extends ViewPart {
	public static final String ID = "com.vshatrov.smvplayer.counterexampleview";
	public static final Color UNMAPPED = new Color(null, 212, 212, 212, 50);
	public static final Color IMPLICIT = new Color(null, 255, 252, 238);
	public static final Color CHANGED = new Color(null, 73, 212, 134, 10);

	@Inject IWorkbench workbench;

	private Composite sectionClient;
	private Composite tableSection;
	private Table tableViewer;
	private Spinner step;
	private SmvPlayer smvPlayer = null;
	private Label currentFB;
	private Label currentTime;
	private CounterExample counterExample;

	public void setCurrentFB(String currentFB) {
		this.currentFB.setText(currentFB);
		sectionClient.layout();
		sectionClient.redraw();
	}

	public void setTime(int currentTime) {
		this.currentTime.setText("" + currentTime);
		sectionClient.layout();
	}

	private class StringLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {
			return super.getText(element);
		}

		@Override
		public Image getImage(Object obj) {
			return null;
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		setEditor();

		sectionClient = new Composite(parent, SWT.NONE);
		sectionClient.setLayout(new GridLayout(1, false));
		Composite controlsPane = new Composite(sectionClient, SWT.NONE);
		controlsPane.setLayout(new GridLayout(2, false));
		Label stepLabel = new Label(controlsPane, SWT.LEFT);
		stepLabel.setText("Step: ");
		step = new Spinner(controlsPane, SWT.RIGHT);
		step.setToolTipText("Step: ");
		step.setMinimum(1);
		step.setMaximum(100);
		step.setIncrement(1);
		step.setPageIncrement(4);
		step.addModifyListener(e -> {
			int selection = step.getSelection();
			stepLabel.setText("Step " + selection + " : ");
			setEditor();
			if (smvPlayer != null) {
				smvPlayer.setState(step.getSelection() - 1);
			}
			sectionClient.redraw();
		});

		Label currentFBLabel = new Label(controlsPane, SWT.LEFT);
		currentFBLabel.setText("Current FB: ");
		currentFB = new Label(controlsPane, SWT.LEFT);
		currentFB.setText("");
		Label currentTimeLabel = new Label(controlsPane, SWT.LEFT);
		currentTimeLabel.setText("Current Time: ");
		currentTime = new Label(controlsPane, SWT.LEFT);
		currentTime.setText("");

//		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
//		column.setLabelProvider(new StringLabelProvider());
//		tableSection = new Composite(sectionClient, SWT.NONE);
//		tableSection.setLayout(new GridLayout(1, false));
		Button pickFile = new Button(controlsPane, SWT.NONE);
		pickFile.setText("Choose counterexample");
		pickFile.addListener(SWT.Selection, e -> {
			FileDialog dialog = new FileDialog(getViewSite().getShell(), SWT.OPEN);
			String file = dialog.open();
			CounterExampleReader counterExampleReader = new CounterExampleReader();
			try {
				counterExample = counterExampleReader.readCSV(file);
				step.setMaximum(counterExample.states.length);
				setEditor();
				buildTable(counterExample);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

//		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
	}

	private void buildTable(CounterExample counterExample) {
		if (tableViewer != null) {
			tableViewer.dispose();
		}
		tableViewer = new Table(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		tableViewer.setLayoutData(data);
		tableViewer.setLinesVisible(true);
		tableViewer.setHeaderVisible(true);
		TableColumn varsColumn = new TableColumn (tableViewer, SWT.NONE);

		for (int i = tableViewer.getColumnCount(); i < counterExample.states.length + 1; i++) {
			new TableColumn(tableViewer, SWT.FULL_SELECTION);
		}

		tableViewer.getColumn(0).setText("Variables \\ States");
		for (int i = 1; i < counterExample.states.length + 1; i++) {
			TableColumn column = tableViewer.getColumn(i);
			column.setText(counterExample.states[i - 1]);
		}

		for (int i = 0; i < counterExample.data.length; i++) {
			String[] varStates = counterExample.data[i];

			TableItem item = new TableItem(tableViewer, SWT.NONE);
			CounterExample.VarQualifier qualifier = counterExample.vars[i];
			item.setText(0, qualifier.FQN);

			if (!qualifier.mapped) {
				item.setBackground(UNMAPPED);
			} else if (!qualifier.explicit) {
				item.setBackground(IMPLICIT);
			}

			String prev = varStates[0];
			for (int j = 0; j < varStates.length; j++) {
				item.setText(j + 1, varStates[j]);
				if (!prev.equals(varStates[j])) {
					item.setBackground(j + 1, CHANGED);
				}
				prev = varStates[j];
			}

		}

		for (int i = 0; i < tableViewer.getColumnCount(); i++) {
			tableViewer.getColumn(i).pack();
		}
		tableViewer.setSortColumn(tableViewer.getColumn(0));
		tableViewer.setSortDirection(SWT.UP);
		tableViewer.layout();
		sectionClient.redraw();
	}

	public void setEditor() {
		IEditorPart activeEditor = getSite().getPage().getActiveEditor();
		if (activeEditor instanceof SmvPlayer && (smvPlayer == null ||smvPlayer != activeEditor)) {
			smvPlayer = (SmvPlayer) activeEditor;
			smvPlayer.setCEView(this);
		}
		if (counterExample != null) {
			smvPlayer.setCounterExample(counterExample);
		}
	}

	@Override
	public void setFocus() {
		tableViewer.setFocus();
	}
}