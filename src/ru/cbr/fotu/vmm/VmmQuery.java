package ru.cbr.fotu.vmm;

import static com.ibm.websphere.wim.SchemaConstants.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.websphere.wim.SchemaConstants;
import com.ibm.websphere.wim.util.SDOHelper;

import commonj.sdo.DataObject;
import ru.cbr.fotu.vmm.SdoDocument.Entity.Group;
import ru.cbr.fotu.vmm.SdoDocument.Entity.Member;
import ru.cbr.fotu.vmm.SdoDocument.Entity.PersonAccount;

class VmmQuery {

	enum QueryType {
		search,
		get; 
	}
	
	enum ElementType {
		entity,
		control,
		searchControl; 
		
		public static ElementType getElementType(Element el) {
			return ElementType.valueOf(el.elementType()); 
		}
		
		public static boolean isSearchControlElement(Element el) {
			return searchControl == getElementType(el);
		}
		
		public static boolean isControlElement(Element el) {
			return control == getElementType(el); 
		}
		
		public static boolean isEntityElement(Element el) {
			return entity == getElementType(el); 
		}
		
	}
	
	static class PreparedVmmQuery {
		
		private final QueryType queryType; 
		private final DataObject query;
		
		private PreparedVmmQuery(QueryType queryType, DataObject query) {
			super();
			this.queryType = queryType;
			this.query = query;
		}
		
		public QueryType queryType() {
			return queryType;
		}
		public DataObject query() {
			return query;
		} 
	}
	
	@SuppressWarnings("serial")
	static class VmmPrepareQueryException extends RuntimeException {
		VmmPrepareQueryException(String message) {
			super(message);
		}

		VmmPrepareQueryException(String message, Exception cause) {
			super(message, cause);
		}
	}

	private final VmmQueryParser parser;
	private final StringBuilder constructedQuery = new StringBuilder();

	public VmmQuery(VmmQueryParser parser) {
		this.parser = parser;
	}

	@Override
	public String toString() {
		return constructedQuery.toString();
	}

	public PreparedVmmQuery prepare() {
		try {
			List<Element> elements = parser.parse().and().getElements(); 
			return new PreparedVmmQuery(getQueryType(elements), createDataObject(elements));
		} catch (Exception e) {
			throw new VmmPrepareQueryException("Exception while prepare vmm query: " + e.getMessage(), e);
		}
	}

	private DataObject createDataObject(List<Element> elements) {
		DataObject root = DataObjectFactory.createRootDataObject();
		DataObjectFactory factory = new DataObjectFactory(root);
		for (Element el : elements) {
			factory.create(el);
			constructedQuery.append(el.toString()).append("\n");
		}
		return root; 
	}
	
	private QueryType getQueryType(List<Element> elements) {
		for (Element el : elements) {
			if (ElementType.isSearchControlElement(el)) {
				return QueryType.search; 
			}
		}
		return QueryType.get;
	}

	static class QueryText {

		private final String query;
		private final Map<String, String> params = new HashMap<String, String>();

		public QueryText(String query, Map<String, String> params) {
			super();
			this.query = query;
			this.params.putAll(params);
		}

		public QueryText(String query) {
			super();
			this.query = query;
		}

		public String string() {
			String query = this.query;
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				query = query.replaceFirst(key, params.get(key));
			}

			return query;
		}

