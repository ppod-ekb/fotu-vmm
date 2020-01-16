package ru.cbr.fotu.vmm;

import java.util.Hashtable;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import com.ibm.websphere.security.auth.callback.WSCallbackHandlerImpl;
import com.ibm.websphere.wim.Service;
import com.ibm.websphere.wim.client.LocalServiceProvider;

public abstract class VmmServiceSetup {
	
	protected final VmmUserCredential credential; 

	private Service service;
	private Subject subject;

	private VmmServiceSetup(VmmUserCredential credential) {
		this.credential = credential;
	}

	abstract Service createService();
	
	protected Subject createSubject() {
		try {
			return login(); 
		} catch (Exception e) {
			throw new RuntimeException("can't login to webshere application server by user: " + credential +", cause: " + e.getMessage(), e);
		}
	}
	
	private Subject login() throws Exception {
        LoginContext loginContext = new LoginContext("WSLogin", new WSCallbackHandlerImpl(credential.userName(), "", credential.password()));
        loginContext.login();
        Subject subject = loginContext.getSubject();
        System.out.println("Subject=" + subject);
        if (subject.getPrincipals().size() >= 1)
            return subject;
        else
            throw new Exception("Login failed.");
    }

	public VmmService setup() {
		service = createService();
		subject = createSubject();

		return new VmmService.VmmServiceImpl(service, subject);
	}


	public static class Local extends VmmServiceSetup {

		public Local(VmmUserCredential credential) {
			super(credential);
		}

		@Override
		protected Service createService() {
			try {
				return new LocalServiceProvider(null);
			} catch (Exception e) {
				throw new RuntimeException("can't get vmm service in local JVM: " + e.getMessage(), e);
			}
		}		  
	}

	public static class Remote extends VmmServiceSetup {
		
		private final VmmServerConfig config;
		private static final String EJB_JNDI_NAME = "ejb/com/ibm/websphere/wim/ejb/WIMServiceHome";
		
		public Remote(VmmUserCredential credential, VmmServerConfig config) {
			super(credential);
			this.config = config; 
		} 

		@Override
		protected Service createService() {
			try {
	            Hashtable<String, String> env = new Hashtable<String, String>();
	            env.put(LocalServiceProvider.PROVIDER_URL, config.remoteServiceURL());
	            env.put(LocalServiceProvider.EJB_JNDI_NAME, EJB_JNDI_NAME);
	            
				return new LocalServiceProvider(env);
			} catch (Exception e) {
				throw new RuntimeException("can't get remote vmm service " + config + ", cause: " + e.getMessage(), e);
			}    
		}
	}
}
