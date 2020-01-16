package ru.cbr.fotu.vmm;

public interface VmmUserCredential {

	public String userName(); 
	public String password(); 
	
	class SvcVmmReader implements VmmUserCredential {
		
		// WAS Administrator Id
		private static final String userId = System.getProperty("com.ibm.CORBA.loginUserid", "svcVmmReader");

		// WAS Administrator password
		private static final String password = System.getProperty("com.ibm.CORBA.loginPassword", "password");

		@Override
		public String userName() {
			return userId;
		}

		@Override
		public String password() {
			return password;
		}

		@Override
		public String toString() {
			return "SvcVmmReader [userName()=" + userName() + ", password()="+password()+"]";
		}
	}
}
