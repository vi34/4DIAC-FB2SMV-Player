package com.vshatrov.smvplayer;

import com.vshatrov.smvplayer.read.CounterExampleReader;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CounterExampleView extends ViewPart {
	public static final String ID = "com.vshatrov.smvplayer.counterexampleview";

	@Inject IWorkbench workbench;
	
	private Composite sectionClient;
	private TableViewer tableViewer;
	private Spinner step;
	private SmvPlayer smvPlayer = null;
	
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
		sectionClient.setLayout(new GridLayout(2, false));	
		
		Label stepLabel = new Label(sectionClient, SWT.LEFT);
		stepLabel.setText("Step: ");
		step = new Spinner(sectionClient, SWT.RIGHT);
		step.setToolTipText("Step: ");
		step.setMinimum(0);
		step.setMaximum(100);
		step.setIncrement(1);
		step.setPageIncrement(4);
		step.addModifyListener(e -> {
			int selection = step.getSelection();
			stepLabel.setText("Step " + selection + " : ");
			setEditor();
			if (smvPlayer != null) {
				smvPlayer.setStep(step.getSelection());
			}
			sectionClient.redraw();
		});
		
		Button pickFile = new Button(sectionClient, SWT.NONE);
		pickFile.addListener(SWT.Selection, e -> {
			FileDialog dialog = new FileDialog(getViewSite().getShell(), SWT.OPEN);
			String file = dialog.open();
			CounterExampleReader counterExampleReader = new CounterExampleReader();
			counterExampleReader.setFB(smvPlayer.cfbt);
			try {
				counterExampleReader.readCSV(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		
		
		tableViewer = new TableViewer(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getTable().setLinesVisible(true);

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.setLabelProvider(new StringLabelProvider());

		tableViewer.getTable().getColumn(0).setWidth(200);
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		// Provide the input to the ContentProvider
		tableViewer.setInput(createInitialDataModel());
	}


	private void setEditor() {
		IEditorPart activeEditor = getSite().getPage().getActiveEditor();
		if (activeEditor instanceof SmvPlayer && (smvPlayer == null ||smvPlayer != activeEditor)) {
			smvPlayer = (SmvPlayer) activeEditor;
		}
	}


	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
	
	private List<String> createInitialDataModel() {
		return Arrays.asList("One", "Two", "Three");
	}
}