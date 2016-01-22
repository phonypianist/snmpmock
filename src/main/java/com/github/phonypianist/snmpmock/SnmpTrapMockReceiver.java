package com.github.phonypianist.snmpmock;

import java.io.IOException;

import org.snmp4j.CommandResponder;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpTrapMockReceiver {
    private int port;

    private Snmp snmp;

    public SnmpTrapMockReceiver() {
        this(162);
    }

    public SnmpTrapMockReceiver(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        Address listenAddress = GenericAddress.parse("udp:0.0.0.0/" + port);
        TransportMapping transport = new DefaultUdpTransportMapping(
                (UdpAddress) listenAddress);

        snmp = new Snmp(new MessageDispatcherImpl(), transport);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
        USM usm = new USM(SecurityProtocols.getInstance(),
                new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        snmp.listen();
    }

    public void stop() throws IOException {
        if (snmp != null) {
            snmp.close();
        }
    }

    public void addListener(CommandResponder listener) {
        if (snmp != null) {
            snmp.addCommandResponder(listener);
        }
    }

    public void removeListener(CommandResponder listener) {
        if (snmp != null) {
            snmp.removeCommandResponder(listener);
        }
    }
}
