package com.vshatrov.smvplayer;

import com.vshatrov.smvplayer.read.CounterExample;
import org.apache.commons.lang.StringUtils;
import org.eclipse.fordiac.ide.model.libraryElement.CompositeFBType;
import org.eclipse.fordiac.ide.model.libraryElement.FB;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;

import java.util.HashMap;
import java.util.Map;

class Mapper {
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
            String firstLevelName = qualifier.parts.get(1);
            String part = trimPart(firstLevelName);
            FB fb = ((CompositeFBType)root.getType()).getFBNetwork().getFBNamed(part);
            if (fb == null) fb = root;
            var2FB.put(qualifier, fb);
            if (qualifier.parts.size() > 2 || isEvent(firstLevelName, fb)) {
                String var = isEvent(firstLevelName, fb)
                        ? StringUtils.substringAfter(firstLevelName, eventPrefix(fb))
                        : qualifier.parts.get(2);

                IInterfaceElement iface = fb.getInterfaceElement(var);
                if (iface != null) {
                    var2Interface.put(qualifier, iface);
                }
            }
        } else if (qualifier.parts.get(0).startsWith(rootFBName)) {
            var2FB.put(qualifier, root);
        }
    }

    private String eventPrefix(FB fb) {
        return fb.getName() + "_";
    }

    private boolean isEvent(String name, FB fb) {
        return name.startsWith(eventPrefix(fb));
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
