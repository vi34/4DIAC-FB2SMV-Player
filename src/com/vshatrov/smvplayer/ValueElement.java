package com.vshatrov.smvplayer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.fordiac.ide.gef.editparts.IEditPartCreator;
import org.eclipse.fordiac.ide.gef.editparts.InterfaceEditPart;
import org.eclipse.fordiac.ide.model.libraryElement.FB;
import org.eclipse.fordiac.ide.model.monitoring.MonitoringPackage;
import org.eclipse.gef.EditPart;

public class ValueElement extends EObjectImpl implements IEditPartCreator {

    public static final int VALUE_CHANGE_FEATURE_ID = 0;
    InterfaceEditPart parentPart;
    FB fb;
    String value;

    public ValueElement(InterfaceEditPart inteface, FB fb) {
        parentPart = inteface;
        this.fb = fb;
    }

    public FB getFb() {
        return fb;
    }

    @Override
    public EditPart createEditPart() {
        return new ValueEditPart(parentPart);
    }

    public String getCurrentValue() {
        return value;
    }

    public void setCurrentValue(String newValue) {
        String oldValue = value;
        value = newValue;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, VALUE_CHANGE_FEATURE_ID, oldValue, newValue));
        }
    }
}
