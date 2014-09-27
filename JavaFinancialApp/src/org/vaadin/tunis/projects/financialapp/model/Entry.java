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

package org.vaadin.tunis.projects.financialapp.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Entry {

    public static enum Status {
        RECONCILING, CLEARED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Entry splitEntry;

    @OneToMany(mappedBy = "splitEntry", orphanRemoval = true)
    private List<Entry> subEntries;

    /**
     * Double entry booking
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Entry other;

    @Enumerated
    private Status status;

    private long creation = Calendar.getInstance().getTime().getTime();
    private Date date;
    private Date valuta;
    private String description;
    private long amount;
    private String memo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getValuta() {
        return valuta;
    }

    public void setValuta(Date valuta) {
        this.valuta = valuta;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Entry getSplitEntry() {
        return splitEntry;
    }

    public void setSplitEntry(Entry splitEntry) {
        this.splitEntry = splitEntry;
    }

    public List<Entry> getSubEntries() {
        return subEntries;
    }

    public void setSubEntries(List<Entry> subEntries) {
        this.subEntries = subEntries;
    }

    public Entry getOther() {
        return other;
    }

    public void setOther(Entry other) {
        this.other = other;
    }

    public boolean contains(String filter) {
        if (StringUtils.isEmpty(filter)) {
            return true;
        }

        String categoryName = category != null ? category.getName() : null;

        return StringUtils.contains(StringUtils.defaultString(description), filter)
                || StringUtils.contains(StringUtils.defaultString(categoryName), filter)
                || StringUtils.contains(StringUtils.defaultString(memo), filter);
    }

}