package org.vaadin.tunis.projects.financialapp.imodel;

import org.vaadin.tunis.projects.financialapp.SortedTreeNode;

public class CategoryNode extends SortedTreeNode {

    /**
     * Used by XMLDecoder.
     */
    public CategoryNode() { }

    /**
     * Creates a new category node
     * @param cat a category
     */
    public CategoryNode(Category cat) { super(cat); }

    /**
     * @return the category.
     */
    public Category getCategory() { return (Category) getUserObject(); }

}
