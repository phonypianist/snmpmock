package com.github.phonypianist.snmpmock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Rule;
import org.junit.Test;
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

public class SendTrapTest {
    @Rule
    public SnmpTrapMockRule rule = new SnmpTrapMockRule().waitOnClose(500);

    @Test
    public void send1Trap() {
        sendTrap("127.0.0.1", 162, "1.3.6.1.6.3.1.1.5.4");

        rule.waitFor(1, 1000);

        assertThat(rule.getTrapCount(), is(1));
        assertThat(
                rule.getVariableBinding(0,
                        SnmpConstants.snmpTrapOID.toString()).getVariable().toString(),
                is("1.3.6.1.6.3.1.1.5.4"));
    }

    @Test
    public void send2Traps() {
        sendTrap("127.0.0.1", 162, "1.3.6.1.6.3.1.1.5.4");
        sendTrap("127.0.0.1", 162, "1.3.6.1.6.3.1.1.5.3");

        rule.waitFor(2, 1000);

        assertThat(rule.getTrapCount(), is(2));
        assertThat(
                rule.getVariableBinding(0,
                        SnmpConstants.snmpTrapOID.toString()).getVariable().toString(),
                is("1.3.6.1.6.3.1.1.5.4"));
        assertThat(
                rule.getVariableBinding(1,
                        SnmpConstants.snmpTrapOID.toString()).getVariable().toString(),
                is("1.3.6.1.6.3.1.1.5.3"));
    }

    @Test
    public void send2TrapsWithDelay() {
        sendTrapWithDelay("127.0.0.1", 162, "1.3.6.1.6.3.1.1.5.4", 300);
        sendTrapWithDelay("127.0.0.1", 162, "1.3.6.1.6.3.1.1.5.3", 500);

        rule.waitFor(2, 1000);

        assertThat(rule.getTrapCount(), is(2));
        assertThat(
                rule.getVariableBinding(0,
                        SnmpConstants.snmpTrapOID.toString()).getVariable().toString(),
                is("1.3.6.1.6.3.1.1.5.4"));
        assertThat(
                rule.getVariableBinding(1,
                        SnmpConstants.snmpTrapOID.toString()).getVariable().toString(),
                is("1.3.6.1.6.3.1.1.5.3"));
    }

    @Test(expected = RuntimeException.class)
    public void send2TrapsWithDelayNotReached() {
        Timer timer1 = sendTrapWithDelay("127.0.0.1", 162,
                "1.3.6.1.6.3.1.1.5.4", 500);
        Timer timer2 = sendTrapWithDelay("127.0.0.1", 162,
                "1.3.6.1.6.3.1.1.5.3", 1500);

        try {
            rule.waitFor(2, 1000);
        } finally {
            timer1.cancel();
            timer2.cancel();
        }
    }

    // ---------- Utility Methods ---------- //

    private static void sendTrap(String ipAddress, int port, String oid) {
        PDU pdu = new PDU();
        pdu.setType(PDU.TRAP);
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(0)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(oid)));
        int version = SnmpConstants.version2c;

        CommunityTarget target = new CommunityTarget();
        target.setVersion(version);
        target.setCommunity(new OctetString("public"));

        try {
            TransportMapping transportMapping = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transportMapping);
            String distAddress = ipAddress + "/" + port;
            Address address = GenericAddress.parse(distAddress);
            target.setAddress(address);
            snmp.send(pdu, target);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Timer sendTrapWithDelay(final String ipAddress,
            final int port, final String oid, long delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendTrap(ipAddress, port, oid);
            }
        }, delay);
        return timer;
    }
}
