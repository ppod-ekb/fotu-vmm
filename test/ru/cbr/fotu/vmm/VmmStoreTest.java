package ru.cbr.fotu.vmm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ibm.ws.sip.stack.transaction.logging.LoggerMgr;

import commonj.sdo.DataObject;
import ru.cbr.fotu.vmm.LoggerConfig.Loader;
import ru.cbr.fotu.vmm.SdoDocument.Entity;
import ru.cbr.fotu.vmm.SdoDocument.Entity.Group;
import ru.cbr.fotu.vmm.SdoDocument.Entity.Member;
import ru.cbr.fotu.vmm.VmmService.VmmServiceException;

/*
 * 
 * production usage sample: 
 * 
	VmmStore vmmClient = new VmmStore(
								new VmmServiceSetup.Local(
									  new VmmUserCredential.SvcVmmReader()).setup());
		
		
		
		Optional<VmmObject.User> user = vmmClient.findUserByLogin("danj");
		if (user.isPresent()) {
			vmmClient.getUserGroupMembership(user.get()); 
		}
 * 
 * */



@SuppressWarnings("unused")
class VmmStoreTest {
	
	static {
		String configPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "vmmLogger.properties";
		new LoggerConfig.Loader.PropertiesLoaderImpl(configPath).load();
	}
	
	private static VmmService getService() {
		return new VmmServiceSetup.Remote(
				  new VmmUserCredential.SvcVmmReader(), new VmmServerConfig.LocalhostDockerVmmServerConfig()).setup();
	}
	
	@RunWith(JUnit4.class)
	public static class VmmStoreUserTest {
		
		private static final String login = "danj";
		private static final String nonExistinglogin = "danj1";
		private static final String cn = "Dan Jump"; 
		private static final String searchBase = "ou=users,dc=kav,dc=ru"; 
		private static final String wrongSearchBase = "ou=groups,dc=kav,dc=ru"; 
		private static final String dn = "cn=Dan Jump,ou=users,dc=kav,dc=ru";
		private static final String nonExistingDn = "cn=DanJump,ou=users,dc=kav,dc=ru";
		private static final String uid = "danj";
		
		private static final String principalName = "danj"; 
		private static final String sn = "Jump";
		private static final String displayName = "Dan Jump"; 
		private static final String mail = "danj@contoso.com"; 
		private static final String givenName = "Dan";
		
		private final VmmStore store;
		
		public VmmStoreUserTest() {
			this.store = new VmmStore(VmmStoreTest.getService());
		}
		
		public void init() {
			
		}
		
		@Test
		public void getUserWithGroupMembershipByUserDnTest() {
			Optional<VmmObject.User> u = store.getUserWithGroupMembership(dn);
			assertTrue(u.isPresent());
			propsCheck(u.get()); 
			assertTrue(u.get().getGroups().isEmpty() == false);
			assertTrue(u.get().getGroups().size() == 1);
		}
		
		@Test 
		public void getUserByNonExistingDnTest() {
			Optional<VmmObject.User> u = store.getUserByDn(nonExistingDn); 
			assertTrue(!u.isPresent());
		}
		
		@Test 
		public void getUserByExistingDnTest() {
			Optional<VmmObject.User> u = store.getUserByDn(dn); 
			assertTrue(u.isPresent());
			propsCheck(u.get()); 
		}
		
		@Test
		public void findExistingUserByLoginTest() { 
			Optional<VmmObject.User> u = store.findUserByLogin(login);
			assertTrue(u.isPresent());
			propsCheck(u.get()); 
		}
		
		@Test
		public void findExistingUserByLoginAndSearchBaseTest() { 
			Optional<VmmObject.User> u = store.findUserByLogin(login, searchBase);
			assertTrue(u.isPresent());
			propsCheck(u.get()); 
		}
		
		@Test
		public void findNonExistingUserByLoginTest() { 
			Optional<VmmObject.User> u = store.findUserByLogin(nonExistinglogin);
			assertTrue(!u.isPresent());
		}
		
		@Test
		public void findExistingUserByLoginWithWrongSearchBaseTest() { 
			Optional<VmmObject.User> u = store.findUserByLogin(login, wrongSearchBase);
			assertTrue(!u.isPresent());
		}
		
		private void propsCheck(VmmObject.User u) {
			assertEquals(cn, u.getCn());
			assertEquals(uid, u.getUid());
			assertEquals(sn, u.getSn());
			assertEquals(mail, u.getMail());
			assertEquals(displayName, u.getDisplayName());
			assertEquals(givenName, u.getGivenName());
			assertEquals(principalName, u.getPrincipalName());
			
			// dirty hook
			assertEquals(dn.toUpperCase(), u.getDn().toUpperCase());	
		}
	}
	
	@RunWith(JUnit4.class)
	public static class VmmStoreGroupTest {
		
