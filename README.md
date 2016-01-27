This is a mock server for SNMP trap (using JUnit).  The code is released under the [MIT license](http://www.opensource.org/licenses/mit-license.php).


Usage
=====

Define SnmpTrapMockRule.

```java
@Rule
public SnmpTrapMockRule snmpTrapMockRule = new SnmpTrapMockRule();
```

And use rule as follows:

```java
// Sending traps for localhost...

// Wait until receiving traps (count, timeout)
snmpTrapMockRule.waitFor(1, 1000);

// Retrieve traps
assertThat(snmpTrapMockRule.getTrapCount(), is(1));
assertThat(
        snmpTrapMockRule.getVariableBinding(0,
                SnmpConstants.snmpTrapOID.toString()).getVariable().toString(),
        is("1.3.6.1.6.3.1.1.5.4"));
```
