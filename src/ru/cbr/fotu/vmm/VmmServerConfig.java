package ru.cbr.fotu.vmm;

interface VmmServerConfig {

	String remoteServiceURL();
	
	class LocalhostDockerVmmServerConfig implements VmmServerConfig {
		
		private final static String host = "localhost";
		private final static Integer port = 2809;
		
		@Override
		public String remoteServiceURL() {
			return "corbaloc:iiop:" + host + ":" + port;
		}
		
		public String host() {
			return host;
		}

		public Integer port() {
			return port; 
		}

		@Override
		public String toString() {
			return "LocalhostDockerVmmServerConfig [remoteServiceURL()=" + remoteServiceURL() + "]";
		}

		
		
		
		
	}
}
