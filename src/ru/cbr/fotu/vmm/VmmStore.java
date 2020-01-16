package ru.cbr.fotu.vmm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import commonj.sdo.DataObject;
import ru.cbr.fotu.vmm.SdoDocument.Entity;
import ru.cbr.fotu.vmm.SdoDocument.Entity.Group;
import ru.cbr.fotu.vmm.SdoDocument.Entity.Member;
import ru.cbr.fotu.vmm.SdoDocument.Entity.PersonAccount;
import ru.cbr.fotu.vmm.VmmObject.User;
import ru.cbr.fotu.vmm.VmmService.VmmServiceException;

@SuppressWarnings("unused")
public class VmmStore {
	
	private final VmmService vmmService; 
	
	private static final String GET_GROUP_MEMBERS_QUERY = "/entity[type='Group', identifier='#1#']/control[type='PropertyControl', props='cn']/control[type='GroupMemberControl', props='*']";
	
	private static final String FIND_USER_BY_LOGIN_QUERY = "/searchControl[type='PersonAccount', expression=\"uid='#1#'\", props='*']";
	
	private static final String FIND_USER_BY_LOGIN_AND_SEARCH_BASE_QUERY = "/searchControl[type='PersonAccount', searchBase='#2#', expression=\"uid='#1#'\", props='*']";
	
	private static final String FIND_GROUP_BY_CN_QUERY = "/searchControl[type='Group', expression=\"cn='#1#'\", props='cn']";
    
	private static final String FIND_GROUP_BY_CN_AND_SEARCH_BASE_QUERY = "/searchControl[type='Group', searchBase='#2#', expression=\"cn='#1#'\", props='cn']";
	
	private static final String GET_USER_GROUP_MEMBERSHIP_QUERY = "/entity[type='PersonAccount', identifier='#1#']/control[type='GroupMembershipControl', props='Group.*']";
	
	private static final String GET_GROUP_BY_DN_QUERY = "/entity[type='Group', identifier='#1#']/control[type='PropertyControl', props='Group.*']";
	
	private static final String GET_USER_BY_DN_QUERY = "/entity[type='PersonAccount', identifier='#1#']/control[type='PropertyControl', props='*']";
	
	private static final String GET_USER_WITH_GROUP_MEMBERSHIP_BY_USER_DN_QUERY = "/entity[type='PersonAccount', identifier='#1#']/control[type='PropertyControl', props='*']/control[type='GroupMembershipControl', props='cn']";
	
    public VmmStore(VmmService vmmService) {
    	this.vmmService = vmmService; 
    }

    public Optional<VmmObject.User> findUserByLogin(final String login) {
    	return new VmmStoreQuery<Optional<VmmObject.User>>() {
			@Override
			protected DataObject executeOrError() throws VmmServiceException {
				return vmmService.query(FIND_USER_BY_LOGIN_QUERY, new String[] {login}); 
			}

			@Override
			protected Optional<User> extract(DataObject dto) {
				return new SingleResult.VmmUserSingleResult(dto).get(); 
			}	
    	}.execute();
    }
    
    public Optional<VmmObject.User> findUserByLogin(final String login, final String searchBase) {
    	return new VmmStoreQuery<Optional<VmmObject.User>>() {
    		 @Override
    		 public DataObject executeOrError() throws VmmServiceException {
		    	return vmmService.query(FIND_USER_BY_LOGIN_AND_SEARCH_BASE_QUERY, new String[] {login, searchBase});
    		 }

			@Override
			protected Optional<User> extract(DataObject dto) {
				return new SingleResult.VmmUserSingleResult(dto).get(); 
			}

    	}.execute(); 
    }
    
    public Optional<VmmObject.Group> findGroupByCn(final String cn, final String searchBase) {
    	return new VmmStoreQuery<Optional<VmmObject.Group>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(FIND_GROUP_BY_CN_AND_SEARCH_BASE_QUERY, new String[] {cn, searchBase}); 
    		}

