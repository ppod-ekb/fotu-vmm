package ru.cbr.fotu.vmm;

public abstract class Logger { 
	
	private final String className; 
	
	public Logger(String className) {
		this.className = className; 
	}
	
	abstract public void debug(Object o); 
	
	abstract public void error(String message, Exception e);
	
	protected String getClassName() {
		return className; 
	}

	public static class LoggerImpl extends Logger {
	
		public LoggerImpl(String className) {
			super(className); 
		}
		
		@Override
		public void debug(Object o) {
			System.out.println(">>> " + getClassName() + "  " + o); 
		}

		@Override
		public void error(String message, Exception e) {
			System.err.println(">>> " + getClassName() + "  " + message);
			e.printStackTrace();
		}
	}
}
