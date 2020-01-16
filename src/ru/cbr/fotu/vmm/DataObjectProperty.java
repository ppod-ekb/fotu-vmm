package ru.cbr.fotu.vmm;

enum DataObjectProperty {
	CN("cn"),
	SN("sn"),
	DISPLAY_NAME("displayName"),
	GIVEN_NAME("givenName"),
	PRINCIPAL_NAME("principalName"),
	MAIL("mail"), 
	INITIALS("initials"),
	UID("uid");
	
	private final String value; 
	
	DataObjectProperty(String value) {
		this.value = value; 
	}
	
	public String value() {
		return value; 
	}
	
	@Override
	public String toString() {
		return value; 
	}
	
	public static String[] allProps() {
		return new String[] {
		 CN.value(),
		 SN.value(),
		 UID.value(),
		 DISPLAY_NAME.value(),
		 GIVEN_NAME.value(),
		 INITIALS.value(),
		 MAIL.value(),
		 PRINCIPAL_NAME.value()
		}; 
	}
}
