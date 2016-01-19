package com.github.phonypianist.snmpmock;

import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;

public class TrapCommandResponder implements CommandResponder {
    private List<CommandResponderEvent> received;
    
    public TrapCommandResponder() {
        received = new ArrayList<CommandResponderEvent>();
    }

    public void processPdu(CommandResponderEvent event) {
        received.add(event);
    }
    
    public List<CommandResponderEvent> getReceived() {
        return received;
    }
}
