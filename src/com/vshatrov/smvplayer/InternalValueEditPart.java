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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.fordiac.ide.application.editparts.FBEditPart;
import org.eclipse.fordiac.ide.gef.draw2d.SetableAlphaLabel;
import org.eclipse.fordiac.ide.model.libraryElement.INamedElement;
import org.eclipse.swt.widgets.Display;

public class InternalValueEditPart extends AbstractValueBaseEditPart {

	public InternalValueEditPart(FBEditPart editPart) {
		parentPart = editPart;
	}

	@Override
	public INamedElement getINamedElement() {
		return ((FBEditPart)parentPart).getModel();
	}

	@Override
	public ValueElement getModel() {
		return (ValueElement) super.getModel();
	}

	@Override
	protected void createEditPolicies() { }

	@Override
	protected void setBackgroundColor(IFigure l) {
		super.setBackgroundColor(l);
	}

	@Override
	protected IFigure createFigureForModel() {
		SetableAlphaLabel l = new SetableAlphaLabel();
		setBackgroundColor(l);
		l.setOpaque(true);
		l.setLabelAlignment(PositionConstants.CENTER);
		l.setTextAlignment(PositionConstants.CENTER);
		l.setBorder(new MarginBorder(0, 5, 0, 5));
		l.setText("N/A");
		l.setMinimumSize(new Dimension(50, 1));
		l.setAlpha(220);
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
	protected Point calculatePos() {
		if (parentPart != null) {
			Rectangle bounds = ((FBEditPart)parentPart).getFigure().getBounds();
			int y = 0;
			int height = 20;
			height = getFigure().getBounds().height;
			height = Math.max(20, height);
			y = bounds.y + bounds.height + 2;
			int width = getFigure().getBounds().width;

			int x = bounds.x + bounds.width / 2 - width / 2;
			return new Point(x, y);
		}
		return new Point(0, 0);
	}

	public void setValue(String string) {
		if (isActive() && getFigure() != null) {
			((Label) getFigure()).setText(string);
			refreshVisuals();
		}
	}

}
