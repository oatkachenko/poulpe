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
package org.jtalks.poulpe.service.transactional;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jtalks.poulpe.model.dao.UserDao;
import org.jtalks.poulpe.model.entity.PoulpeUser;
import org.jtalks.poulpe.pages.Pages;
import org.jtalks.poulpe.pages.Pagination;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class for testing {@code TransactionalUserService} functionality.
 * 
 * @author Guram Savinov
 * @author Vyacheslav Zhivaev
 */
public class TransactionalUserServiceTest {

    // sut
    private TransactionalUserService userService;

    // dependencies
    private UserDao userDao;
    
    final String searchString = "searchString";

    @BeforeMethod
    public void setUp() {
        userDao = mock(UserDao.class);
        userService = new TransactionalUserService(userDao);
    }

    @Test
    public void getAll() {
        String NO_FILTER = "";
        userService.getAll();
        verify(userDao).findPoulpeUsersPaginated(NO_FILTER, Pages.NONE);
    }

    @Test
    public void countUsernameMatches() {
        userService.countUsernameMatches(searchString);
        verify(userDao).countUsernameMatches(searchString);
    }

    @Test
    public void findUsersPaginated() {
        int page = 1, limit = 10;
        userService.findUsersPaginated(searchString, page, limit);
        verify(userDao).findPoulpeUsersPaginated(eq(searchString), paginatedWith(page, limit));
    }

    @Test
    public void testGetUsersByUsernameWord() {
        userService.withUsernamesMatching(searchString);
        verify(userDao).findPoulpeUsersPaginated(searchString, Pages.NONE);
    }

    @Test
    public void testUpdateUser() {
        PoulpeUser user = user();
        userService.updateUser(user);
        verify(userDao).update(user);
    }

    @Test
    public void testGetAllBannedUsers() {
        userService.getAllBannedUsers();
        verify(userDao).getAllBannedUsers();
    }

    @Test
    public void testGetNonBannedByUsername() {
        int maxCount = 1000;

        userService.getNonBannedByUsername(searchString, maxCount);
        verify(userDao).getNonBannedByUsername(searchString, maxCount);
    }

    private static PoulpeUser user() {
        return new PoulpeUser(RandomStringUtils.randomAlphanumeric(10), "username@mail.com", "PASSWORD", "salt");
    }

    // TODO: i think it should be moved away
    public static Pagination paginatedWith(int page, int limit) {
        final Pagination expected = Pages.paginate(page, limit);

        return argThat(new BaseMatcher<Pagination>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof Pagination) {
                    Pagination second = (Pagination) o;
                    return expected.getFrom() == second.getFrom() && expected.getCount() == second.getCount();
                }
                return false;
            }

            @Override
            public void describeTo(Description d) {
            }
        });
    }

}
