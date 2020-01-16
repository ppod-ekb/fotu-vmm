package ru.cbr.fotu.vmm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

final public class LoggerConfig {
	
	private final boolean debugEnabled; 
	private final String loggerClassName; 
	
	private static LoggerConfig instance; 
	
	static LoggerConfig get() {
		if (instance == null) {
			LoggerConfig.LoggerConfigBuilder c = new LoggerConfig.LoggerConfigBuilder();
			c.debugEnabled(false);
			c.loggerClassName(Logger.LoggerImpl.class.getName());
			new Loader.PrpgrammaticLoader(c).load();
		}
		return instance; 
	}

	private LoggerConfig(LoggerConfigBuilder b) {
		debugEnabled = b.debugEnabled; 
		loggerClassName = b.loggerClassName; 
	}
	
	public static class LoggerConfigBuilder {
		private boolean debugEnabled; 
		private String loggerClassName; 
		
		public LoggerConfig buid() {
			return new LoggerConfig(this); 
		}

		public void debugEnabled(boolean debugEnabled) {
			this.debugEnabled = debugEnabled;
		}

		public void loggerClassName(String loggerClassName) {
			this.loggerClassName = loggerClassName;
		}

	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}


	public String getLoggerClassName() {
		return loggerClassName;
	}

	public interface Loader {
		
		public static class PrpgrammaticLoader implements Loader {
			
			private final LoggerConfig.LoggerConfigBuilder builder; 
	
			PrpgrammaticLoader(LoggerConfig.LoggerConfigBuilder builder) {
				this.builder = builder; 
			}
			
			private LoggerConfig createLoggerConfigIntance() {
				return builder.buid(); 
			}
			
			public void load() {
				instance = createLoggerConfigIntance(); 
			}
		}
	
		public static class PropertiesLoaderImpl implements Loader {
			
			private Properties props;  
			
			private final String loggerConfigFilePath; 
			
			private static final String DEBUG_ENABLED_PROP = "debugEnabled"; 
			private static final String LOGGER_CLASS_NAME_PROP = "loggerClassName"; 
			
			public PropertiesLoaderImpl(String loggerConfigFilePath) {
				this.loggerConfigFilePath = loggerConfigFilePath; 
			}
			
			private LoggerConfig createLoggerConfigIntance() {
				LoggerConfig.LoggerConfigBuilder c = new LoggerConfig.LoggerConfigBuilder(); 
				c.debugEnabled(getBoolOrDefault(DEBUG_ENABLED_PROP, false));
				c.loggerClassName(getStringOrDefault(LOGGER_CLASS_NAME_PROP, Logger.LoggerImpl.class.getName()));
				return c.buid(); 
			}
			
			public void load() {
				loadProperties(); 
				instance = createLoggerConfigIntance(); 
			}
			
			private void loadProperties() {
				props = new Properties();
				try {
					File f = new File(loggerConfigFilePath); 
					if (f.exists())
						props.load(new FileInputStream(f));
					else 
						throw new RuntimeException("can't load logger configuraton: " + loggerConfigFilePath); 
				} catch (FileNotFoundException e) {
					throw new RuntimeException("error while load logger configuraton: " + e.toString(), e); 
				} catch (IOException e) {
					throw new RuntimeException("error while load logger configuraton: " + e.toString(), e); 
				}
			}
			
			private boolean getBoolOrDefault(String prop, boolean def) {
				if (props.containsKey(prop)) {
					return Boolean.valueOf(props.get(prop).toString()); 
				}
				return def; 
			}
			
			private String getStringOrDefault(String prop, String def) {
				if (props.containsKey(prop)) {
					return props.get(prop).toString(); 
				}
				return def; 
			}
		}
	}
}
