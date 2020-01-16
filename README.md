# initialize fotu vmm logger:
```java
new LoggerConfig.Loader.PropertiesLoaderImpl(configPath).load();
```
# logger configuration file name: 
vmmLogger.properties
# vmmLogger.properties
```
debugEnabled=true
loggerClassName=ru.cbr.fotu.vmm.Logger$LoggerImpl
```
# sample of initialize fotu vmm clinet:
```java
VmmStore vmmClient = new VmmStore(
			new VmmServiceSetup.Local(
				new VmmUserCredential.SvcVmmReader()).setup());
```
# sample of usage fotu vmm api:
```java
Optional<VmmObject.User> user = vmmClient.findUserByLogin("danj");
if (user.isPresent()) {
	vmmClient.getUserGroupMembership(user.get()); 
```
# fotu vmm api methods: 
```java
findGroupByCn(String)
findGroupByCn(String, String)
findUserByLogin(String)
findUserByLogin(String, String)
getGroupByDn(String)
getGroupGroups(String)
getGroupGroups(Group)
getGroupMembers(String)
getGroupMembers(Group)
getGroupUsers(String)
getGroupUsers(Group)
getUserByDn(String)
getUserGroupMembership(String)
getUserGroupMembership(User)
getUserWithGroupMembership(String)
getUserWithGroupMembership(User)
```
