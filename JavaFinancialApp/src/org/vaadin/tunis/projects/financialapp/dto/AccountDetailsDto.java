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

package org.vaadin.tunis.projects.financialapp.dto;

import org.vaadin.tunis.projects.financialapp.model.Account;

public class AccountDetailsDto extends AccountDto {

    private String currencyCode;
    private String bank;
    private String accountNumber;
    private long startBalance;
    private Long minBalance;
    private String abbreviation;
    private String comment;

    public AccountDetailsDto() {
    }

    public AccountDetailsDto(Account account) {
        super(account);
        currencyCode = account.getCurrencyCode();
        bank = account.getBank();
        accountNumber = account.getAccountNumber();
        startBalance = account.getStartBalance();
        minBalance = account.getMinBalance();
        abbreviation = account.getAbbreviation();
        comment = account.getComment();
    }

    public void mapToModel(Account account) {
        account.setName(getName());
        account.setAbbreviation(getAbbreviation());
        account.setAccountNumber(getAccountNumber());
        account.setBank(getBank());
        account.setComment(getComment());
        account.setCurrencyCode(getCurrencyCode());
        account.setMinBalance(getMinBalance());
        account.setStartBalance(getStartBalance());
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(long startBalance) {
        this.startBalance = startBalance;
    }

    public Long getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(Long minBalance) {
        this.minBalance = minBalance;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
