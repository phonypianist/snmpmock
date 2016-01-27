package com.github.phonypianist.snmpmock;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class TrapCommandResponder implements CommandResponder {
    private List<CommandResponderEvent> received;

    public TrapCommandResponder() {
        received = new ArrayList<CommandResponderEvent>();
    }

    public synchronized void processPdu(CommandResponderEvent event) {
        received.add(event);
        notifyAll();
    }

    public int getTrapCount() {
        return received.size();
    }

    public CommandResponderEvent getTrap(int index) {
        return received.get(index);
    }
    
    public VariableBinding getVariableBinding(int index, String oid) {
        OID oidObj = new OID(oid);
        Vector<?> bindings = received.get(index).getPDU().getVariableBindings();
        for (Object binding : bindings) {
            VariableBinding variableBinding = (VariableBinding)binding;
            if (oidObj.equals(variableBinding.getOid())) {
                return variableBinding;
            }
        }
        return null;
    }

    public List<CommandResponderEvent> getTraps() {
        return received;
    }
}
