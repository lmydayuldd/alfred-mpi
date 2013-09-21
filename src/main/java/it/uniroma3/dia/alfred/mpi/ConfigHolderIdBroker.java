package it.uniroma3.dia.alfred.mpi;

import java.util.UUID;

public class ConfigHolderIdBroker {
	private static final String ID_DIVISOR = "_";

	public static String getId() {
		return getId(UUID.randomUUID().toString(), UUID.randomUUID().toString());
	}
	
	public static String getId(String domainName) {
		return getId(UUID.randomUUID().toString(), domainName);
	}
	
	public static String getId(String name, String domainName) {
		return name + ID_DIVISOR + domainName;
	}
}
