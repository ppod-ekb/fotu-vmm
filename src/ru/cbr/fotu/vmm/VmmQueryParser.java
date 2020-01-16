package ru.cbr.fotu.vmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.cbr.fotu.vmm.VmmQuery.Element;
import ru.cbr.fotu.vmm.VmmQuery.QueryText;

interface VmmQueryParser {

	List<Element> getElements();

	VmmQueryParser parse() throws VmmQueryParser.ParseException;
	
	VmmQueryParser and(); 

	@SuppressWarnings("serial")
	class ParseException extends Exception {

		ParseException(String message) {
			super(message);
		}

		ParseException(String message, Exception cause) {
			super(message, cause);
		}
	}

	static class QueryParser implements VmmQueryParser {

		private static final Logger logger = LoggerFactory.getLogger(QueryParser.class); 
		private final QueryText query;
		private List<Element> elements = new ArrayList<Element>();

		QueryParser(QueryText query) {
			super();
			this.query = query;
		}

		@Override
		public List<Element> getElements() {
			return elements;
		}

		@Override
		public QueryParser parse() throws VmmQueryParser.ParseException {
			logger.debug("parse: " + query);
			String[] elements = query.string().split("/");
			for (String el : elements) {
				if (!el.isEmpty())
					addElement(el);
			}
			return this; 
		}

		private void addElement(String elStr) throws VmmQueryParser.ParseException {
			try {
				logger.debug("create inner element");
				QueryParser.ElementParser p = new QueryParser.ElementParser(elStr);
				Element el = p.parse();
				elements.add(el);
			} catch (Exception e) {
				throw new VmmQueryParser.ParseException(
						"Exception while create element [" + elStr + "]: " + e.getMessage(), e);
			}
		}

		@Override
		public String toString() {
			return "QueryParser [query=" + query + ", elements=" + elements + "]";
		}

		private static class ElementParser {
			
			private static final Logger logger = LoggerFactory.getLogger(ElementParser.class); 
			
			private final String el;

			private String elementType;
			private Map<String, String> params;
			private String[] props;

			private ElementParser(String el) {
				super();
				this.el = el;
			}

			public Element parse() {
				logger.debug("parse element: " + el);
				int start = el.indexOf("[");
				int stop = el.lastIndexOf("]");

				createType(el.substring(0, start));
				createParamMap(el.substring(start + 1, stop));
				createProps(params.get("props"));

				return new Element(elementType, params, props);
			}

			private void createType(String typeStr) {
				this.elementType = typeStr;
			}

			private void createParamMap(String paramStr) {
				params = new HashMap<String, String>();
				if (paramStr != null) {
					String[] params = paramStr.split(", ");
					for (String param : params) {
						addParam(param);
					}
				}
			}

			private void addParam(String keyValueStr) {
				params.put(getKey(keyValueStr), getValue(keyValueStr)); 
			}
			
			private String getValue(String keyValueStr) {
				int start = keyValueStr.indexOf("="); 
				int stop = keyValueStr.length(); 
				return prepareValue(keyValueStr.substring(start+1, stop)); 
			}

			private String getKey(String keyValueStr) {
				int start = 0; 
				int stop = keyValueStr.indexOf("="); 
				return prepareValue(keyValueStr.substring(start, stop)); 
			}
			
			private String prepareValue(String valueStr) {
				return valueStr.trim().replaceFirst("^[\"']", "").replaceFirst("[\"']$", ""); 
			}

			private void createProps(String propsStr) {
				if (propsStr != null) {
					props = propsStr.split(",");
				} else {
					props = new String[0]; 
				}
			}
		}

		@Override
		public VmmQueryParser and() {
			// TODO Auto-generated method stub
			return this;
		}
	}
}
