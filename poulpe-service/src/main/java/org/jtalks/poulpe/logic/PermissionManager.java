/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.logic;

import java.util.List;

import javax.annotation.Nonnull;

import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.security.acl.AclManager;
import org.jtalks.common.security.acl.GroupAce;
import org.jtalks.common.security.acl.builders.AclBuilders;
import org.jtalks.poulpe.model.dao.GroupDao;
import org.jtalks.poulpe.model.dto.PermissionChanges;
import org.jtalks.poulpe.model.dto.PermissionsMap;
import org.jtalks.poulpe.model.entity.PoulpeGroup;

/**
 * Responsible for allowing, restricting or deleting the permissions of the User Groups to actions.
 * 
 * @author stanislav bashkirtsev
 * @author Vyacheslav Zhivaev
 */
public class PermissionManager {
    private final AclManager aclManager;
    private final GroupDao groupDao;

    /**
     * Constructs {@link PermissionManager} with given {@link AclManager} and {@link GroupDao}
     * 
     * @param aclManager manager instance
     * @param groupDao group dao instance
     */
    public PermissionManager(@Nonnull AclManager aclManager, @Nonnull GroupDao groupDao) {
        this.aclManager = aclManager;
        this.groupDao = groupDao;
    }

    /**
     * Changes the granted permissions according to the specified changes.
     * 
     * @param entity the entity to change permissions to
     * @param changes contains a permission itself, a list of groups to be granted to the permission and the list of
     * groups to remove their granted privileges
     * @see org.jtalks.poulpe.model.dto.PermissionChanges#getNewlyAddedGroupsAsArray()
     * @see org.jtalks.poulpe.model.dto.PermissionChanges#getRemovedGroups()
     */
    public void changeGrants(Entity entity, PermissionChanges changes) {
        AclBuilders builders = new AclBuilders();
        builders.newBuilder(aclManager).grant(changes.getPermission()).to(changes.getNewlyAddedGroupsAsArray())
                .on(entity).flush();
        builders.newBuilder(aclManager).delete(changes.getPermission()).from(changes.getRemovedGroupsAsArray())
                .on(entity).flush();
    }

    /**
     * Changes the restricting permissions according to the specified changes.
     * 
     * @param entity the entity to change permissions to
     * @param changes contains a permission itself, a list of groups to be restricted from the permission and the list
     * of groups to remove their restricting privileges
     * @see org.jtalks.poulpe.model.dto.PermissionChanges#getNewlyAddedGroupsAsArray()
     * @see org.jtalks.poulpe.model.dto.PermissionChanges#getRemovedGroups()
     */
    public void changeRestrictions(Entity entity, PermissionChanges changes) {
        AclBuilders builders = new AclBuilders();
        builders.newBuilder(aclManager).restrict(changes.getPermission()).to(changes.getNewlyAddedGroupsAsArray())
                .on(entity).flush();
        builders.newBuilder(aclManager).delete(changes.getPermission()).from(changes.getRemovedGroupsAsArray())
                .on(entity).flush();
    }

    /**
     * @param branch object identity
     * @return {@link BranchPermissions} for given branch
     */
    public PermissionsMap<BranchPermission> getPermissionsMapFor(Branch branch) {
        return getPermissionsMapFor(BranchPermission.getAllAsList(), branch);
    }

    /**
     * Gets {@link PermissionsMap} for provided {@link Component}.
     * 
     * @param component the component to obtain PermissionsMap for
     * @return {@link PermissionsMap} for {@link Component}
     */
    public PermissionsMap<GeneralPermission> getPermissionsMapFor(Component component) {
        return getPermissionsMapFor(GeneralPermission.getAllAsList(), component);
    }

    /**
     * Gets {@link PermissionsMap} for provided {@link Entity}.
     * 
     * @param permissions the list of permissions to get
     * @param entity the entity to get for
     * @return {@link PermissionsMap} for provided {@link Entity}
     */
    public <T extends JtalksPermission> PermissionsMap<T> getPermissionsMapFor(List<T> permissions, Entity entity) {
        PermissionsMap<T> permissionsMap = new PermissionsMap<T>(permissions);
        List<GroupAce> groupAces = aclManager.getGroupPermissionsOn(entity);
        for (T permission : permissions) {
            for (GroupAce groupAce : groupAces) {
                if (groupAce.getBranchPermissionMask() == permission.getMask()) {
                    permissionsMap.add(permission, getGroup(groupAce), groupAce.isGranting());
                }
            }
        }
        return permissionsMap;
    }

    /**
     * @param groupAce from which if of group should be extracted
     * @return {@link PoulpeGroup} extracted from {@link GroupAce}
     */
    private PoulpeGroup getGroup(GroupAce groupAce) {
        return groupDao.get(groupAce.getGroupId());
    }
}