package ru.cbr.fotu.vmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.websphere.wim.SchemaConstants;

import commonj.sdo.DataObject;
import ru.cbr.fotu.vmm.SdoDocument.Entity.Group;
import ru.cbr.fotu.vmm.SdoDocument.Entity.PersonAccount;
import ru.cbr.fotu.vmm.VmmObject.User;

abstract class SdoDocument {
	
	private enum DocumentType {
		Group {
		},
		PersonAccount {
		},
		Member {
		};
		
		static DocumentType type(DataObject o) {
			return DocumentType.valueOf(o.getType().getName());
		}
	}

	final DataObject sdo;

	public SdoDocument(DataObject sdo) {
		this.sdo = sdo;
	}
	
	public boolean isEmpty() {
		return sdo == null;
	}

	public DataObject sdo() {
		return sdo;
	}

	static private abstract class SdoNode extends SdoDocument {

		private final DataObjectPropExtractor propExtractor;

		public SdoNode(DataObject sdo) {
			super(sdo);
			propExtractor = new DataObjectPropExtractor(sdo);
		}
		
		public Identifier identifier() {
			return new Identifier(sdo.getDataObject(SchemaConstants.DO_IDENTIFIER));
		}

		public String getProp(DataObjectProperty prop) {
			return propExtractor.get(prop);
		}

		@Override
		public String toString() {
			return "SdoNode [identifier()=" + identifier() + "]";
		}
	}

	static private abstract class IdentifierNode extends SdoDocument {

		private IdentifierNode(DataObject sdo) {
			super(sdo);
		}

	}

	static private class Identifier extends IdentifierNode {

		Identifier(DataObject sdo) {
			super(sdo);
		}

		public String uniqueName() {
			return sdo.getString(SchemaConstants.PROP_UNIQUE_NAME);
		}
		
		public String uniqueId() {
			return sdo.getString(SchemaConstants.PROP_UNIQUE_ID); 
		}
		
		public String externalName() {
			return sdo.getString(SchemaConstants.PROP_EXTERNAL_NAME); 
		}
		
		public String repositoyId() {
			return sdo.getString(SchemaConstants.PROP_REPOSITORY_ID); 
		}

		@Override
		public String toString() {
			return "Identifier [uniqueName()=" + uniqueName() + ", uniqueId()=" + uniqueId() + ", externalName()="
					+ externalName() + "]";
		}
	}

	static class Root extends SdoDocument { 
		
		public Root(DataObject root) {
			super(root);
		}

		@SuppressWarnings("unchecked")
		private List<DataObject> getListOfEntities() {
			return sdo.getList(SchemaConstants.DO_ENTITIES);
		}

		public List<Entity> entities() {
			List<Entity> results = new ArrayList<Entity>();
			for (DataObject ent : getListOfEntities()) {
				results.add(new Entity(ent));
			}
			return results;
		}
	}

	static class Entity extends SdoNode {

		Entity(DataObject ent) {
			super(ent);
		}

		public List<Member> members() {
			List<Member> results = new ArrayList<Member>();
			for (DataObject mem : getListOfMembers()) {
				results.add(new Member(mem));
			}
			return results;
		}

		public List<Group> groups() {
			List<Group> results = new ArrayList<Group>();
			for (DataObject mem : getListOfGroups()) {
				results.add(new Group(mem));
			}
			return results;
		}

