/*******************************************************************************
 * Copyright (c) 2013 - 2017 Profactor GmbH, fortiss GmbH
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

import com.vshatrov.smvplayer.read.CounterExample;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fordiac.ide.application.editparts.FBEditPart;
import org.eclipse.fordiac.ide.application.viewer.composite.CompositeInstanceViewerInput;
import org.eclipse.fordiac.ide.gef.DiagramEditor;
import org.eclipse.fordiac.ide.gef.ZoomUndoRedoContextMenuProvider;
import org.eclipse.fordiac.ide.gef.editparts.InterfaceEditPart;
import org.eclipse.fordiac.ide.model.libraryElement.*;
import org.eclipse.fordiac.ide.util.AdvancedPanningSelectionTool;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.ui.IEditorInput;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

public class SmvPlayer extends DiagramEditor {

	private FB fb;
	public CompositeFBType cfbt;
	private FBEditPart fbEditPart;
	private SmvPlayerEditPartFactory editPartFactory = null;
	private CounterExampleView counterExampleView;
	private int currentState = 0;
	private CounterExample counterExample;
	private Mapper mapper;
	private String currentFB;
	private int currentTime = 0;

	public FBEditPart getFbEditPart() {
		return fbEditPart;
	}

	public void setFbEditPart(FBEditPart fbEditPart) {
		this.fbEditPart = fbEditPart;
	}

	@Override
	protected EditPartFactory getEditPartFactory() {
		if (editPartFactory == null) {
			editPartFactory = new SmvPlayerEditPartFactory(this, fb, fbEditPart, getZoomManger());
		}
		return editPartFactory;
	}

	@Override
	protected ContextMenuProvider getContextMenuProvider(ScrollingGraphicalViewer viewer,
			ZoomManager zoomManager) {
		return new ZoomUndoRedoContextMenuProvider(getGraphicalViewer(), zoomManager, getActionRegistry());
	}

	@Override
	protected TransferDropTargetListener createTransferDropTargetListener() {
		//
		return null;
	}

	@Override
	public AutomationSystem getSystem() {
		return null;
	}

	@Override
	public String getFileName() {
		return null;
	}

	@Override
	protected void setModel(IEditorInput input) {

		setEditDomain(new DefaultEditDomain(this));
		getEditDomain().setDefaultTool(new AdvancedPanningSelectionTool());
		getEditDomain().setActiveTool(getEditDomain().getDefaultTool());

		if (input instanceof CompositeInstanceViewerInput) {
			CompositeInstanceViewerInput untypedInput = (CompositeInstanceViewerInput) input;
			Object content = untypedInput.getContent();
			if ((content instanceof FB) && (((FB) content).getType() instanceof CompositeFBType)) {
				fb = (FB) content;
				setPartName(getNameHierarchy());
				//we need to copy the type so that we have an instance specific network TODO consider using here the type
				//cfbt = EcoreUtil.copy((CompositeFBType) fb.getFBType()); 
				cfbt = (CompositeFBType) fb.getType();
				this.fbEditPart = untypedInput.getFbEditPart();
				mapper = new Mapper(cfbt.getName());
			}
		}
	}


	@Override
	public FBNetwork getModel(){
		return cfbt.getFBNetwork();
	}
	
	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		if (cfbt.getFBNetwork() != null) {			
			viewer.setContents(getModel());
		}
	}
	
	@Override
	public void commandStackChanged(EventObject event) {
		// nothing to do as its a viewer!
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		// nothing to do as its a viewer!
	}
	
	private String getNameHierarchy() {
		StringBuilder retVal =  new StringBuilder(fb.getName());
		EObject cont = fb.eContainer().eContainer();
		while(cont instanceof INamedElement){
			retVal.insert(0, ((INamedElement)cont).getName() + "."); //$NON-NLS-1$
			if(cont instanceof Application) {
				break;
			}
			cont = cont.eContainer().eContainer();
		}		
		return retVal.toString();
	}

	public void setState(int step) {
		currentState = step;
		updateCurrentFB();
		updateVars();
	}

	private void updateVars() {
		for (int i = 0; i < counterExample.vars.length; i++) {
			CounterExample.VarQualifier qualifier = counterExample.vars[i];
			if (mapper.var2Interface.containsKey(qualifier)) {
				IInterfaceElement iface = mapper.var2Interface.get(qualifier);
				FB fb = mapper.var2FB.get(qualifier);
				String value = counterExample.data[i][currentState];
				setValue(fb, iface, value);
			}

			if (mapper.isTimeVar(qualifier)) {
				try {
					currentTime = Integer.parseInt(counterExample.data[i][currentState]);
					counterExampleView.setTime(currentTime);
				} catch (NumberFormatException e) { }
			}
		}
	}

	private void updateCurrentFB() {
		boolean found = false;
		for (int i = 0; i < counterExample.vars.length; i++) {
			CounterExample.VarQualifier qualifier = counterExample.vars[i];
			if (mapper.isExecutionVar(qualifier)) {
				if (counterExample.data[i][currentState].equals("TRUE")) {
					FB fb = mapper.getFB(qualifier);
					if (fb != null) {
						highlightFB(fb);
					}
					setCurrentFB(qualifier.FQN);
					found = true;
				}
			}
		}
		if (!found) {
			setCurrentFB("");
			getViewer().deselectAll();
		}
	}

	private void highlightFB(FB fb) {
		EditPartViewer viewer = getViewer();
		viewer.deselectAll();
		if (fb != this.fb) {
			EditPart fbEdit = editPartFactory.mapping.get(fb);
			EditPart network = fbEdit.getParent();
			viewer.select(fbEdit);
			network.refresh();
		}
	}

	private void setValue(FB fb, IInterfaceElement iface, String value) {
		InterfaceEditPart editPart = (InterfaceEditPart) editPartFactory.mapping.get(iface);
		ValueElement valueElement = SimulationManager.getValueElement(editPart, fb);
		valueElement.setCurrentValue(value);
	}

	public void setCounterExample(CounterExample counterExample) {
		this.counterExample = counterExample;
		for (int i = 0; i < counterExample.vars.length; i++) {
			CounterExample.VarQualifier qualifier = counterExample.vars[i];
			if (mapper.isRootFB(qualifier.parts.get(0))) {
				qualifier.mapped = true;
				mapper.findMapping(qualifier, fb);
			}
		}
	}

	private void setCurrentFB(String fqn) {
		currentFB = fqn;
		counterExampleView.setCurrentFB(currentFB);
	}

	public void setCEView(CounterExampleView counterExampleView) {
		this.counterExampleView = counterExampleView;
	}

	static class Mapper {
		public static final String ROOT_FB_SUFFIX = "_inst";
		public static final String ALPHA = "_alpha";
		String rootFBName;
		public Map<CounterExample.VarQualifier, FB> var2FB = new HashMap<>();
		public Map<CounterExample.VarQualifier, IInterfaceElement> var2Interface = new HashMap<>();

		public Mapper(String topFB) {
			rootFBName = topFB;
		}

		public boolean isRootFB(String smvName) {
			return smvName.startsWith(rootFBName + ROOT_FB_SUFFIX);
		}

		public boolean isExecutionVar(CounterExample.VarQualifier smvVar) {
			return smvVar.parts.get(smvVar.parts.size() - 1).endsWith(ALPHA);
		}

		public void findMapping(CounterExample.VarQualifier qualifier, FB root) {
			if (qualifier.parts.size() > 1) {
				String part = trimPart(qualifier.parts.get(1));
				FB fb = ((CompositeFBType)root.getType()).getFBNetwork().getFBNamed(part);
				if (fb == null) fb = root;
				var2FB.put(qualifier, fb);
				if (qualifier.parts.size() > 2) {
					String var = qualifier.parts.get(2);
					IInterfaceElement iface = fb.getInterfaceElement(var);
					if (iface != null) {
						var2Interface.put(qualifier, iface);
					}
				}
			} else if (qualifier.parts.get(0).startsWith(rootFBName)) {
				var2FB.put(qualifier, root);
			}
		}

		public FB getFB(CounterExample.VarQualifier qualifier) {
			return var2FB.get(qualifier);
		}

		private String trimPart(String qualifierPart) {
			return StringUtils.substringBeforeLast(qualifierPart, "_");
		}

		public boolean isTimeVar(CounterExample.VarQualifier qualifier) {
			return qualifier.FQN.equals("TGlobal");
		}
	}


}
