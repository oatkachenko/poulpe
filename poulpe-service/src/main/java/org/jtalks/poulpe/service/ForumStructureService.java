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
package org.jtalks.poulpe.service;

import org.jtalks.poulpe.model.entity.Jcommune;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.exceptions.SendingNotificationFailureException;

/**
 * A coarse-grained service to work with forum structure (sections, branches).
 *
 * @author stanislav bashkirtsev
 * @author Guram Savinov
 */
public interface ForumStructureService {

    /**
     * Saves the {@code Jcommune} instance.
     * 
     * @param jcommune the Jcommune instance to save
     */
    void saveJcommune(Jcommune jcommune);

    /**
     * Returns a {code JCommune} instance.
     *
     * @return the instance of Jcommune or {@code null} if no JCommune instance is in database
     */
    Jcommune getJcommune();

    /**
     * Removes the branch from database (and this from its section), it takes a section inside the branch in order to
     * remove it from section
     *
     * @param branch a branch to be removed from database
     * @throws SendingNotificationFailureException some connection problems happend, while trying to notify jCommune
     */
    void removeBranch(PoulpeBranch branch) throws SendingNotificationFailureException;

    /**
     * Moves the branch from one section to another. Note, that if the section was the same as the branch is in, it will
     * result in no-op.
     *
     * @param branch    a branch to move from its section to another one
     * @param toSection a target section to add the branch to
     */
    void moveBranch(PoulpeBranch branch, PoulpeSection toSection);

    /**
     * Deletes the specified section from its JCommune instance and returns updated JCommune. Results in no-op if there
     * is no such section in DB.
     *
     * @param section a section to be removed from the database
     * @return the updated JCommune
     */
    Jcommune deleteSectionWithBranches(PoulpeSection section) throws SendingNotificationFailureException;

    void deleteSectionAndMoveBranches(PoulpeSection toRemove, PoulpeSection toReceiveBranches);

    PoulpeBranch saveBranch(PoulpeSection inSection, PoulpeBranch notYetSavedBranch);

    /**
     * Moves the branch to the target branch place. Shifts the target branch and any subsequent branches to the right. 
     * 
     * @param branch a branch to move
     * @param target a target branch that will be shifted
     */
    void moveBranch(PoulpeBranch branch, PoulpeBranch target);
}