		private static final String cn = "Executive"; 
		private static final String searchBase = "ou=groups,dc=kav,dc=ru"; 
		private static final String dn = "cn=Executive,ou=groups,dc=kav,dc=ru";
		private static final String nonExistingDn = "cn=Executive1,ou=groups,dc=kav,dc=ru";
		private static final String uid = "CN=EXECUTIVE,OU=GROUPS,DC=KAV,DC=RU"; 
		
		private final VmmStore store;
		
		public VmmStoreGroupTest() {
			this.store = new VmmStore(VmmStoreTest.getService());
		}
		
		@Test
		public void getGroupMembersByVmmGroupTest() {
			Optional<VmmObject.Group> gr = store.findGroupByCn(cn);
			assertTrue(gr.isPresent());
			List<VmmObject.Member> members = store.getGroupMembers(gr.get()); 
			assertTrue(members.size() == 8);
		}
		
		@Test
		public void getGroupMembersByExistingGroupDnTest() {
			List<VmmObject.Member> members = store.getGroupMembers(dn); 
			assertTrue(members.size() == 8);			
		}
		
		@Test 
		public void getGroupUsersByExistingGroupDnTest() {
			List<VmmObject.User> users = store.getGroupUsers(dn); 
			assertTrue(users.size() == 7);		
		}
		
		@Test
		public void getGroupUsersByVmmGroupTest() {
			Optional<VmmObject.Group> gr = store.findGroupByCn(cn);
			assertTrue(gr.isPresent());
			List<VmmObject.User> users = store.getGroupUsers(gr.get()); 
			assertTrue(users.size() == 7);
		}
		
		@Test 
		public void getGroupGroupsByExistingGroupDnTest() {
			List<VmmObject.Group> groups = store.getGroupGroups(dn); 
			assertTrue(groups.size() == 1);		
		}
		
		@Test
		public void getGroupGroupsByVmmGroupTest() {
			Optional<VmmObject.Group> gr = store.findGroupByCn(cn);
			assertTrue(gr.isPresent());
			List<VmmObject.Group> groups = store.getGroupGroups(gr.get()); 
			assertTrue(groups.size() == 1);		
		}
		
		
		@Test
		public void getGroupByExistingDnTest() {
			Optional<VmmObject.Group> gr = store.getGroupByDn(dn); 
			assertTrue(gr.isPresent());
			propsCheck(gr.get());
		}
		
		@Test
		public void getGroupByNonExistingDnTest() {
			Optional<VmmObject.Group> gr = store.getGroupByDn(nonExistingDn); 
			assertTrue(!gr.isPresent()); 
		}
		
		@Test
		public void getGroupMembersByNonExistingGroupDnTest() {
			List<VmmObject.Member> members = store.getGroupMembers(nonExistingDn); 
			assertTrue(members.isEmpty());			
		}
		
		@Test
		public void getGroupUsersByNonExistingGroupDnTest() {
			List<VmmObject.User> users = store.getGroupUsers(nonExistingDn); 
			assertTrue(users.isEmpty());		
		}
		
		@Test
		public void findExistingGroupByCnTest() { 
			Optional<VmmObject.Group> gr = store.findGroupByCn(cn);
			assertTrue(gr.isPresent());
			propsCheck(gr.get());
		}
		
		@Test
		public void findExistingGroupByCnAndSearchBaseTest() {
			Optional<VmmObject.Group> gr = store.findGroupByCn(cn, searchBase);
			assertTrue(gr.isPresent());
			propsCheck(gr.get()); 
		}
		
		@Test
		public void findNonExistingGroupByCnTest() {
			Optional<VmmObject.Group> gr = store.findGroupByCn("Executive1");
			assertTrue(!gr.isPresent());
		}
		
		@Test
		public void findExistingGroupByCnWithWrongBaseSearchTest() {
			Optional<VmmObject.Group> gr = store.findGroupByCn("ou=users,dc=kav,dc=ru");
			assertTrue(!gr.isPresent());
		}
		
