package org.jtalks.poulpe.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.common.model.entity.Entity;

/**
 * forum section 
 * @author tanya birina
 *
 */
public class Section extends Entity {

	private String name;
	private Integer position;
	private String description;
	private List <Branch> branches;
	
	 /**
     * Set section name which briefly describes the topics contained in it.
     *
     * @return section name
     */
    public String getName() {
        return name;
    }

    /**
     * Get section name.
     *
     * @param name section name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get section description.
     *
     * @return section description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set section description which contains additional information about the section.
     *
     * @param description section description
     */
    public void setDescription(String description) {
        this.description = description;
    } 
    
    public Integer getPosition () {
    	return position;
    }
    
    public void setPosition (Integer position) {
    	this.position = position;
    }
    
    /**
     * @return list of branches
     */
    public List<Branch> getBranches() {
        return branches;
    }
    
    /**
     * @param branches set  list of branches
     */
    public void setBranches(List<Branch> branches) {
    	if (branches == null)
    		branches = new ArrayList <Branch> ();
        this.branches = branches;
    }


    /**
     * Add branch to section.
     *
     * @param branch branch
     */
    public void addBranch(Branch branch) {
    	if (branches == null)
    		branches = new ArrayList <Branch> ();
       branches.add(branch);
    }

    /**
     * Delete branch from section.
     *
     * @param branch branch
     */
    public void deleteBranch(Branch branch) {
    	branches.remove(branch);
    }

}
