/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.model.dao;

import java.util.List;
import java.util.Set;

import org.jtalks.common.model.dao.ParentRepository;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;

/**
 * Dao for jtalks engine {@link Component}.
 * 
 * @author Pavel Vervenko
 */
public interface ComponentDao extends ParentRepository<Component> {

    /**
     * The enumeration of the fields which ought to be unique in the DB for the User.
     * @author Dmitriy Sukharev
     */
    public static enum ComponentDuplicateField implements DuplicatedField { NAME, TYPE }

    /**
     * Get the list of all components.
     * 
     * @return components list
     */
    List<Component> getAll();
    
    /**
     * Get the set of unoccupied ComponentType.
     * @return set of ComponentType
     */
    Set<ComponentType> getAvailableTypes();
    
    /**
     * Obtains the set of such fields which ought to be unique and whose uniqueness will be violated
     * after adding {@code component} to the data source.
     * @param component the component object
     * @return the set of fields whose uniqueness will be violated after adding {@code component}
     *         to the data source
     */
    Set<DuplicatedField> getDuplicateFieldsFor(Component component);
}
