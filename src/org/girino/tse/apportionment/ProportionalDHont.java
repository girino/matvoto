package org.girino.tse.apportionment;

public class ProportionalDHont extends Proportional {
	public ProportionalDHont() {
		super(new DHontMethod());
	}
	public ProportionalDHont(boolean allowsCoalition) {
		super(new DHontMethod(), allowsCoalition);
	}
}