			@Override
			protected Optional<ru.cbr.fotu.vmm.VmmObject.Group> extract(DataObject dto) {
				return new SingleResult.VmmGroupSingleResult(dto).get(); 
			}
    	}.execute();
    } 
    
    public Optional<VmmObject.Group> findGroupByCn(final String cn) {
    	return new VmmStoreQuery<Optional<VmmObject.Group>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(FIND_GROUP_BY_CN_QUERY, new String[] {cn});  
    		}

			@Override
			protected Optional<ru.cbr.fotu.vmm.VmmObject.Group> extract(DataObject dto) {
				return new SingleResult.VmmGroupSingleResult(dto).get(); 
			}
    	}.execute();
    }
    
    public List<VmmObject.Group> getUserGroupMembership(final String userDn) {
    	return new VmmStoreQuery<List<VmmObject.Group>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(GET_USER_GROUP_MEMBERSHIP_QUERY, new String[] {userDn});
    		}

			@Override
			protected List<VmmObject.Group> extract(DataObject dto) {
				return new MultipleResult.VmmGroupMultipleResult(dto).get();
			}
    	}.execute();  
    }
    
    public List<VmmObject.Group> getUserGroupMembership(final VmmObject.User user) {
    	return getUserGroupMembership(user.getDn());
    }
    
    public Optional<VmmObject.Group> getGroupByDn(final String groupDn) {
    	return new VmmStoreQuery<Optional<VmmObject.Group>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			 return vmmService.query(GET_GROUP_BY_DN_QUERY, new String[] {groupDn});
    		}

			@Override
			protected Optional<VmmObject.Group> extract(DataObject dto) {
				return new SingleResult.VmmGroupSingleResult(dto).get();
			}
    	}.execute();
    }
    
    public Optional<VmmObject.User> getUserByDn(final String userDn) {
    	return new VmmStoreQuery<Optional<VmmObject.User>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(GET_USER_BY_DN_QUERY, new String[] {userDn});
    		}

			@Override
			protected Optional<User> extract(DataObject dto) {
				return new SingleResult.VmmUserSingleResult(dto).get();
			}
    	}.execute();
    }
    
    public Optional<VmmObject.User> getUserWithGroupMembership(final User user) {
    	return getUserWithGroupMembership(user.getDn());
    }
    
    public Optional<VmmObject.User> getUserWithGroupMembership(final String userDn) {
    	return new VmmStoreQuery<Optional<VmmObject.User>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(GET_USER_WITH_GROUP_MEMBERSHIP_BY_USER_DN_QUERY, new String[] {userDn});
    		}

			@Override
			protected Optional<User> extract(DataObject dto) {
				return new SingleResult.VmmUserSingleResult(dto).get();
			}
    	}.execute();
    }
    
    public List<VmmObject.User> getGroupUsers(final VmmObject.Group group) {
    	return getGroupUsers(group.getDn()); 
    }
    
    public List<VmmObject.User> getGroupUsers(final String groupDn) {
    	return new VmmStoreQuery<List<VmmObject.User>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(GET_GROUP_MEMBERS_QUERY, new String[] {groupDn});
    		}

			@Override
			protected List<VmmObject.User> extract(DataObject dto) {
				return new MultipleResult.VmmUserOfMemberMultipleResult(dto).get();
			}
    	}.execute(); 
    }
    
    public List<VmmObject.Group> getGroupGroups(final VmmObject.Group group) {
    	return getGroupGroups(group.getDn()); 
    }
    
    public List<VmmObject.Group> getGroupGroups(final String groupDn) {
    	return new VmmStoreQuery<List<VmmObject.Group>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(GET_GROUP_MEMBERS_QUERY, new String[] {groupDn});
    		}

			@Override
			protected List<VmmObject.Group> extract(DataObject dto) {
				return new MultipleResult.VmmGroupOfMemberMultipleResult(dto).get();
			}
    	}.execute(); 
    }

    public List<VmmObject.Member> getGroupMembers(final VmmObject.Group group) {
    	return getGroupMembers(group.getDn()); 
    }
    
    public List<VmmObject.Member> getGroupMembers(final String groupDn) {
    	return new VmmStoreQuery<List<VmmObject.Member>>() {
    		@Override
    		public DataObject executeOrError() throws VmmServiceException {
    			return vmmService.query(GET_GROUP_MEMBERS_QUERY, new String[] {groupDn});
    		}

			@Override
			protected List<VmmObject.Member> extract(DataObject dto) {
				return new MultipleResult.VmmMemberMultipleResult(dto).get();
			}
    	}.execute(); 
    }
    
    
    
    
    private static abstract class MultipleResult<T> {
		
		private final SdoDocument.Root root; 
		
		public MultipleResult(DataObject dto) {
			this.root = new SdoDocument.Root(dto); 
		}

		protected abstract List<T> extract(final Entity e); 
		
		@SuppressWarnings("unchecked")
		public List<T> get() {
			if (!root.isEmpty()) {
				for (Entity e : root.entities()) {
					return extract(e); 
				}
			}
			return Collections.EMPTY_LIST; 
		}
		
		static class VmmUserMultipleResult extends MultipleResult<VmmObject.User> {

			public VmmUserMultipleResult(DataObject dto) {
				super(dto);
			}

			@Override
			protected List<VmmObject.User> extract(final Entity e) {
				List<VmmObject.User> results = new ArrayList<VmmObject.User>();
				for (PersonAccount pa : e.personAccounts()) {
					results.add(pa.extractUser()); 
				}
				return results; 
			}
		}
		
		static class VmmGroupMultipleResult extends MultipleResult<VmmObject.Group> {
			
			public VmmGroupMultipleResult(DataObject dto) {
				super(dto);
			}

			@Override
			protected List<VmmObject.Group> extract(final Entity e) {
				List<VmmObject.Group> results = new ArrayList<VmmObject.Group>();
				for (Group gr : e.groups()) {
					results.add(gr.extractGroup()); 
				}
				return results; 
			}
		}
		
		static class VmmMemberMultipleResult extends MultipleResult<VmmObject.Member> {
			
			public VmmMemberMultipleResult(DataObject dto) {
				super(dto);
			}

			@Override
			protected List<VmmObject.Member> extract(final Entity e) {
				List<VmmObject.Member> results = new ArrayList<VmmObject.Member>();
				for (Member mem : e.members()) {			
					results.add(mem.extractMember()); 
				}
				return results; 
			}
		}
		
		static class VmmGroupOfMemberMultipleResult extends MultipleResult<VmmObject.Group> {

			public VmmGroupOfMemberMultipleResult(DataObject dto) {
				super(dto);
			}

			@Override
			protected List<VmmObject.Group> extract(Entity e) {
				List<VmmObject.Group> results = new ArrayList<VmmObject.Group>();
				for (Member mem : e.members()) {
					if (mem.isGroup()) results.add(mem.extractGroup()); 
				}
				return results; 
			}
		}
		
		static class VmmUserOfMemberMultipleResult extends MultipleResult<VmmObject.User> {

			public VmmUserOfMemberMultipleResult(DataObject dto) {
				super(dto);
			}

			@Override
			protected List<VmmObject.User> extract(Entity e) {
				List<VmmObject.User> results = new ArrayList<VmmObject.User>();
				for (Member mem : e.members()) {
					if (mem.isPersonAccount()) results.add(mem.extractUser()); 
				}
				return results; 
			}
		}
		
	}
	
	private static abstract class SingleResult<T> {
		
		private final SdoDocument.Root root; 
		
		protected abstract Optional<T> extract(final Entity e);
		
		public SingleResult(DataObject dto) {
			this.root = new SdoDocument.Root(dto); 	
		}
		
		public Optional<T> get() {
			if (!root.isEmpty()) {
				for (Entity e : root.entities()) {
		    		return extract(e);
		    	}
			}
	    	return Optional.empty(); 
		}
		
		static class VmmUserSingleResult extends SingleResult<VmmObject.User> {

			public VmmUserSingleResult(DataObject dto) {
				super(dto);
			}

			@Override
			protected Optional<VmmObject.User> extract(final Entity e) {
	    		return Optional.of(e.personAccount().extractUser()); 
			}

		}
		
		static class VmmGroupSingleResult extends SingleResult<VmmObject.Group> {

			public VmmGroupSingleResult(DataObject dto) {
				super(dto);
			}

			@Override
			protected Optional<VmmObject.Group> extract(final Entity e) {
	    		return Optional.of(e.group().extractGroup()); 
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class VmmStoreException extends RuntimeException {
		
		public VmmStoreException(String message, Throwable cause) {
			super(message, cause);
		}
	}
	
	private abstract class VmmStoreQuery<T> {
		
		abstract protected DataObject executeOrError() throws VmmServiceException;
		
		abstract protected T extract(DataObject dto); 
		
		public T execute() {
			try {
				return extract(executeOrError());
			} catch (VmmServiceException e) {
				if (isEntityNotFoundException(e)) {
					return extract(null); 
				} else {
					throw new VmmStoreException("Exception while execute vmm store query: " + e.toString(), e); 
				}		
			}
		}
		
		private boolean isEntityNotFoundException(VmmServiceException e) {
			if (e != null 
					&& e.getCause() != null 
					&& e.getCause().getCause() != null 
					&& e.getCause().getCause().getClass() != null)
				return e.getCause()
							.getCause()
								.getClass().getCanonicalName().equals("com.ibm.websphere.wim.exception.EntityNotFoundException");
			else 
				return false; 
		}
	}
}
