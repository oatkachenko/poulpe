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
package org.jtalks.poulpe.web.controller.section;

import com.google.common.annotations.VisibleForTesting;
import org.jtalks.common.model.entity.Group;
import org.jtalks.poulpe.model.entity.Jcommune;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.service.ForumStructureService;
import org.jtalks.poulpe.web.controller.SelectedEntity;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.jtalks.poulpe.web.controller.branch.BranchPermissionManagementVm;
import org.jtalks.poulpe.web.controller.zkutils.ZkTreeModel;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Treeitem;

import javax.annotation.Nonnull;

import static org.jtalks.poulpe.web.controller.section.TreeNodeFactory.buildForumStructure;

/**
 * Is used in order to work with page that allows admin to manage sections and branches (moving them, reordering,
 * removing, editing, etc.). Note, that this class is responsible for back-end of the operations (presenter,
 * controller), so it stores all the changes to the database using {@link ComponentService}. In order to control the
 * view and what it should show/change, it uses {@link ForumStructureData}.
 *
 * @author stanislav bashkirtsev
 * @author Guram Savinov
 */
public class ForumStructureVm {
    private static final String SELECTED_ITEM_PROP = "selectedItem", VIEW_DATA_PROP = "viewData";
    private final ForumStructureService forumStructureService;
    private final WindowManager windowManager;
    private SelectedEntity<PoulpeBranch> selectedBranchForPermissions;
    private ForumStructureData viewData = new ForumStructureData();

    public ForumStructureVm(@Nonnull ForumStructureService forumStructureService, @Nonnull WindowManager windowManager,
                            @Nonnull SelectedEntity<PoulpeBranch> selectedBranchForPermissions) {
        this.forumStructureService = forumStructureService;
        this.windowManager = windowManager;
        this.selectedBranchForPermissions = selectedBranchForPermissions;
    }

    /**
     * Creates the whole sections and branches structure. Always hits database. Is executed each time a page is
     * opening.
     */
    @Init
    public void init() {
        viewData.setSectionTree(new ZkTreeModel<ForumStructureItem>(buildForumStructure(loadJcommune())));
    }

    /**
     * Shows the dialog either for creating or for editing existing section.
     *
     * @param createNew whether or not it's a creating of new section or just editing existing one
     */
    @Command
    @NotifyChange({VIEW_DATA_PROP, SELECTED_ITEM_PROP})
    public void showNewSectionDialog(@BindingParam("createNew") boolean createNew) {
        viewData.showSectionDialog(createNew);
    }

    /**
     * Shows the dialog for creating or editing the branch. Whether it's a creating or editing is decided by the
     * specified parameter.
     *
     * @param createNew pass {@code true} if this is a window for creating a new branch
     */
    @Command
    @NotifyChange({VIEW_DATA_PROP, SELECTED_ITEM_PROP})
    public void showNewBranchDialog(@BindingParam("createNew") boolean createNew) {
        viewData.showBranchDialog(createNew);
    }

    /**
     * Deletes the selected entity no matter what it is - a branch or a section. It does both: back-end removal from DB
     * and ask the {@link ForumStructureData} to remove the item from the tree.
     *
     * @see ForumStructureData#getSelectedItem()
     */
    @Command
    @NotifyChange({VIEW_DATA_PROP, SELECTED_ITEM_PROP})
    public void deleteSelected() {
        ForumStructureItem selectedItem = viewData.removeSelectedItem();
        if (selectedItem.isBranch()) {
            deleteSelectedBranch(selectedItem.getBranchItem());
        } else {
            deleteSelectedSection(selectedItem.getSectionItem());
        }
    }

    void deleteSelectedSection(PoulpeSection selectedSection) {
        Jcommune jcommune = forumStructureService.deleteSectionWithBranches(selectedSection);
        viewData.setSectionTree(new ZkTreeModel<ForumStructureItem>(buildForumStructure(jcommune)));
    }

    void deleteSelectedBranch(PoulpeBranch branchItem) {
        forumStructureService.removeBranch(branchItem);
    }

    /**
     * Opens a separate page - Branch Permissions where admin can edit what Groups have wha Permissions on the selected
     * branch.
     */
    @Command
    public void openBranchPermissions() {
        selectedBranchForPermissions.setEntity(getSelectedItem().getBranchItem());
        BranchPermissionManagementVm.showPage(windowManager, getSelectedItem().getBranchItem());
    }