		private void propsCheck(VmmObject.Group gr) {
			assertEquals(cn, gr.getCn());
			assertEquals(dn.toUpperCase(), gr.getDn().toUpperCase());
			
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(VmmStoreTest.class); 
	
	public static void main(String args[]) {
		logger.debug("ready"); 
	}
	
	
	public static void vmmStoreTest() {
		
		/*
		VmmStore s = new VmmStore(getService());
		
		Logger.debug("get group by cn");
		Optional<VmmGroup> g = s.findGroupByCn("Executive"); 
		
		Logger.debug("get group by dn");
		Optional<VmmGroup> groupByDn = s.findGroupByCn("Executive", "ou=groups,dc=kav,dc=ru"); 
		if (groupByDn.isPresent()) {
			Logger.debug("get group members"); 
			s.getGroupMembers(groupByDn.get());
		}
		
		Logger.debug("get user by login");
		Optional<VmmUser> u = s.findUserByLogin("danj", "ou=users,dc=kav,dc=ru");
		if (u.isPresent()) {
			s.getUserGroupMembership(u.get());
		}
		*/
	}
	
	public static void vmmQueryParserTest() {
		String query = "/entity[type='Group', identifier='cn=Executive,ou=groups,dc=kav,dc=ru', props='cn']/control[type='PropertyControl', props='cn']/control[type='GroupMemberControl', props='cn,uid,sn,givenName,displayName']";
		
		VmmQueryParser.QueryParser q =
							new VmmQueryParser.QueryParser(
									new VmmQuery.QueryText(query)); 
		try {
			q.parse();
		
			logger.debug("constructedQuery: " + q);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void vmmGetGroupMembersTest(VmmService service) throws VmmServiceException {
		String query = "/entity[type='Group', identifier='cn=Executive,ou=groups,dc=kav,dc=ru']/control[type='PropertyControl', props='cn']/control[type='GroupMemberControl', props='*']";
		DataObject result = service.query(query); 
		SdoDocument.Root root = new SdoDocument.Root(result); 
		for (Member mem : root.entities().get(0).members()) {
			logger.debug("mem: " + mem.toString());
			logger.debug("isGroup: " + mem.isGroup());
			logger.debug("isPersonAccount: " + mem.isPersonAccount());
			logger.debug("extracted member: " + mem.extractMember());
		}; 
	}
	
	public static void vmmGetUserMembershipTest(VmmService service) throws VmmServiceException {
		String query = "/entity[type='PersonAccount', identifier='cn=Dan Jump,ou=users,dc=kav,dc=ru']/control[type='PropertyControl', props='PersonAccount.*']/control[type='GroupMembershipControl', props='Group.*']";
		DataObject result = service.query(query); 
		
		SdoDocument.Root root = new SdoDocument.Root(result); 
		
		logger.debug("pa: " + root.entities().get(0).personAccount().toString());
		logger.debug("user: " + root.entities().get(0).personAccount().extractUser().toString());
		logger.debug("groups: " + root.entities().get(0).groups().size());
		for (Group grp : root.entities().get(0).groups()) {
			logger.debug("grp: " + grp.toString());
		}
		 
	}
	
	public static void vmmGetUserTest(VmmService service) throws VmmServiceException {
		String query = "/entity[type='PersonAccount', identifier='cn=Dan Jump,ou=users,dc=kav,dc=ru']/control[type='PropertyControl', props='PersonAccount.*']/control[type='GroupMembershipControl', props='Group.*']";
		DataObject result = service.query(query); 
		SdoDocument.Root root2 = new SdoDocument.Root(result);
		for (Entity e : root2.entities()) {
			logger.debug("entity: " + e.personAccount().toString());
			logger.debug("user: " +e.personAccount().extractUser().toString());
		}
	}
	
	public static void vmmGetGroupTest(VmmService service) throws VmmServiceException {
		String query = "/entity[type='Group', identifier='cn=Executive,ou=groups,dc=kav,dc=ru']/control[type='PropertyControl', props='Group.*']/control[type='GroupMemberControl', props='Member.*']";
		DataObject result = service.query(query); 
	}
	
	public static void vmmSearchTest(VmmService service) throws VmmServiceException {
		String query = "/searchControl[type='Group', expression=\"cn='E*'\",  props='cn']";
		DataObject result = service.query(query); 
		
		SdoDocument.Root root = new SdoDocument.Root(result);
		for (Entity e : root.entities()) {
			logger.debug("entity: " + e.group().toString());
			logger.debug("group: " +e.group().extractGroup().toString());
		}
		
		String query2 = "/searchControl[type='PersonAccount', expression=\"uid='Danj'\",  props='PersonAccount.*']/control[type='GroupMembershipControl', props='Group.*']";
		DataObject result2 = service.query(query2); 

		SdoDocument.Root root2 = new SdoDocument.Root(result2);
		for (Entity e : root2.entities()) {
			logger.debug("entity: " + e.personAccount().toString());
			logger.debug("user: " +e.personAccount().extractUser().toString());
		}
	}
	
	public static void vmmSearchByUidTest(VmmService service) throws VmmServiceException {
		String query = "/searchControl[type='PersonAccount', expression=\"uid='DANJ'\",  props='PersonAccount.*']";
		DataObject result = service.query(query); 
		SdoDocument.Root root = new SdoDocument.Root(result);
		for (Entity e : root.entities()) {
			logger.debug("entity: " + e.personAccount().toString());
			logger.debug("user: " +e.personAccount().extractUser().toString());
		}
	}
	/*
	public static void vmmServiceTest() {
		VmmStore client = new VmmStore(
				new VmmServiceSetup.Remote(
					  new VmmUserCredential.SvcVmmReader(), new VmmServerConfig.LocalhostDockerVmmServerConfig()).setup());

		client.search("da*");
		client.getByUniqueName("cn=Dan Jump,ou=users,dc=kav,dc=ru");
		client.getUserMembership("cn=Dan Jump,ou=users,dc=kav,dc=ru");
		client.getGroupMembers("cn=Executive,ou=groups,dc=kav,dc=ru");
	}
	*/
}
