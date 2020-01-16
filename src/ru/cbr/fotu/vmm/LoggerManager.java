package ru.cbr.fotu.vmm;

class LoggerManager {
	
	private static String logger; 
	
	static { 
		LoggerManager.registerLogger(LoggerConfig.get().getLoggerClassName());
	}
	

	public static void registerLogger(String logger) {
		LoggerManager.logger = logger; 
	}
	
	public static String getLoggerClass() {
		if (logger != null) 
			return logger;
		else 
			return Logger.LoggerImpl.class.getName();
	}
	
}
