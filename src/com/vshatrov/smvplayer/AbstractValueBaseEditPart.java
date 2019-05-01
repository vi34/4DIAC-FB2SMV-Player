/*******************************************************************************
 * Copyright (c) 2012 - 2018 Profactor GmbH, fortiss GmbH, Johannes Kepler 
 * 							 University
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gerhard Ebenhofer, Alois Zoitl, Gerd Kainz, Monika Wenger
 *     - initial API and implementation and/or initial documentation
 *   Alois Zoitl - Harmonized deployment and monitoring
 *******************************************************************************/
package com.vshatrov.smvplayer;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.fordiac.ide.application.SpecificLayerEditPart;
import org.eclipse.fordiac.ide.deployment.monitoringbase.MonitoringBaseElement;
import org.eclipse.fordiac.ide.gef.editparts.AbstractViewEditPart;
import org.eclipse.fordiac.ide.gef.editparts.InterfaceEditPart;
import org.eclipse.fordiac.ide.gef.editparts.ZoomScalableFreeformRootEditPart;
import org.eclipse.fordiac.ide.model.libraryElement.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValueBaseEditPart extends AbstractViewEditPart implements SpecificLayerEditPart{

	protected EditPart parentPart;

	private IPropertyChangeListener listener;

	@Override
	public void activate() {
		super.activate();
		/*for (Object object : getViewer().getEditPartRegistry().keySet()) {
			if (object instanceof IInterfaceElement) {
				IInterfaceElement interfaceElement = (IInterfaceElement) object;
				if (interfaceElement.equals(getInterfaceElement())) {
					EditPart part = (EditPart) getViewer().getEditPartRegistry().get(object);
					if (part instanceof InterfaceEditPart) {
						parentPart = (InterfaceEditPart) part;
						IFigure f = parentPart.getFigure();
						f.addAncestorListener(new AncestorListener() {

							@Override
							public void ancestorRemoved(IFigure ancestor) {
								// nothing to do
							}

							@Override
							public void ancestorMoved(IFigure ancestor) {
								refreshVisuals();
							}

							@Override
							public void ancestorAdded(IFigure ancestor) {
								// nothing to do
							}
						});
					}
				}
				else if (interfaceElement instanceof AdapterDeclaration) {
					IInterfaceElement subInterfaceElement = null;
					InterfaceList interfaceList = ((AdapterType)((AdapterDeclaration)interfaceElement).getType()).getInterfaceList();
					ArrayList<IInterfaceElement> list = new ArrayList<IInterfaceElement>();
					list.addAll(interfaceList.getEventInputs());
					list.addAll(interfaceList.getEventOutputs());
					list.addAll(interfaceList.getInputVars());
					list.addAll(interfaceList.getOutputVars());
					for (IInterfaceElement element : list) {
						if (element.equals(getInterfaceElement()) && interfaceElement.eContainer().eContainer() == getModel().getFb()) {
							subInterfaceElement = element;
							break;
						}
					}

					*//*if (subInterfaceElement != null) {
						Object subObject = null;
						for (Object obj : getViewer().getEditPartRegistry().values()) {
							if (obj instanceof MonitoringAdapterEditPart) {
								MonitoringAdapterEditPart part = (MonitoringAdapterEditPart)obj;
								if (part.getModel().getPort().getInterfaceElement() == interfaceElement) {
									for (Object subView : part.getModelChildren()) {
										if (((IInterfaceElement)subView).getName().equals(subInterfaceElement.getName())) {
											subObject = subView;
											break;
										}
									}
									if (subObject != null) {
										break;
									}
								}
							}
						}

						if (subObject != null) {
							EditPart part = (EditPart) getViewer().getEditPartRegistry().get(subObject);
							if (part instanceof InterfaceEditPart) {
								parentPart = (InterfaceEditPart) part;
								IFigure f = parentPart.getFigure();
								f.addAncestorListener(new AncestorListener() {

									@Override
									public void ancestorRemoved(IFigure ancestor) {
										// nothing to do
									}

									@Override
									public void ancestorMoved(IFigure ancestor) {
										// calculatePos()
										refreshVisuals();

									}

									@Override
									public void ancestorAdded(IFigure ancestor) {
										// nothing to do
									}
								});
							}
						}
					}*//*
				}
			}
		}*/
		refreshVisuals();
	}

	@Override
	public Label getNameLabel() {
		return (Label)getFigure();
	}

	@Override
	public String getSpecificLayer() {
		return ZoomScalableFreeformRootEditPart.TOPLAYER;
	}
	
	@Override
	public ValueElement getModel() {
		return (ValueElement) super.getModel();
	}

	@Override
	public boolean understandsRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_MOVE) {
			return false;
		}
		return super.understandsRequest(request);
	}
	
	protected abstract Point calculatePos();
	
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		refreshPosition();
		setBackgroundColor(getFigure());
		getFigure().setEnabled(true);
	}

	protected void refreshPosition() {
		if (getParent() != null) {
			Rectangle bounds = null;
			Point p = calculatePos();
			int width = getFigure().getPreferredSize().width;
			width = Math.max(40, width);
			bounds = new Rectangle(p.x, p.y, width, -1);
			((GraphicalEditPart) getParent()).setLayoutConstraint(this,
					getFigure(), bounds);
			toFront();
		}
	}

	private void toFront() {
		IFigure figure = getFigure();
		while (!(figure instanceof LayeredPane)) {
			figure = figure.getParent();
		}
		LayeredPane layeredPane = (LayeredPane) figure;
		String primary_layer_key = "Primary Layer";
		String connection_layer_key = "Connection Layer";
		Layer primary_layer = layeredPane.getLayer(primary_layer_key);
		layeredPane.removeLayer(primary_layer_key);
		layeredPane.addLayerAfter(primary_layer, primary_layer_key, connection_layer_key);
		layeredPane.repaint();
	}

	protected void setBackgroundColor(IFigure l) {
		l.setBackgroundColor(new Color(null, 37, 237, 182));
	}
	
	@Override
	protected void backgroundColorChanged(IFigure figure) {
		setBackgroundColor(figure);
	}


	@Override
	protected IPropertyChangeListener getPreferenceChangeListener() {
		return null;
	}

}
