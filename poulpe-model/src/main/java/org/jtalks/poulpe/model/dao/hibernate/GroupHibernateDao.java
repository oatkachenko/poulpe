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
package org.jtalks.poulpe.model.dao.hibernate;

import org.hibernate.Query;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateParentRepository;
import org.jtalks.common.model.entity.Group;
import org.jtalks.poulpe.model.dao.GroupDao;
import ru.javatalks.utils.general.Assert;

import java.util.List;

/**
 * Hibernate implementation of {@link GroupDao}
 *
 * @author Vitaliy Kravchenko
 * @author Pavel Vervenko
 */
public class GroupHibernateDao extends AbstractHibernateParentRepository<Group> implements GroupDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Group> getAll() {
		return getSession().createQuery("from Group").list();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Group> getByName(String name) {
		Assert.throwIfNull(name, "name");

		Query query = getSession().createQuery("from Group g where g.name = ?");
		query.setString(0, name);

		return query.list();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Group group) {
		getSession().update(group);

		group.getUsers().clear();
		saveOrUpdate(group);
		super.delete(group);
	}
}