package ru.cbr.fotu.vmm;

import java.util.ArrayList;
import java.util.List;

public abstract class VmmObject {
	
	private final Identifier identifier; 
	private final String dn;
	private final String cn; 
	
	private VmmObject(VmmObjectBuilder b) {
		identifier = b.identifier; 
		dn = b.dn; 
		cn = b.cn; 
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}

	public String getDn() {
		return dn;
	}

	public String getCn() {
		return cn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dn == null) ? 0 : dn.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VmmObject other = (VmmObject) obj;
		if (dn == null) {
			if (other.dn != null)
				return false;
		} else if (!dn.equals(other.dn))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VmmObject [identifier=" + identifier + ", dn=" + dn + ", cn=" + cn + "]";
	}

	static abstract class VmmObjectBuilder {
		
		private Identifier identifier; 
		private String dn;
		private String cn;
		
		public VmmObjectBuilder dn(String value) {
			this.dn = value; 
			return this; 
		}
		
		public VmmObjectBuilder cn(String value) {
			this.cn = value; 
			return this; 
		}

		public VmmObjectBuilder identifier(Identifier value) {
			this.identifier = value; 
			return this; 
		}
		
		public abstract VmmObject build();
	}
	
	static public class User extends VmmObject {
		
		private User(UserBuilder b) {
			super(b); 
			sn = b.sn; 
			mail = b.mail; 
			uid = b.uid; 
			principalName = b.principalName; 
			displayName = b.displayName; 
			givenName = b.givenName;
			initials = b.initials; 
			name = b.name; 
			groups = b.groups; 				
		}

		private final String sn; 
		private final String mail; 
		private final String uid; 
		private final String principalName; 
		private final String displayName; 
		private final String givenName;
		private final String initials; 
		private final String name;
		private final List<String> groups; 
		
		@Override
		public String toString() {
			return "VmmUser [sn=" + sn + ", mail=" + mail + ", uid=" + uid + ", principalName=" + principalName
					+ ", displayName=" + displayName + ", givenName=" + givenName + ", initials=" + initials + ", name="
					+ name + ", groups=" + groups + ", toString()=" + super.toString() + "]";
		}

		public String getSn() {
			return sn;
		}

		public String getMail() {
			return mail;
		}


		public String getUid() {
			return uid;
		}


		public String getPrincipalName() {
			return principalName;
		}


		public String getDisplayName() {
			return displayName;
		}


		public String getGivenName() {
			return givenName;
		}


		public String getInitials() {
			return initials;
		}


		public String getName() {
			return name;
		}


		public List<String> getGroups() {
			return groups;
		}

		static class UserBuilder extends VmmObjectBuilder { 
			
			private String sn; 
			private String mail; 
			private String uid; 
			private String principalName; 
			private String displayName; 
			private String givenName;
			private String initials; 
			private String name;
			private List<String> groups = new ArrayList<String>(); 
			
			public User build() {
				return new User(this); 
			}
			
			public UserBuilder sn(String value) {
				this.sn = value;
				return this; 
			}
			public UserBuilder mail(String value) {
				this.mail = value;
				return this; 
			}
			public UserBuilder uid(String value) {
				this.uid = value;
				return this; 
			}
			public UserBuilder principalName(String value) {
				this.principalName = value;
				return this; 
			}
			public UserBuilder displayName(String value) {
				this.displayName = value;
				return this; 
			}
			public UserBuilder givenName(String value) {
				this.givenName = value;
				return this; 
			}
			public UserBuilder initials(String value) {
				this.initials = value;
				return this; 
			}
			public UserBuilder name(String value) {
				this.name = value;
				return this; 
			}
			
			public UserBuilder addCnToUserGroups(String cn) {
				this.groups.add(cn); 
				return this;
			}
			
			public UserBuilder groups(List<String> value) {
				this.groups.addAll(value);
				return this; 
			}			
		}		
	}
	
	static public class Member extends VmmObject {
		
		private Member(MemberBuilder b) {
			super(b);
		}

		@Override
		public String toString() {
			return "VmmMember [getIdentifier()=" + getIdentifier() + ", getDn()=" + getDn() + ", getCn()=" + getCn()
					+ "]";
		}

		static class MemberBuilder extends VmmObjectBuilder {

			@Override
			public Member build() {
				return new Member(this);
			}
		}
	}
	
	static public class Group extends VmmObject {
		
		private Group(GroupBuilder b) {
			super(b);
		}

		@Override
		public String toString() {
			return "VmmGroup [getIdentifier()=" + getIdentifier() + ", getDn()=" + getDn() + ", getCn()=" + getCn()
					+ "]";
		}

		static class GroupBuilder extends VmmObjectBuilder {
			
			public Group build() {
				return new Group(this);
			}
		}
	}
	
	static public class Identifier {

		private final String externalName; 
		private final String repositoryId; 
		private final String uniqueId; 
		private final String uniqueName;
		
		private Identifier(IdentifierBuilder b) {
			this.externalName = b.externalName; 
			this.repositoryId = b.repositoryId; 
			this.uniqueId = b.uniqueId;
			this.uniqueName = b.uniqueName;
		}
		
		public String getExternalName() {
			return externalName;
		}

		public String getRepositoryId() {
			return repositoryId;
		}
		
		public String getUniqueId() {
			return uniqueId;
		}

		public String getUniqueName() {
			return uniqueName;
		}

		@Override
		public String toString() {
			return "Identifier [externalName=" + externalName + ", repositoryId=" + repositoryId + ", uniqueId="
					+ uniqueId + ", uniqueName=" + uniqueName + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((externalName == null) ? 0 : externalName.hashCode());
			result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
			result = prime * result + ((uniqueName == null) ? 0 : uniqueName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Identifier other = (Identifier) obj;
			if (externalName == null) {
				if (other.externalName != null)
					return false;
			} else if (!externalName.equals(other.externalName))
				return false;
			if (uniqueId == null) {
				if (other.uniqueId != null)
					return false;
			} else if (!uniqueId.equals(other.uniqueId))
				return false;
			if (uniqueName == null) {
				if (other.uniqueName != null)
					return false;
			} else if (!uniqueName.equals(other.uniqueName))
				return false;
			return true;
		}



		static class IdentifierBuilder {
			private String externalName; 
			private String repositoryId; 
			private String uniqueId; 
			private String uniqueName;
			
			public IdentifierBuilder externalName(String value) {
				this.externalName = value; 
				return this; 
			}
			
			public IdentifierBuilder repositoryId(String value) {
				this.repositoryId = value; 
				return this; 
			}
			
			public IdentifierBuilder uniqueId(String value) {
				this.uniqueId = value; 
				return this; 
			}
			
			public IdentifierBuilder uniqueName(String value) {
				this.uniqueName = value; 
				return this; 
			}
			
			public Identifier buid() {
				return new Identifier(this);
			}
		}
	}

	
}