		public List<PersonAccount> personAccounts() {
			List<PersonAccount> results = new ArrayList<PersonAccount>();
			for (DataObject mem : getListOfPersonAccounts()) {
				results.add(new PersonAccount(mem));
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		private List<DataObject> getListOfGroups() {
		    return sdo.getList(SchemaConstants.DO_GROUPS);
		}

		@SuppressWarnings("unchecked")
		private List<DataObject> getListOfPersonAccounts() {
			return sdo.getList(SchemaConstants.DO_PERSON_ACCOUNT);
		}

		@SuppressWarnings("unchecked")
		private List<DataObject> getListOfMembers() {
			return sdo.getList(SchemaConstants.DO_MEMBERS);
		}

		public PersonAccount personAccount() {
			return new PersonAccount(sdo);
		}

		public Group group() {
			return new Group(sdo);
		}
		
		@Override
		public String toString() {
			return "Entity [toString()=" + super.toString() + "]";
		}

		static class Member extends SdoNode {
			
			public static final String TYPE_NAME = "Member"; 
			private final Extractor<VmmObject.Member> memberExtractor;
			private final Extractor<VmmObject.Group> groupExtrator;
			private final Extractor<VmmObject.User> userExtractor; 

			Member(DataObject ent) {
				super(ent);
				this.memberExtractor = Extractor.VmmMemberExtractor.create(this); 
				this.groupExtrator = Extractor.VmmGroupExtractor.create(new Group(ent));
				this.userExtractor = Extractor.VmmUserExtractor.create(new PersonAccount(ent));
			}
	
			public boolean isGroup() {
				return DocumentType.Group.equals(DocumentType.type(sdo)); 
			}
			
			public boolean isPersonAccount() {
				return DocumentType.PersonAccount.equals(DocumentType.type(sdo)); 
			}
			
			public VmmObject.Member extractMember() {
				return memberExtractor.get(); 
			}
			
			public VmmObject.Group extractGroup() {
				return groupExtrator.get(); 
			}
			
			public VmmObject.User extractUser() {
				return userExtractor.get(); 
			}

			@Override
			public String toString() {
				return "Member [toString()=" + super.toString() + "]";
			}
		}

		static class Group extends SdoNode {
			
			public static final String TYPE_NAME = "Group"; 
			final Extractor<VmmObject.Group> extractor;

			public Group(DataObject sdo) {
				super(sdo);
				this.extractor = Extractor.VmmGroupExtractor.create(this);
			}

			public VmmObject.Group extractGroup() {
				return extractor.get();
			}

			@Override
			public String toString() {
				return "Group [toString()=" + super.toString() + "]";
			}
		}

		static class PersonAccount extends SdoNode {
			
			public static final String TYPE_NAME = "PersonAccount"; 

			final Extractor<VmmObject.User> extractor;

			public PersonAccount(DataObject sdo) {
				super(sdo);
				this.extractor = Extractor.VmmUserExtractor.create(this);
			}

			public VmmObject.User extractUser() {
				return extractor.get();
			}
			
			public List<Group> groups() {
				List<Group> results = new ArrayList<Group>();
				for (DataObject mem : getListOfGroups()) {
					results.add(new Group(mem));
				}
				return results;
			}
			
			@SuppressWarnings("unchecked")
			private List<DataObject> getListOfGroups() {
				return sdo.getList(SchemaConstants.DO_GROUPS);
			}

			@Override
			public String toString() {
				return "PersonAccount [toString()=" + super.toString() + "]";
			}
		}
	}
	
		
	static private abstract class Extractor<T extends VmmObject> {
	
		private final SdoDocument.SdoNode sdoNode;
		private final VmmObject.Identifier.IdentifierBuilder iBuilder;
		private final VmmObject.VmmObjectBuilder oBuilder;
		
		private Extractor(SdoNode sdoNode, VmmObject.VmmObjectBuilder oBuilder) {
			this.sdoNode = sdoNode; 
			this.oBuilder = oBuilder;
			this.iBuilder = new VmmObject.Identifier.IdentifierBuilder();
		}
		
		protected abstract T build();
		
		public T get() {
			oBuilder.identifier(buildIdentifier()); 
			oBuilder.cn(sdoNode.getProp(DataObjectProperty.CN));
			oBuilder.dn(sdoNode.identifier().uniqueName());
			
			return build();
		}
		
		private VmmObject.Identifier buildIdentifier() {
			Identifier identifier = sdoNode.identifier(); 
			iBuilder.externalName(identifier.externalName()); 
			iBuilder.repositoryId(identifier.repositoyId()); 
			iBuilder.uniqueId(identifier.uniqueId()); 
			iBuilder.uniqueName(identifier.uniqueName()); 
			return iBuilder.buid(); 
		}
		
		static private class VmmMemberExtractor extends Extractor<VmmObject.Member> {
			
			private final VmmObject.Member.MemberBuilder b;
			
			private static Extractor<VmmObject.Member> create(SdoDocument.Root.Entity.Member mbr) {
				return new VmmMemberExtractor(mbr, new VmmObject.Member.MemberBuilder()); 
			}
			
			private VmmMemberExtractor(SdoDocument.Root.Entity.Member mbr, 
									   VmmObject.Member.MemberBuilder builder) {
				super(mbr, builder);
				this.b = builder;
			}

			@Override
			protected ru.cbr.fotu.vmm.VmmObject.Member build() {
				return b.build();
			}
		}
		
		static private class VmmGroupExtractor extends Extractor<VmmObject.Group> {

			private final VmmObject.Group.GroupBuilder b;
			
			private static Extractor<VmmObject.Group> create(SdoDocument.Root.Entity.Group gr) {
				return new VmmGroupExtractor(gr, new VmmObject.Group.GroupBuilder()); 
			}

			private VmmGroupExtractor(Group group, VmmObject.Group.GroupBuilder builder) {
				super(group, builder);
				this.b = builder;
			}

			@Override
			protected ru.cbr.fotu.vmm.VmmObject.Group build() {
				return b.build();
			}
		}
		
		static private class VmmUserExtractor extends Extractor<VmmObject.User> {

			private final SdoDocument.Root.Entity.PersonAccount pa;
			private final VmmObject.User.UserBuilder b;
			
			private static Extractor<VmmObject.User> create(SdoDocument.Root.Entity.PersonAccount pa) {
				return new VmmUserExtractor(pa, new VmmObject.User.UserBuilder()); 
			}

			public VmmUserExtractor(PersonAccount pa, VmmObject.User.UserBuilder builder) {
				super(pa, builder);
				this.pa = pa;
				this.b = builder;
			}

			@Override
			protected User build() {
				b.mail(pa.getProp(DataObjectProperty.MAIL))
					.displayName(pa.getProp(DataObjectProperty.DISPLAY_NAME))
					.sn(pa.getProp(DataObjectProperty.SN))
					.givenName(pa.getProp(DataObjectProperty.GIVEN_NAME))
					.initials(pa.getProp(DataObjectProperty.INITIALS))
					.principalName(pa.getProp(DataObjectProperty.PRINCIPAL_NAME))
					.uid(pa.getProp(DataObjectProperty.UID));

				for (Group g : pa.groups()) {
					b.addCnToUserGroups(g.extractGroup().toString());
				}
		
				return b.build();
			}
		}
	}
		
	static private class DataObjectPropExtractor {

		private static final Logger logger = LoggerFactory.getLogger(DataObjectPropExtractor.class); 
		private final DataObject ent;

		public DataObjectPropExtractor(DataObject ent) {
			super();
			this.ent = ent;
		}

		public String get(DataObjectProperty prop) {
			return getProp(prop);
		}

		private String getPropOrError(DataObjectProperty prop) {
			return PropertyExtractorFactory.getExtractor(prop).get(ent, prop);
		}

		private String getProp(DataObjectProperty prop) {
			try {
				return getPropOrError(prop);
			} catch (Exception e) {
				logger.debug("Error while get property " + prop + ": " + e.toString());
				return "#ERROR#";
			}
		}
		
		private static class PropertyExtractorFactory {
			
			private static final Map<DataObjectProperty, PropertyExtractor> extractors = new HashMap<DataObjectProperty, PropertyExtractor>();
			private static final PropertyExtractor.DefaultPropertyExtractor defaultExtractor = new PropertyExtractor.DefaultPropertyExtractor();
			
			static {
				extractors.put(DataObjectProperty.DISPLAY_NAME, new PropertyExtractor.EDataTypeEListPropertyExtractor());
				extractors.put(DataObjectProperty.GIVEN_NAME, new PropertyExtractor.EDataTypeEListPropertyExtractor());
				extractors.put(DataObjectProperty.INITIALS, new PropertyExtractor.EDataTypeEListPropertyExtractor());
				extractors.put(DataObjectProperty.CN, new PropertyExtractor.StringPropertyExtractor());
				extractors.put(DataObjectProperty.MAIL, new PropertyExtractor.StringPropertyExtractor());
				extractors.put(DataObjectProperty.PRINCIPAL_NAME, new PropertyExtractor.StringPropertyExtractor());
				extractors.put(DataObjectProperty.SN, new PropertyExtractor.StringPropertyExtractor());
				extractors.put(DataObjectProperty.UID, new PropertyExtractor.StringPropertyExtractor());
			}
			
			public static PropertyExtractor getExtractor(DataObjectProperty prop) {
				if (extractors.containsKey(prop)) {
					return extractors.get(prop); 
				} else {
					return defaultExtractor;
				}
			}
		}
		
		static private abstract class PropertyExtractor {
			
			String get(DataObject dto, DataObjectProperty prop) {
				Object obj = extract(dto, prop); 
				if (obj != null) 
					return obj.toString(); 
				else 
					return ""; 
			}
			
		    abstract protected Object extract(DataObject dto, DataObjectProperty prop);
			
			static private class DefaultPropertyExtractor extends PropertyExtractor {
				
				@Override
				protected String extract(DataObject dto, DataObjectProperty prop) {
					return dto.get(prop.toString()).toString();
				}
			}
			
			static private class StringPropertyExtractor extends PropertyExtractor {

				@Override
				protected String extract(DataObject dto, DataObjectProperty prop) {
					return dto.getString(prop.toString());
				}
			}
			
			@SuppressWarnings("unchecked")
			static private class EDataTypeEListPropertyExtractor extends PropertyExtractor {

				@Override
				protected String extract(DataObject dto, DataObjectProperty prop) {
					List<String> results = dto.getList(prop.toString());
					String concat = ""; 
					for (int i = 0; i < results.size(); i++) {
						String s = results.get(i); 
						concat += s;
						if (i > 0 && i < results.size()) {
							concat += ","; 
						}
					}
					return concat;
				}
			}
		}
	}
}
