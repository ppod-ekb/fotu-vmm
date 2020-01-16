package ru.cbr.fotu.vmm;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.wim.Service;
import com.ibm.websphere.wim.util.SDOUtils;

import commonj.sdo.DataObject;
import ru.cbr.fotu.vmm.VmmQuery.PreparedVmmQuery;
import ru.cbr.fotu.vmm.VmmQuery.QueryType;

public interface VmmService {
	
	DataObject query(String query, String[] params) throws VmmServiceException; 

	DataObject query(String query) throws VmmServiceException; 
	
	@SuppressWarnings("serial")
	class VmmServiceException extends Exception {
		
		public VmmServiceException(String message, Exception cause) {
			super(message, cause); 
		}
	}
	
	class VmmServiceImpl implements VmmService {
		private static final Logger logger = LoggerFactory.getLogger(VmmServiceImpl.class); 
		private final SDOLogger sdoLogger = new SDOLogger(LoggerConfig.get()); 
		
		private final Service service;
		private final Subject subject;
		
		private class VmmQueryExecutor {
			
			private final PreparedVmmQuery preparedQuery; 
			
			private VmmQueryExecutor(PreparedVmmQuery query) {
				super();
				this.preparedQuery = query;
			} 

			private VmmQueryExecutor(VmmQuery query) {
				super();
				this.preparedQuery = query.prepare();
			}

			public DataObject execute() throws VmmServiceException {
				sdoLogger.debug("prepared query: ", preparedQuery.query());
				
				return new PriveledgetActionExecutor(
							new PrivilegedExceptionAction<DataObject>() {
								public DataObject run() throws Exception {
									if (preparedQuery.queryType().equals(QueryType.search)) {
										return service.search(preparedQuery.query());
									} else {
										return service.get(preparedQuery.query());
									}
								}
							}
						).execute(); 
			}
		}

		public VmmServiceImpl(Service service, Subject subject) {
			this.subject = subject;
			this.service = service;
		}
		
		@Override
		public DataObject query(final String query) throws VmmServiceException {
			return new VmmQueryExecutor(
						new VmmQuery(
							new VmmQueryParser.QueryParser(
								new VmmQuery.QueryText(query))))
					.execute();
		}
		
		@Override
		public DataObject query(final String query, final String[] paramValues) throws VmmServiceException {
			return new VmmQueryExecutor(
						new VmmQuery(
							new VmmQueryParser.QueryParser(
								new VmmQuery.QueryText(query, getParams(paramValues)))))
					.execute();
		}
		
		private Map<String,String> getParams(final String[] paramValues) {
			Map<String,String> params = new HashMap<String, String>();
			for(int i = 0; i < paramValues.length; i++) {
				String key = "#"+(i+1)+"#"; 
				String value = paramValues[i]; 
				params.put(key, value); 
			}
			return params; 
		}

		private class PriveledgetActionExecutor {

			private final PrivilegedExceptionAction<DataObject> action;
			private final SDOLogger sdoLogger = new SDOLogger(LoggerConfig.get()); 

			public PriveledgetActionExecutor(PrivilegedExceptionAction<DataObject> action) {
				super();
				this.action = action;
			}

			public DataObject execute() throws VmmServiceException {
				try {
					Object resultAsObject = WSSubject.doAs(subject, action);
					if (resultAsObject != null) {
						DataObject result = (DataObject) resultAsObject;
						
						sdoLogger.debug("search result: ", result);
						
						return result;
					} else {
						logger.debug("data not found");
						return null;
					}
				} catch (PrivilegedActionException e) {
					throw new VmmServiceException("cant't execute priveledget action: " + e.toString(), e);
				}
			}
		}
		
		private static class SDOLogger {
			
			private final LoggerConfig config; 
			
			public SDOLogger(LoggerConfig config) {
				this.config = config; 
			}
			
			public void debug(String message, DataObject dto) {
				if (config.isDebugEnabled())
					SDOUtils.printDataGraph(message, dto);
			}
		}
	}
}
