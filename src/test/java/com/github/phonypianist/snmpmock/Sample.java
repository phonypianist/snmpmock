package com.github.phonypianist.snmpmock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Sample {
    @Rule
    public SnmpTrapMockRule rule = new SnmpTrapMockRule();

    @Test
    public void startStop() throws IOException, InterruptedException {
        sendTrap("127.0.0.1", 162);
        Thread.sleep(1000);
        List<CommandResponderEvent> received = rule.getTraps();
        assertThat(received.size(), is(1));
    }

    private void sendTrap(String ipAddress, int port) throws IOException {
        PDU pdu = new PDU();
        pdu.setType(PDU.TRAP);
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime,
                new TimeTicks(0)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID,
                new OID("1.3.6.1.6.3.1.1.5.4")));
        int version = SnmpConstants.version2c;

        CommunityTarget target = new CommunityTarget();
        target.setVersion(version);
        target.setCommunity(new OctetString("public"));
        TransportMapping transportMapping = new DefaultUdpTransportMapping();

        Snmp snmp = new Snmp(transportMapping);
        String distAddress = ipAddress + "/" + port;
        Address address = GenericAddress.parse(distAddress);
        target.setAddress(address);
        snmp.send(pdu, target);
    }
}