		@Override
		public String toString() {
			return "QueryText [query=" + query + "]";
		}
	}
	
	private static class DataObjectFactory {

		private final DataObject root;

		private DataObjectFactory(DataObject root) {
			super();
			this.root = root;
		}

		public DataObject create(Element el) {
			try {
				return createOrError(el);
			} catch (Exception e) {
				throw new VmmQuery.VmmPrepareQueryException(
						"Exception while create entity element " + el.toString() + ": " + e.getMessage(), e);
			}
		}

		private DataObject createOrError(Element el) {
			if (ElementType.isEntityElement(el)) {
				return createEntityDataObject(el);
			} else if (ElementType.isControlElement(el)) {
				return createControlDataObject(el);
			} else if (ElementType.isSearchControlElement(el)) {
				return createSearchControlDataObject(el);
			} else {
				throw new RuntimeException("can't create unknown element: " + el.toString());
			}
		}

		private static DataObject createRootDataObject() {
			try {
				return SDOHelper.createRootDataObject();
			} catch (Exception e) {
				throw new VmmQuery.VmmPrepareQueryException("can't create root element: " + e.getMessage(), e);
			}
		} 

		private DataObject createEntityDataObject(Element el) {
			DataObject entity = SDOHelper.createEntityDataObject(root, null, el.entityType());
			if (el.hasIdentifier()) {
				DataObject dto = entity.createDataObject(DO_IDENTIFIER); 
				dto.set(PROP_UNIQUE_NAME, el.identifier());
			} 
			
			return entity;
		}

		@SuppressWarnings("unchecked")
		private DataObject createControlDataObject(Element el) {
			DataObject control = SDOHelper.createControlDataObject(root, null, el.entityType());
			if (el.hasProps()) {
				control.getList(PROP_PROPERTIES).addAll(el.props());
			}
			
			return control;
		}
		
		@SuppressWarnings("unchecked")
		private DataObject createSearchControlDataObject(Element el) {
			DataObject control = SDOHelper.createControlDataObject(root, null, DO_SEARCH_CONTROL);
			if (el.hasExpression()) {
				control.setString(PROP_SEARCH_EXPRESSION, el.expression()); 
			}
			if (el.hasSearchBase()) {
				control.set(SchemaConstants.PROP_SEARCH_BASES, Arrays.asList(el.searchBase()));
			}
			if (el.hasProps()) {
				control.getList(PROP_PROPERTIES).addAll(el.props());
			}
			
			return control;
		}
	}

	static class Element {

		private final String elementType;
		private final Map<String, String> params;
		private final String[] props;

		public Element() {
			this.elementType = "root";
			this.params = new HashMap<String, String>();
			this.props = new String[0];
		}

		public Element(String elementType, Map<String, String> params, String[] props) {
			this.elementType = elementType;
			this.params = params;
			this.props = props;
		}

		public String elementType() {
			return elementType;
		}

		public String entityType() {
			return params.get("type");
		}

		public boolean hasIdentifier() {
			return identifier() != null;
		}

		public String identifier() {
			return params.get("identifier");
		}
		
		public boolean hasExpression() {
			return params.get("expression") != null; 
		}
		
		public String expression() {
			return "@xsi:type='"+entityType()+"' and "+params.get("expression"); 
		}
		
		public boolean hasSearchBase() {
			return searchBase() != null;
		}
		
		public String searchBase() {
			return params.get("searchBase"); 
		}

		public boolean hasProps() {
			return props.length > 0;
		}

		public Collection<String> props() {
			if (isAllPropsSelected()) {
				return getAllProps(); 
			} else {
				return Arrays.asList(props);
			}
		}
		
		private Collection<String> getAllProps() {
			String classOfAllProps = getClassOfAllProps(); 
			if (classOfAllProps.equalsIgnoreCase(PersonAccount.TYPE_NAME)) {
				return Arrays.asList(DataObjectProperty.allProps()); 
			} else if (classOfAllProps.equals("*")) {
				return Arrays.asList(DataObjectProperty.allProps());
			} else {
				return Arrays.asList(DataObjectProperty.CN.value()); 
			}
		}
		
		private String getClassOfAllProps() {
			for (String prop: props) {
				if (prop.equalsIgnoreCase(PersonAccount.TYPE_NAME+".*")) {
					return PersonAccount.TYPE_NAME; 
				}
				if (prop.equalsIgnoreCase(Group.TYPE_NAME+".*")) {
					return Group.TYPE_NAME; 
				}
				if (prop.equalsIgnoreCase(Member.TYPE_NAME+".*")) {
					return Member.TYPE_NAME; 
				}
				if (prop.equalsIgnoreCase("*")) {
					return "*"; 
				}
			}
			
			return null;
		}
		
		private boolean isAllPropsSelected() {
			return getClassOfAllProps() != null; 
		}

		@Override
		public String toString() {
			return "Element [elementType()=" + elementType() + ", entityType()=" + entityType() + ", identifier()="
					+ identifier() + ", expression()=" + expression() + ", props()=" + props() + "]";
		}

		

		
	}
}
