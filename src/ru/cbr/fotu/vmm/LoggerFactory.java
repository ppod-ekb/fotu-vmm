package ru.cbr.fotu.vmm;

import java.lang.reflect.Constructor;

class LoggerFactory {

	public static Logger getLogger(Class<?> clazz) {
		
		if (!LoggerConfig.get().isDebugEnabled()) {
			return new Logger(null) {

				@Override
				public void debug(Object o) {
				}

				@Override
				public void error(String message, Exception e) {
				}
				
			};
		}
		
		try {
			Constructor<?> c = Class.forName(LoggerManager.getLoggerClass()).getConstructor(String.class);
			Logger logger = (Logger) c.newInstance(clazz.getCanonicalName());
			return logger; 
		} catch (Exception e) {
			throw new RuntimeException("can't create Logger: " + e.toString(), e); 
		}
	}
}
