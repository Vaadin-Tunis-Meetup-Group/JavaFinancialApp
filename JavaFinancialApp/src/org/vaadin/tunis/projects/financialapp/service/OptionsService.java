/*
 * Copyright 2012 Johann Gyger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.tunis.projects.financialapp.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.tunis.projects.financialapp.XMLReader;
import org.vaadin.tunis.projects.financialapp.imodel.CategoryNode;
import org.vaadin.tunis.projects.financialapp.imodel.RootCategory;
import org.vaadin.tunis.projects.financialapp.imodel.SplitCategory;
import org.vaadin.tunis.projects.financialapp.imodel.SplittedEntry;
import org.vaadin.tunis.projects.financialapp.imodel.TransferCategory;
import org.vaadin.tunis.projects.financialapp.model.Account;
import org.vaadin.tunis.projects.financialapp.model.Category;
import org.vaadin.tunis.projects.financialapp.model.Entry;
import org.vaadin.tunis.projects.financialapp.model.Session;

public class OptionsService {

    private static final Logger log = LoggerFactory.getLogger(OptionsService.class);

    private static final Entry.Status[] entryStates = {null, Entry.Status.RECONCILING, Entry.Status.CLEARED};

    @PersistenceContext
    private EntityManager em;

    private Session session;

    private org.vaadin.tunis.projects.financialapp.imodel.Session oldSession;

    private Map<org.vaadin.tunis.projects.financialapp.imodel.Category, Category> oldToNewCategoryMap = new HashMap<org.vaadin.tunis.projects.financialapp.imodel.Category, Category>();

    private Map<Entry, org.vaadin.tunis.projects.financialapp.imodel.Category> entryToOldCategoryMap = new HashMap<Entry, org.vaadin.tunis.projects.financialapp.imodel.Category>();

    private Map<org.vaadin.tunis.projects.financialapp.imodel.DoubleEntry, Entry> oldToNewDoubleEntryMap = new HashMap<org.vaadin.tunis.projects.financialapp.imodel.DoubleEntry, Entry>();

    
    private SessionService sessionService;

    public void init() {
        removeOldSession();
        Session session = new Session();
        initCategories(session);
        em.persist(session);
    }

    public Category initCategories(Session session) {
        List<Category> cList = new ArrayList<Category>();

        Category root = createCategory(Category.Type.ROOT, "[ROOT]", null, cList);
        session.setRootCategory(root);

        Category transfer = createCategory(Category.Type.TRANSFER, "[UMBUCHUNG]", root, cList);
        session.setTransferCategory(transfer);

        Category split = createCategory(Category.Type.SPLIT, "[SPLITTBUCHUNG]", root, cList);
        session.setSplitCategory(split);

        createNormalCategory("Steuern", root, cList);
        createNormalCategory("Mitgliedschaften", root, cList);
        createNormalCategory("Spenden", root, cList);
        createNormalCategory("Gebühren", root, cList);
        createNormalCategory("Geschenke", root, cList);

        Category income = createNormalCategory("Einkünfte", root, cList);
        createNormalCategory("Lohn", income, cList);
        createNormalCategory("Nebenerwerb", income, cList);
        createNormalCategory("Wertschriftenerträge", income, cList);

        Category children = createNormalCategory("Kinder", root, cList);
        createNormalCategory("Arzt", children, cList);
        createNormalCategory("Kleidung", children, cList);
        createNormalCategory("Hüten", children, cList);
        createNormalCategory("Spielsachen", children, cList);

        Category housing = createNormalCategory("Wohnen", root, cList);
        createNormalCategory("Nebenkosten/Unterhalt", housing, cList);
        createNormalCategory("Miete/Hypozins", housing, cList);
        createNormalCategory("TV", housing, cList);

        Category communication = createNormalCategory("Kommunikation", root, cList);
        createNormalCategory("Telefon", communication, cList);
        createNormalCategory("Mobile", communication, cList);
        createNormalCategory("Internet", communication, cList);

        Category insurance = createNormalCategory("Versicherungen", root, cList);
        createNormalCategory("Krankenkasse", insurance, cList);
        createNormalCategory("Haushalt/Haftpflicht", insurance, cList);

        Category household = createNormalCategory("Haushalt", root, cList);
        createNormalCategory("Lebensmittel", household, cList);
        createNormalCategory("Ausser-Haus-Verpflegung", household, cList);
        createNormalCategory("Kleidung", household, cList);

        Category transport = createNormalCategory("Verkehr", root, cList);
        createNormalCategory("Auto", transport, cList);
        createNormalCategory("ÖV", transport, cList);

        Category entertainment = createNormalCategory("Unterhaltung", root, cList);
        createNormalCategory("Bücher", entertainment, cList);
        createNormalCategory("Zeitungen", entertainment, cList);
        createNormalCategory("Zeitschriften", entertainment, cList);
        createNormalCategory("Musik", entertainment, cList);
        createNormalCategory("Filme", entertainment, cList);
        createNormalCategory("Spiele", entertainment, cList);

        Category leisure = createNormalCategory("Freizeit", root, cList);
        createNormalCategory("Ausgang", leisure, cList);
        createNormalCategory("Kino", leisure, cList);
        createNormalCategory("Sportanlässe", leisure, cList);
        createNormalCategory("Konzerte", leisure, cList);
        createNormalCategory("Ausflüge", leisure, cList);
        createNormalCategory("Bücher", leisure, cList);
        createNormalCategory("Ferien", leisure, cList);

        Category healthCare = createNormalCategory("Gesundheit", root, cList);
        createNormalCategory("Arzt", healthCare, cList);
        createNormalCategory("Apotheke", healthCare, cList);
        createNormalCategory("Zahnarzt", healthCare, cList);
        createNormalCategory("Körperpflege", healthCare, cList);

        for (Category c : cList) {
            em.persist(c);
        }

        return root;
    }

    private Category createCategory(Category.Type type, String name, Category parent, List<Category> cList) {
        Category c = new Category(type, name);
        c.setParent(parent);
        cList.add(c);
        return c;
    }

    private Category createNormalCategory(String name, Category parent, List<Category> cList) {
        Category c = new Category(Category.Type.NORMAL, name);
        c.setParent(parent);
        cList.add(c);
        return c;
    }

    private void removeOldSession() {
        if (sessionService.isSessionAvailable()) {
            Session oldSession = sessionService.getSession();
            em.remove(oldSession);
        }
    }

    public void importFile(InputStream in) {
        removeOldSession();

        oldSession = XMLReader.readSessionFromInputStream(in);

        session = new Session();
        em.persist(session);

        mapCategoryNode(oldSession.getCategories().getRootNode(), null);
        mapRootCategoryToSession();
        mapCategoryToEntry();
        mapDoubleEntries();
    }

    private void mapCategoryNode(org.vaadin.tunis.projects.financialapp.imodel.CategoryNode node, Category parent) {
        org.vaadin.tunis.projects.financialapp.imodel.Category oldCat = node.getCategory();
        if (oldCat instanceof org.vaadin.tunis.projects.financialapp.imodel.Account) {
            mapAccount((org.vaadin.tunis.projects.financialapp.imodel.Account) oldCat, parent);
        } else {
            mapCategory(node, parent);
        }
    }

    private void mapAccount(org.vaadin.tunis.projects.financialapp.imodel.Account oldAcc, Category parent) {
        Account acc = new Account();
        acc.setAbbreviation(oldAcc.getAbbrevation());
        acc.setAccountNumber(oldAcc.getAccountNumber());
        acc.setBank(oldAcc.getBank());
        acc.setName(oldAcc.getCategoryName());
        acc.setComment(oldAcc.getComment());
        acc.setCurrencyCode(oldAcc.getCurrencyCode());
        acc.setMinBalance(oldAcc.getMinBalance());
        acc.setStartBalance(oldAcc.getStartBalance());
        acc.setSession(session);
        acc.setParent(parent);

        em.persist(acc);

        oldToNewCategoryMap.put(oldAcc, acc);

        mapEntries(oldAcc, acc);
    }

    private void mapCategory(org.vaadin.tunis.projects.financialapp.imodel.CategoryNode node,
                             Category parent) {
        org.vaadin.tunis.projects.financialapp.imodel.Category oldCat = node.getCategory();
        Category cat = createCategory(parent, oldCat);

        for (int i = 0; i < node.getChildCount(); i++) {
            org.vaadin.tunis.projects.financialapp.imodel.CategoryNode childNode = (CategoryNode) node.getChildAt(i);
            mapCategoryNode(childNode, cat);
        }
    }

    private Category createCategory(Category parent,
                                    org.vaadin.tunis.projects.financialapp.imodel.Category oldCat) {
        Category cat = new Category();
        if (oldCat instanceof SplitCategory) {
            cat.setType(Category.Type.SPLIT);
            session.setSplitCategory(cat);
        } else if (oldCat instanceof TransferCategory) {
            cat.setType(Category.Type.TRANSFER);
            session.setTransferCategory(cat);
        } else if (oldCat instanceof RootCategory) {
            cat.setType(Category.Type.ROOT);
            session.setTransferCategory(cat);
        }
        cat.setName(oldCat.getCategoryName());
        cat.setParent(parent);

        em.persist(cat);

        oldToNewCategoryMap.put(oldCat, cat);
        if (oldCat instanceof SplitCategory) {
            // Workaround for redundant split category.
            org.vaadin.tunis.projects.financialapp.imodel.Category oldCat2 = oldSession.getCategories().getSplitNode().getCategory();
            oldToNewCategoryMap.put(oldCat2, cat);
        }
        return cat;
    }

    @SuppressWarnings("unchecked")
    private void mapEntries(org.vaadin.tunis.projects.financialapp.imodel.Account oldAcc, Account acc) {
        for (Object o : oldAcc.getEntries()) {
            org.vaadin.tunis.projects.financialapp.imodel.Entry oldEntry = (org.vaadin.tunis.projects.financialapp.imodel.Entry) o;

            if (oldEntry instanceof SplittedEntry) {
                mapSplitEntry(acc, oldEntry);
            } else {
                mapEntryOrDoubleEntry(acc, null, oldEntry);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private void mapSplitEntry(Account acc, org.vaadin.tunis.projects.financialapp.imodel.Entry oldEntry) {
        org.vaadin.tunis.projects.financialapp.imodel.SplittedEntry oldSe = (org.vaadin.tunis.projects.financialapp.imodel.SplittedEntry) oldEntry;
        Entry splitEntry = new Entry();

        mapEntry(splitEntry, acc, null, oldSe);

        for (Object o : oldSe.getEntries()) {
            org.vaadin.tunis.projects.financialapp.imodel.Entry oldSubEntry = (org.vaadin.tunis.projects.financialapp.imodel.Entry) o;
            mapEntryOrDoubleEntry(null, splitEntry, oldSubEntry);
        }
    }

    private void mapEntryOrDoubleEntry(Account acc, Entry splitEntry, org.vaadin.tunis.projects.financialapp.imodel.Entry oldEntry) {
        if (oldEntry instanceof org.vaadin.tunis.projects.financialapp.imodel.DoubleEntry) {
            mapDoubleEntry(new Entry(), acc, splitEntry, (org.vaadin.tunis.projects.financialapp.imodel.DoubleEntry) oldEntry);
        } else {
            mapEntry(new Entry(), acc, splitEntry, oldEntry);
        }
    }

    private void mapDoubleEntry(Entry doubleEntry, Account acc, Entry splitEntry, org.vaadin.tunis.projects.financialapp.imodel.DoubleEntry oldDe) {
        mapEntry(doubleEntry, acc, splitEntry, oldDe);
        oldToNewDoubleEntryMap.put(oldDe, doubleEntry);
    }

    private void mapEntry(Entry e, Account acc, Entry splitEntry, org.vaadin.tunis.projects.financialapp.imodel.Entry oldEntry) {
        e.setAccount(acc);
        e.setSplitEntry(splitEntry);
        e.setAmount(oldEntry.getAmount());
        e.setCreation(oldEntry.getCreation());
        e.setDate(oldEntry.getDate());
        e.setDescription(oldEntry.getDescription());
        e.setMemo(oldEntry.getMemo());
        e.setStatus(entryStates[oldEntry.getStatus()]);
        e.setValuta(oldEntry.getValuta());

        // Category might not exist yet, so this is done in a second pass.
        org.vaadin.tunis.projects.financialapp.imodel.Category oldCat = oldEntry.getCategory();
        if (oldCat != null) {
            entryToOldCategoryMap.put(e, oldCat);
        }

        em.persist(e);
    }

    private void mapRootCategoryToSession() {
        org.vaadin.tunis.projects.financialapp.imodel.Category oldRootCat = oldSession.getCategories().getRootNode().getCategory();
        Category rootCat = oldToNewCategoryMap.get(oldRootCat);
        session.setRootCategory(rootCat);
    }

    private void mapCategoryToEntry() {
        for (Map.Entry<Entry, org.vaadin.tunis.projects.financialapp.imodel.Category> mapEntry : entryToOldCategoryMap.entrySet()) {
            Entry e = mapEntry.getKey();
            org.vaadin.tunis.projects.financialapp.imodel.Category oldCat = mapEntry.getValue();
            Category c = oldToNewCategoryMap.get(mapEntry.getValue());
            if (c == null) {
                Category root = session.getRootCategory();
                createCategory(root, oldCat);
            }
            e.setCategory(c);
        }
    }

    private void mapDoubleEntries() {
        for (Map.Entry<org.vaadin.tunis.projects.financialapp.imodel.DoubleEntry, Entry> mapEntry : oldToNewDoubleEntryMap.entrySet()) {
            org.vaadin.tunis.projects.financialapp.imodel.DoubleEntry oldDe = mapEntry.getKey();
            Entry de = mapEntry.getValue();
            Entry otherDe = oldToNewDoubleEntryMap.get(oldDe.getOther());
            if (otherDe == null) {
                log.warn("Dangling double entry: " + oldDe.getDescription() + ", " + oldDe.getFullCategoryName() + ", " + oldDe.getDate());
            }
            de.setOther(otherDe);
        }
    }

}
