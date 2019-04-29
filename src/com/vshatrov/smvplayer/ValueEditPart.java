/*******************************************************************************
 * Copyright (c) 2015 - 2017 fortiss GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gerd Kainz, Alois Zoitl
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package com.vshatrov.smvplayer;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.fordiac.ide.gef.draw2d.SetableAlphaLabel;
import org.eclipse.fordiac.ide.gef.editparts.InterfaceEditPart;
import org.eclipse.fordiac.ide.model.libraryElement.Event;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.model.monitoring.MonitoringElement;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.swt.widgets.Display;

public class ValueEditPart extends AbstractValueBaseEditPart {

	public ValueEditPart(InterfaceEditPart inteface) {
		parentPart = inteface;
	}

	public boolean isEvent() {
		return getInterfaceElement() instanceof Event;
	}

	public boolean isVariable() {
		return getInterfaceElement() instanceof VarDeclaration;
	}

	@Override
	public ValueElement getModel() {
		return (ValueElement) super.getModel();
	}

	@Override
	protected void createEditPolicies() {
		if(!isEvent()) {
			//only allow direct edit if it is not an event, see Bug 510735 for details.
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPolicy(){
				@Override
				protected Command getDirectEditCommand(DirectEditRequest request) {
					return null;
				}

				@Override
				protected void showCurrentEditValue(DirectEditRequest request) {
				}
			});
		}
	}

	@Override
	protected void setBackgroundColor(IFigure l) {
		super.setBackgroundColor(l);
	}

	@Override
	protected IFigure createFigureForModel() {
		SetableAlphaLabel l = new SetableAlphaLabel();
		setBackgroundColor(l);
		l.setOpaque(true);
		if (isInput()) {
			l.setLabelAlignment(PositionConstants.RIGHT);
			l.setTextAlignment(PositionConstants.RIGHT);
		} else {
			l.setTextAlignment(PositionConstants.LEFT);
			l.setLabelAlignment(PositionConstants.LEFT);
		}
		l.setBorder(new MarginBorder(0, 5, 0, 5));
		l.setText("N/A");
		l.setMinimumSize(new Dimension(50, 1));
		l.setAlpha(190);
		return l;
	}

	@Override
	protected EContentAdapter createContentAdapter() {
		return new EContentAdapter() {
			@Override
			public void notifyChanged(final Notification notification) {
				super.notifyChanged(notification);
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						setValue(getModel().getCurrentValue());
						refreshVisuals();

					}
				});
			}

		};
	}

	@Override
	public void performRequest(final Request request) {
		// REQ_DIRECT_EDIT -> first select 0.4 sec pause -> click -> edit
		// REQ_OPEN -> doubleclick
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT
				|| request.getType() == RequestConstants.REQ_OPEN) {
			if(!isEvent()) {
				performDirectEdit();
			}
		} else {
			super.performRequest(request);
		}
	}

	@Override
	public boolean understandsRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT
				|| request.getType() == RequestConstants.REQ_OPEN) {
			return isVariable(); //Currently only allow direct when we are a variable
		}
		return super.understandsRequest(request);
	}

	public void setValue(String string) {
		if (isActive() && getFigure() != null) {
			((Label) getFigure()).setText(string);
			refreshVisuals();
		}
	}

}
