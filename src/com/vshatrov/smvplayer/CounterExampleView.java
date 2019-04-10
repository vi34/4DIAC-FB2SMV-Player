package com.vshatrov.smvplayer;

import java.util.*;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

public class CounterExampleView extends ViewPart {
	public static final String ID = "com.vshatrov.smvplayer.counterexampleview";

	@Inject IWorkbench workbench;
	
	private Composite sectionClient;
	private TableViewer viewer;
	private Spinner step;
	
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
		sectionClient = new Composite(parent, SWT.NONE);
		sectionClient.setLayout(new GridLayout(1, false));	
		
		Label stepLabel = new Label(sectionClient, SWT.NONE);
		stepLabel.setText("Step: ");
		step = new Spinner(sectionClient, SWT.NONE);
		step.setToolTipText("Step: ");
		step.setMinimum(0);
		step.setMaximum(100);
		step.setIncrement(1);
		step.setPageIncrement(4);
		step.addModifyListener(e -> {
			
			stepLabel.setText("Step " + step.getSelection() + " : ");
			IEditorPart activeEditor = getSite().getPage().getActiveEditor();
			if (activeEditor instanceof SmvPlayer) {
				SmvPlayer smvPlayer = (SmvPlayer) activeEditor;
				smvPlayer.getModel().getNetworkElements().forEach(fb -> {
					stepLabel.setText(stepLabel.getText() + fb.getName());
				});
			}
			sectionClient.redraw();
		});
		viewer = new TableViewer(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTable().setLinesVisible(true);

		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.setLabelProvider(new StringLabelProvider());

		viewer.getTable().getColumn(0).setWidth(200);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		
		// Provide the input to the ContentProvider
		viewer.setInput(createInitialDataModel());
	}


	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	private List<String> createInitialDataModel() {
		return Arrays.asList("One", "Two", "Three");
	}
}