    /**
     * Saves the {@link #getSelectedItem} to the database, adds it as the last one to the list of sections and cleans
     * the selected section. Also makes the create section dialog to be closed.
     */
    @Command
    @NotifyChange({VIEW_DATA_PROP})
    public void saveSection() {
        viewData.addSelectedSectionToTreeIfNew();
        storeNewSection(viewData.getSelectedEntity(PoulpeSection.class));
        viewData.closeDialog();
    }

    void storeNewSection(PoulpeSection section) {
        Jcommune jcommune = viewData.getRootAsJcommune();
        jcommune.addSection(section);
        forumStructureService.saveJcommune(jcommune);
    }

    /**
     * Processes onOK of the Branch Dialog in order to save the branch being edited. Also saves a new branch.
     */
    @Command
    @NotifyChange({VIEW_DATA_PROP, SELECTED_ITEM_PROP})
    public void saveBranch() {
        viewData.putSelectedBranchToSectionInDropdown();
        PoulpeBranch createdBranch = storeSelectedBranch();
        viewData.getSelectedItem().setItem(createdBranch);
    }

    /**
     * Stores the branch that is selected in the {@link #viewData} to the database. Adds it to the list of branches of
     * the section selected in {@link ForumStructureData#getSelectedEntity(Class)} if the branch is new or was moved to
     * another section.
     *
     * @return the stored branch with id being set
     */
    PoulpeBranch storeSelectedBranch() {
        PoulpeBranch selectedBranch = viewData.getSelectedEntity(PoulpeBranch.class);
        Group moderatingGroup = new Group(selectedBranch.getName() + " Moderators");
        selectedBranch.setModeratorsGroup(moderatingGroup);
        PoulpeSection section = viewData.getSectionSelectedInDropdown().getSectionItem();
        return forumStructureService.saveBranch(section, selectedBranch);
    }

    /**
     * Returns all the sections from our database in list model representation in order they are actually sorted.
     *
     * @return all the sections from our database in list model representation in order they are actually sorted or
     *         empty tree if there are no sections. Can't return {@code null}.
     */
    public ListModel<ForumStructureItem> getSectionList() {
        return viewData.getSectionList();
    }

    public ForumStructureData getViewData() {
        return viewData;
    }

    public ForumStructureItem getSelectedItem() {
        return viewData.getSelectedItem();
    }

    public void setSelectedItem(ForumStructureItem selectedItem) {
        this.viewData.setSelectedItem(selectedItem);
    }

    /**
     * Is used by ZK binder to inject the section that is currently selected.
     *
     * @param selectedNode the section that is currently selected
     */
    @NotifyChange(SELECTED_ITEM_PROP)
    public void setSelectedNode(DefaultTreeNode<ForumStructureItem> selectedNode) {
        this.viewData.setSelectedItem(selectedNode.getData());
    }

    /**
     * Loads instance of JCommune from database.
     *
     * @return instance of JCommune from database
     */
    private Jcommune loadJcommune() {
        return forumStructureService.getJcommune();
    }

    /**
     * Handler of event when one item was dragged and dropped to another
     *
     * @param event contains all needed info about event
     */
    @Command
    @NotifyChange(VIEW_DATA_PROP)
    public void onDropItem(@BindingParam("event") DropEvent event) {
        DefaultTreeNode<ForumStructureItem> draggedNode = ((Treeitem) event.getDragged()).getValue();
        DefaultTreeNode<ForumStructureItem> targetNode = ((Treeitem) event.getTarget()).getValue();
        ForumStructureItem draggedItem = draggedNode.getData();
        ForumStructureItem targetItem = targetNode.getData();
        if (draggedItem.isBranch() && targetItem.isBranch()) {
            PoulpeBranch draggedBranch = draggedItem.getBranchItem();
            PoulpeBranch targetBranch = targetItem.getBranchItem();
            forumStructureService.moveBranchTo(draggedBranch, targetBranch);
            viewData.setSectionTree(new ZkTreeModel<ForumStructureItem>(buildForumStructure(loadJcommune())));
        }
    }

    @VisibleForTesting
    void setViewData(ForumStructureData viewData) {
        this.viewData = viewData;
    }
}
