package com.github.phonypianist.snmpmock.rule;

import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.snmp4j.CommandResponderEvent;

import com.github.phonypianist.snmpmock.SnmpTrapMockReceiver;
import com.github.phonypianist.snmpmock.TrapCommandResponder;

public class SnmpTrapMockRule implements MethodRule, TestRule {
    private SnmpTrapMockReceiver receiver;

    private TrapCommandResponder responder;

    public SnmpTrapMockRule() {
        receiver = new SnmpTrapMockReceiver();
        responder = new TrapCommandResponder();
    }
    
    public List<CommandResponderEvent> getReceived() {
        return responder.getReceived();
    }

    public Statement apply(Statement base, Description description) {
        return apply(base, null, null);
    }

    public Statement apply(final Statement base, FrameworkMethod method,
            Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                start();
                try {
                    before();
                    base.evaluate();
                } finally {
                    after();
                    stop();
                }
            }

        };
    }

    protected void start() throws Exception {
        receiver.start();
    }

    protected void stop() throws Exception {
        receiver.stop();
    }

    protected void before() {
        receiver.addListener(responder);
    }

    protected void after() {
        // Do nothing.
    }
}
