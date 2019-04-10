/*******************************************************************************
 * Copyright (c) 2013, 2014 Profactor GmbH, fortiss GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gerhard Ebenhofer, Alois Zoitl
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package com.vshatrov.smvplayer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.fordiac.ide.application.ApplicationPlugin;
import org.eclipse.fordiac.ide.application.Messages;
import org.eclipse.fordiac.ide.application.editparts.FBEditPart;
import org.eclipse.fordiac.ide.application.viewer.composite.CompositeInstanceViewerInput;
import org.eclipse.fordiac.ide.model.libraryElement.FB;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * The Class OpenSubApplicationEditorAction.
 */
public class OpenSmvPlayerAction extends AbstractHandler {

	private static final String SMV_PLAYER_ID = "com.vshatrov.smvplayer.SmvPlayer"; //$NON-NLS-1$
	/** The fb. */
	private FB fb;
	private FBEditPart fbEditPart;
	
	protected void prepareParametersToExecute(Object element){
		if (element instanceof FBEditPart){
			fbEditPart = (FBEditPart) element;
			fb = ((FBEditPart) element).getModel();
		}else if(element instanceof FB){
			fb = (FB)element;
		}
	}
	
	@Override
	public void setEnabled(Object evaluationContext){
		setBaseEnabled(true);
	}

	/**
	 * Opens the editor for the specified Model or sets the focus to the editor
	 * if already opened.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection selection = (StructuredSelection) HandlerUtil.getCurrentSelection(event);
		prepareParametersToExecute(selection.getFirstElement());
		CompositeInstanceViewerInput input = new CompositeInstanceViewerInput(fbEditPart, fb, fb.getName());

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			activePage.openEditor(input, SMV_PLAYER_ID);			
		} catch (PartInitException e) {
			ApplicationPlugin.getDefault().logError(
					"Composite Instance editor can not be opened: ", e); //$NON-NLS-1$
		}
		return null;
	}

}
