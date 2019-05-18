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
	public static final int TABLE_STYLE = SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION;
	public static final int TABLE_HEIGHT = 250;

	@Inject IWorkbench workbench;

	private Composite sectionClient;
	private Composite tableSection;
	private Table table;
	private Spinner step;
	private SmvPlayer smvPlayer = null;
	private Label currentFB;
	private Label currentTime;
	private CounterExample counterExample;
	private TableColumn currentStateColumn;

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
			stepLabel.setText("Step " + step.getSelection() + " : ");
			setEditor();
			int state = step.getSelection() - 1;
			if (smvPlayer != null) {
				smvPlayer.setState(state);
			}
			selectColumn(state);
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

		Button pickFile = new Button(controlsPane, SWT.NONE);
		pickFile.setText("Choose counterexample");
		pickFile.addListener(SWT.Selection, e -> {
			FileDialog dialog = new FileDialog(getViewSite().getShell(), SWT.OPEN);
			String file = dialog.open();
			CounterExampleReader counterExampleReader = new CounterExampleReader();
			try {
				counterExample = counterExampleReader.readCSV(file);
				reset();
				step.setMaximum(counterExample.states.length);
				buildTable(counterExample);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	private void selectColumn(int state) {
		TableColumn currentState = table.getColumn(state);
		table.showColumn(currentState);
		if (currentStateColumn != null) {
			currentStateColumn.setText(currentStateColumn.getText().substring(1));
		}
		currentStateColumn = currentState;
		currentStateColumn.setText("▶" + currentStateColumn.getText());
	}

	private void buildTable(CounterExample counterExample) {
		tableSection = new Composite(sectionClient, SWT.NONE);
		GridLayout tsectionLayout = new GridLayout(2, false);
		tsectionLayout.marginWidth = 0;
		tableSection.setLayout(tsectionLayout);
		Table varsTable = new Table(tableSection, TABLE_STYLE);
		GridData layoutData = new GridData(SWT.LEFT, SWT.FILL, true, true);
		layoutData.heightHint = TABLE_HEIGHT;
		layoutData.widthHint = 225;
		varsTable.setLayoutData(layoutData);
		varsTable.setLinesVisible(true);
		varsTable.setHeaderVisible(true);
		TableColumn varsColumn = new TableColumn(varsTable, SWT.FILL);
		varsColumn.setText("Variables \\ States");

		table = new Table(tableSection, TABLE_STYLE | SWT.H_SCROLL);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		data.heightHint = TABLE_HEIGHT;
		data.widthHint = 650;
		table.setLayoutData(data);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		syncTables(varsTable, table);

		for (int i = table.getColumnCount(); i < counterExample.states.length; i++) {
			new TableColumn(table, SWT.FULL_SELECTION);
		}

		for (int i = 0; i < counterExample.states.length; i++) {
			TableColumn column = table.getColumn(i);
			column.setText(counterExample.states[i]);
		}

		for (int i = 0; i < counterExample.vars.length; i++) {
			String[] varStates = counterExample.vars[i].data;

			TableItem item = new TableItem(table, SWT.NONE);
			TableItem varItem = new TableItem(varsTable, SWT.NONE);
			CounterExample.VarQualifier qualifier = counterExample.vars[i];
			varItem.setText(0, qualifier.FQN);

			if (!qualifier.mapped) {
				item.setBackground(UNMAPPED);
				varItem.setBackground(UNMAPPED );
			} else if (!qualifier.explicit) {
				item.setBackground(IMPLICIT);
				varItem.setBackground(IMPLICIT);
			}

			String prev = varStates[0];
			for (int j = 0; j < varStates.length; j++) {
				item.setText(j, varStates[j]);
				if (!prev.equals(varStates[j])) {
					item.setBackground(j, CHANGED);
				}
				prev = varStates[j];
			}
		}

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}
		varsColumn.pack();
		varsTable.layout();
		table.layout();
		tableSection.layout();
		tableSection.redraw();
	}

	private void reset() {
		step.setSelection(1);
		if (tableSection != null) {
			tableSection.dispose();
		}
		currentStateColumn = null;
		setEditor();
	}

	private void syncTables(Table leftTable, Table rigtTable) {
		leftTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				rigtTable.setSelection(leftTable.getSelectionIndices());
			}
		});
		rigtTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				leftTable.setSelection(rigtTable.getSelectionIndices());
			}
		});
		ScrollBar vBarLeft = leftTable.getVerticalBar();
		vBarLeft.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				rigtTable.setTopIndex(leftTable.getTopIndex());
			}
		});
		ScrollBar vBarRight = rigtTable.getVerticalBar();
		vBarRight.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				leftTable.setTopIndex(rigtTable.getTopIndex());
			}
		});
		// Horizontal bar on second rigtTable takes up a little extra space.
		// To keep vertical scroll bars in sink, force table1 to end above
		// horizontal scrollbar
//		ScrollBar hBarRight = rigtTable.getHorizontalBar();
//		Label spacer = new Label(tableSection, SWT.NONE);
//		GridData spacerData = new GridData();
//		spacerData.heightHint = hBarRight.getSize().y;
//		spacer.setVisible(false);
		tableSection.setBackground(leftTable.getBackground());
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
		table.setFocus();
	}
}