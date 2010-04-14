/*
 * org.openmicroscopy.shoola.env.data.AdminServiceImpl 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2010 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.env.data;


//Java imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

//Third-party libraries

//Application-internal dependencies
import omero.model.Experimenter;
import omero.model.ExperimenterGroup;
import omero.model.Permissions;
import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.config.AgentInfo;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.data.login.UserCredentials;
import org.openmicroscopy.shoola.env.data.model.AdminObject;
import org.openmicroscopy.shoola.env.data.util.PojoMapper;

import pojos.ExperimenterData;
import pojos.GroupData;
import pojos.PermissionData;

/** 
 * Implementation of the {@link AdminService} I/F.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
class AdminServiceImpl
	implements AdminService
{

	/** Uses it to gain access to the container's services. */
	private Registry                context;

	/** Reference to the entry point to access the <i>OMERO</i> services. */
	private OMEROGateway            gateway;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param gateway   Reference to the OMERO entry point.
	 *                  Mustn't be <code>null</code>.
	 * @param registry  Reference to the registry. Mustn't be <code>null</code>.
	 */
	AdminServiceImpl(OMEROGateway gateway, Registry registry)
	{
		if (registry == null)
			throw new IllegalArgumentException("No registry.");
		if (gateway == null)
			throw new IllegalArgumentException("No gateway.");
		context = registry;
		this.gateway = gateway;
	}
	
	/**
	 * Implemented as specified by {@link OmeroDataService}.
	 * @see OmeroDataService#getServerName()
	 */
	public String getServerName() 
	{
		UserCredentials uc = (UserCredentials) 
		context.lookup(LookupNames.USER_CREDENTIALS);
		return uc.getHostName();
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#getServerVersion()
	 */
	public String getServerVersion()
	{
		try {
			return gateway.getServerVersion();
		} catch (Exception e) {
			//ignore it.
		}
		return "";
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#getLoggingName()
	 */
	public String getLoggingName() 
	{
		UserCredentials uc = (UserCredentials) 
		context.lookup(LookupNames.USER_CREDENTIALS);
		return uc.getUserName();
	}
	
	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#getSpace(int, long)
	 */
	public long getSpace(int index, long userID)
		throws DSOutOfServiceException, DSAccessException
	{
		switch (index) {
			case USED: return gateway.getUsedSpace();
			case FREE: return gateway.getFreeSpace();
		}
		return -1;
	}
	
	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#changePassword(String, String)
	 */
	public Boolean changePassword(String oldPassword, String newPassword) 
		throws DSOutOfServiceException, DSAccessException 
	{
		if (newPassword == null || newPassword.trim().length() == 0)
			throw new IllegalArgumentException("Password not valid.");
		UserCredentials uc = (UserCredentials) 
		context.lookup(LookupNames.USER_CREDENTIALS);
		if (!uc.getPassword().equals(oldPassword)) return Boolean.FALSE;

		gateway.changePassword(newPassword);
		uc.resetPassword(newPassword);
		return Boolean.TRUE;
	}
	
	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#changeExperimenterGroup(ExperimenterData, long)
	 */
	public void changeExperimenterGroup(ExperimenterData exp, long groupID)
		throws DSOutOfServiceException, DSAccessException
	{
		if (exp == null) 
			throw new DSAccessException("No object to update.");
		if (groupID < 0)
			throw new DSAccessException("No group specified.");
		if (exp.getDefaultGroup().getId() != groupID) {
			gateway.changeCurrentGroup(exp, groupID);
		}
		UserCredentials uc = (UserCredentials) 
			context.lookup(LookupNames.USER_CREDENTIALS);
		ExperimenterData data = gateway.getUserDetails(uc.getUserName());
		
		context.bind(LookupNames.CURRENT_USER_DETAILS, data);
//		Bind user details to all agents' registry.
		List agents = (List) context.lookup(LookupNames.AGENTS);
		Iterator i = agents.iterator();
		AgentInfo agentInfo;
		while (i.hasNext()) {
			agentInfo = (AgentInfo) i.next();
			if (agentInfo.isActive()) {
				agentInfo.getRegistry().bind(
						LookupNames.CURRENT_USER_DETAILS, data);
			}
		}
	}
	
	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#updateExperimenter(ExperimenterData, GroupData)
	 */
	public ExperimenterData updateExperimenter(ExperimenterData exp, GroupData 
			group) 
		throws DSOutOfServiceException, DSAccessException 
	{
		//ADD control
		if (exp == null) 
			throw new DSAccessException("No object to update.");
		ExperimenterData currentUser = (ExperimenterData)
			context.lookup(LookupNames.CURRENT_USER_DETAILS);
		if (exp.getId() != currentUser.getId()) return exp;
		UserCredentials uc = (UserCredentials) 
			context.lookup(LookupNames.USER_CREDENTIALS);
		gateway.updateExperimenter(exp.asExperimenter());
		ExperimenterData data;
		if (group != null && exp.getDefaultGroup().getId() != group.getId()) {
			gateway.changeCurrentGroup(exp, group.getId());
		}
		data = gateway.getUserDetails(uc.getUserName());
		if (currentUser.getId() != exp.getId()) 
			return data;
		
		
		context.bind(LookupNames.CURRENT_USER_DETAILS, data);
//		Bind user details to all agents' registry.
		List agents = (List) context.lookup(LookupNames.AGENTS);
		Iterator i = agents.iterator();
		AgentInfo agentInfo;
		while (i.hasNext()) {
			agentInfo = (AgentInfo) i.next();
			if (agentInfo.isActive()) {
				agentInfo.getRegistry().bind(
						LookupNames.CURRENT_USER_DETAILS, data);
			}
		}
		return data;
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#createExperimenters(AdminObject)
	 */
	public List<ExperimenterData> createExperimenters(AdminObject object)
		throws DSOutOfServiceException, DSAccessException
	{
		if (object == null)
			throw new IllegalArgumentException("No object.");
		Map<ExperimenterData, UserCredentials> m = object.getExperimenters();
		if (m == null || m.size() == 0)
			throw new IllegalArgumentException("No experimenters to create.");
		return gateway.createExperimenters(object);
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#createGroup(AdminObject)
	 */
	public GroupData createGroup(AdminObject object)
		throws DSOutOfServiceException, DSAccessException
	{
		if (object == null)
			throw new IllegalArgumentException("No object.");
		if (object.getGroup() == null)
			throw new IllegalArgumentException("No group.");
		return gateway.createGroup(object);
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#loadExperimenters(long)
	 */
	public List<ExperimenterData> loadExperimenters(long groupID)
		throws DSOutOfServiceException, DSAccessException
	{
		List<ExperimenterData> l = new ArrayList<ExperimenterData>();
		return l;
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#loadGroups(long)
	 */
	public List<GroupData> loadGroups(long id) 
		throws DSOutOfServiceException, DSAccessException
	{
		return gateway.loadGroups(id);
	}
	
	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#deleteExperimenters(List)
	 */
	public List<ExperimenterData> deleteExperimenters(
			List<ExperimenterData> experimenters)
		throws DSOutOfServiceException, DSAccessException
	{
		if (experimenters == null || experimenters.size() == 0)
			throw new IllegalArgumentException("No experimenters to delete.");
		return gateway.deleteExperimenters(experimenters);
	}
	
	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#deleteGroups(List)
	 */
	public List<GroupData> deleteGroups(List<GroupData> groups)
		throws DSOutOfServiceException, DSAccessException
	{
		if (groups == null || groups.size() == 0)
			throw new IllegalArgumentException("No groups to delete.");
		return gateway.deleteGroups(groups);
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#getPermissionLevel(GroupData)
	 */
	public int getPermissionLevel(GroupData group)
	{
		ExperimenterData exp = (ExperimenterData) context.lookup(
				LookupNames.CURRENT_USER_DETAILS);
		if (group == null) {
			group = exp.getDefaultGroup();
		}
		PermissionData perm = group.getPermissions();
		if (perm.isGroupRead()) {
			if (perm.isGroupWrite())  
				return AdminObject.PERMISSIONS_GROUP_READ_LINK;
			return AdminObject.PERMISSIONS_GROUP_READ;
		}
		if (perm.isWorldRead()) {
			if (perm.isWorldWrite())  
				return AdminObject.PERMISSIONS_PUBLIC_READ_WRITE;
			return AdminObject.PERMISSIONS_PUBLIC_READ;
		}
		//Check if the user is owner of the group.
		Set leaders = group.getLeaders();
		if (leaders == null || leaders.size() == 0) 
			return AdminObject.PERMISSIONS_PRIVATE;
		Iterator j = leaders.iterator();
		long id = exp.getId();
		while (j.hasNext()) {
			exp = (ExperimenterData) j.next();
			if (exp.getId() == id) 
				return AdminObject.PERMISSIONS_GROUP_READ;
		}
		return AdminObject.PERMISSIONS_PRIVATE;
	}
	
	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#getPermissionLevel()
	 */
	public int getPermissionLevel()
	{
		ExperimenterData exp = (ExperimenterData) context.lookup(
				LookupNames.CURRENT_USER_DETAILS);
		GroupData g = exp.getDefaultGroup();
		return getPermissionLevel(g);
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#updateGroup(GroupData, int)
	 */
	public GroupData updateGroup(GroupData group, int permissions)
			throws DSOutOfServiceException, DSAccessException
	{
		if (group == null)
			throw new IllegalArgumentException("No group to update.");
		ExperimenterGroup g = group.asGroup();
		Permissions p = null;
		if (permissions != -1) {
			p = g.getDetails().getPermissions();
			gateway.setPermissionsLevel(p, permissions);
		}
		return gateway.updateGroup(g, p);
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#copyExperimenters(GroupData, Set)
	 */
	public List<ExperimenterData> copyExperimenters(GroupData group, 
				Set experimenters) 
		throws DSOutOfServiceException, DSAccessException
	{
		if (group == null)
			throw new IllegalArgumentException("No group specified.");
		if (experimenters == null || experimenters.size() == 0) 
			return new ArrayList<ExperimenterData>();
		return gateway.copyExperimenters(group, experimenters);
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#cutAndPasteExperimenters(Map, Map)
	 */
	public List<ExperimenterData> cutAndPasteExperimenters(Map toPaste, 
			Map toCut)
			throws DSOutOfServiceException, DSAccessException
	{
		if (toPaste == null) toPaste = new HashMap();
		if (toCut == null) toCut = new HashMap();
		Iterator i;
		Object parent;
		Entry entry;
		List<ExperimenterData> r = new ArrayList<ExperimenterData>();
		i = toCut.entrySet().iterator();
		while (i.hasNext()) {
			entry = (Entry) i.next();
			parent = entry.getKey();
			if (parent instanceof GroupData)
				r.addAll(gateway.removeExperimenters((GroupData) parent, 
						(Set) entry.getValue()));
		}

		i = toPaste.entrySet().iterator();

		while (i.hasNext()) {
			entry = (Entry) i.next();
			parent = entry.getKey();
			if (parent instanceof GroupData) //b/c of orphaned container
				r.addAll(copyExperimenters((GroupData) parent, 
						(Set) entry.getValue()));
		}
		
		//Need to check if the experimenters belong to at least one group
		//which is not the system group.
		/*
		Set experimenters;
		i = toCut.entrySet().iterator();
		Iterator j;
		List<GroupData> groups;
		ExperimenterData exp;
		List<ExperimenterData> list = new ArrayList<ExperimenterData>();
		while (i.hasNext()) {
			entry = (Entry) i.next();
			parent = entry.getKey();
			if (parent instanceof GroupData) {
				experimenters = (Set) entry.getValue();
				if (experimenters != null) {
					j = experimenters.iterator();
					while (j.hasNext()) {
						exp = (ExperimenterData) j.next();
						groups = gateway.getGroups(exp.getId());
						if (groups.size() == 0) list.add(exp);
						
					}
				}
			}
		}
		//Update the list of experimenters.
		if (list.size() > 0) {
			
		}
		*/
		return r;
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#countExperimenters(List)
	 */
	public Map<Long, Long> countExperimenters(List<Long> ids)
			throws DSOutOfServiceException, DSAccessException
	{
		if (ids == null || ids.size() == 0)
			return new HashMap<Long, Long>();
		return gateway.countExperimenters(ids);
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#updateExperimenters(GroupData, Map)
	 */
	public List<ExperimenterData> updateExperimenters(GroupData group,
			Map<ExperimenterData, UserCredentials> experimenters)
			throws DSOutOfServiceException, DSAccessException
	{
		if (experimenters == null)
			throw new IllegalArgumentException("No experimenters to update");
		Entry entry;
		Iterator i = experimenters.entrySet().iterator();
		List<ExperimenterData> l = new ArrayList<ExperimenterData>();
		List<Experimenter> ownersToAdd = new ArrayList<Experimenter>();
		List<Experimenter> ownersToRemove = new ArrayList<Experimenter>();
		List<Experimenter> administratorsToAdd = new ArrayList<Experimenter>();
		List<Experimenter> 
		administratorsToRemove = new ArrayList<Experimenter>();
		List<ExperimenterData> toActivate = new ArrayList<ExperimenterData>();
		List<ExperimenterData> toDeactivate = new ArrayList<ExperimenterData>();
		
		ExperimenterData exp;
		UserCredentials uc;
		Boolean b;
		while (i.hasNext()) {
			entry = (Entry) i.next();
			exp = (ExperimenterData) entry.getKey();
			uc = (UserCredentials) entry.getValue();
			try {
				updateExperimenter(exp, group);
				b = uc.isOwner();
				if (b != null) {
					if (b.booleanValue()) ownersToAdd.add(exp.asExperimenter());
					else ownersToRemove.add(exp.asExperimenter());
				}
				b = uc.isAdministrator();
				if (b != null) {
					if (b.booleanValue()) 
						administratorsToAdd.add(exp.asExperimenter());
					else administratorsToRemove.add(exp.asExperimenter());
				}
				b = uc.isActive();
				if (b != null) {
					if (b.booleanValue()) 
						toActivate.add(exp);
					else toDeactivate.add(exp);
				}
				//Check owner
				//reset login name
				if (!exp.getUserName().equals(uc.getUserName())) {
					gateway.resetUserName(uc.getUserName(), exp);
				}
			} catch (Exception e) {
				l.add(exp);
			}
		}
		if (group != null) {
			if (ownersToAdd.size() > 0)
				gateway.handleGroupOwners(true, group.asGroup(), ownersToAdd);
			if (ownersToRemove.size() > 0)
				gateway.handleGroupOwners(false, group.asGroup(), 
						ownersToRemove);
		}
		if (toActivate.size() > 0)
			gateway.modifyExperimentersRoles(true, toActivate, GroupData.USER);
		if (toDeactivate.size() > 0)
			gateway.modifyExperimentersRoles(false, toDeactivate, 
					GroupData.USER);
		return l;
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#resetExperimentersPassword(AdminObject)
	 */
	public List<ExperimenterData> resetExperimentersPassword(AdminObject object)
			throws DSOutOfServiceException, DSAccessException
	{
		if (object == null)
			throw new IllegalArgumentException("No experimenters" +
					" specified");
		if (AdminObject.RESET_PASSWORD != object.getIndex())
			throw new IllegalArgumentException("No experimenters specified");
		Map<ExperimenterData, UserCredentials> map = object.getExperimenters();
		if (map == null) 
			throw new IllegalArgumentException("No experimenters specified");
			
		List<ExperimenterData> l = new ArrayList<ExperimenterData>();
		UserCredentials uc;
		Entry entry;
		ExperimenterData exp;
		Iterator i = map.entrySet().iterator();
		while (i.hasNext()) {
			entry = (Entry) i.next();
			exp = (ExperimenterData) entry.getKey();
			uc = (UserCredentials) entry.getValue();
			try {
				//check that the user is not ldap
				String ldap = gateway.lookupLdapAuthExperimenter(exp.getId());
				if (ldap != null && ldap.length() > 0) l.add(exp);
				else 
					gateway.resetPassword(exp.getUserName(), exp.getId(), 
						uc.getPassword());
			} catch (Exception e) {
				l.add(exp);
			}
		}
		return l;
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#reloadPIGroups(ExperimenterData)
	 */
	public List<GroupData> reloadPIGroups(ExperimenterData exp)
			throws DSOutOfServiceException, DSAccessException
	{
		Set<GroupData> groups;
		Set<GroupData> available;
		UserCredentials uc = (UserCredentials)
			context.lookup(LookupNames.USER_CREDENTIALS);
		List<ExperimenterData> exps = new ArrayList<ExperimenterData>();
		groups = gateway.getAvailableGroups(exp);
		//Check if the current experimenter is an administrator 
		Iterator<GroupData> i = groups.iterator();
		GroupData g;
		available = new HashSet<GroupData>();
		while (i.hasNext()) {
			g = i.next();
			if (!gateway.isSystemGroup(g.asGroup())) {
				available.add(g);
			} else {
				if (GroupData.SYSTEM.equals(g.getName()))
					uc.setAdministrator(true);
			}
		}
		context.bind(LookupNames.USER_GROUP_DETAILS, available);
		List<Long> ids = new ArrayList<Long>();
		i = available.iterator();
		Set set;
		Iterator j;
		ExperimenterData e;
		while (i.hasNext()) {
			g = (GroupData) i.next();
			set = g.getExperimenters();
			j = set.iterator();
			while (j.hasNext()) {
				e = (ExperimenterData) j.next();
				if (!ids.contains(e.getId())) {
					ids.add(e.getId());
					exps.add(e);
				}
			}
		}
		context.bind(LookupNames.USERS_DETAILS, exps);	
		List<GroupData> result = new ArrayList<GroupData>();
		Iterator<GroupData> k = available.iterator();
		while (k.hasNext()) {
			result.add(k.next());
		}
		
        //Bind user details to all agents' registry.
        List agents = (List) context.lookup(LookupNames.AGENTS);
		j = agents.iterator();
		AgentInfo agentInfo;
		Registry reg;
		while (i.hasNext()) {
			agentInfo = (AgentInfo) j.next();
			if (agentInfo.isActive()) {
				reg = agentInfo.getRegistry();
				reg.bind(LookupNames.USER_GROUP_DETAILS, available);
				reg.bind(LookupNames.USERS_DETAILS, exps);
			}
		}
		return result;
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#isExistingExperimenter(String)
	 */
	public ExperimenterData lookupExperimenter(String name)
			throws DSOutOfServiceException, DSAccessException
	{
		Experimenter value = gateway.lookupExperimenter(name);
		if (value != null) 
			return (ExperimenterData) PojoMapper.asDataObject(value);
		return null;
	}

	/**
	 * Implemented as specified by {@link AdminService}.
	 * @see AdminService#isExistingGroup(String)
	 */
	public GroupData lookupGroup(String name) 
		throws DSOutOfServiceException, DSAccessException
	{
		ExperimenterGroup value = gateway.lookupGroup(name);
		if (value != null) 
			return (GroupData) PojoMapper.asDataObject(value);
		return null;
	}
	
}
