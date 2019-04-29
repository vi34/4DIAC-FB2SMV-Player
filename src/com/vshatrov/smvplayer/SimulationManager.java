package com.vshatrov.smvplayer;

import org.eclipse.fordiac.ide.gef.editparts.IChildrenProvider;
import org.eclipse.fordiac.ide.gef.editparts.IEditPartCreator;
import org.eclipse.fordiac.ide.model.libraryElement.FB;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;

import java.util.*;

public class SimulationManager implements IChildrenProvider {

    private static Map<FB, ValueElement> simulatedElements = new HashMap<>();

    public static void addValueElement(ValueElement element) {
        simulatedElements.put(element.fb, element);
    }

    public Collection<ValueElement> getSimulatedElements() {
        return simulatedElements.values();
    }

    @Override
    public List<IEditPartCreator> getChildren(FBNetwork fbNetwork) {
        List<IEditPartCreator> arrayList = new ArrayList<>();

        for (ValueElement element : getSimulatedElements()) {
            if(null != element) {
                if(fbNetwork.getNetworkElements().contains(element.getFb())){
                    arrayList.add(element);
                }
                else if(null != element.getFb().getResource() && (!element.getFb().isResourceFB())){
                    //check if we are in the resource diagram editor for a mapped FB
                    if(element.getFb().getResource().getFBNetwork().equals(fbNetwork)){
                        arrayList.add(element);
                    }
                }
            }

        }
        return arrayList;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}