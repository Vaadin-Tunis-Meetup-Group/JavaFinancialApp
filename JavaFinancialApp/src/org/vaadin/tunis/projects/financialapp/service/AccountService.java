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

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.vaadin.tunis.projects.financialapp.dto.AccountDetailsDto;
import org.vaadin.tunis.projects.financialapp.dto.AccountDto;
import org.vaadin.tunis.projects.financialapp.model.Account;
import org.vaadin.tunis.projects.financialapp.model.Session;


public class AccountService {

	@PersistenceContext
	private EntityManager em;

	private SessionService sessionService;

	public Collection<AccountDto> getAccounts() {
		Session s = sessionService.getSession();
		Collection<Account> accounts = s.getAccounts();
		Collection<AccountDto> result = new ArrayList<AccountDto>();

		for (Account next : accounts) {
			AccountDto nextDto = new AccountDto(next);
			result.add(nextDto);
		}

		return result;
	}

	public Account getAccount(long accountId) {
		return em.find(Account.class, accountId);
	}

	public AccountDetailsDto getAccountDetails(long accountId) {
		Account a = em.find(Account.class, accountId);
		return new AccountDetailsDto(a);
	}

	public long createAccount(AccountDetailsDto dto) {
		Account a = new Account();
		dto.mapToModel(a);

		Session s = sessionService.getSession();
		a.setSession(s);
		a.setParent(s.getTransferCategory());

		em.persist(a);

		return a.getId();
	}

	public void updateAccount(AccountDetailsDto dto) {
		Account a = em.find(Account.class, dto.getId());
		dto.mapToModel(a);
	}

	public void deleteAccount(long accountId) {
		Account a = em.find(Account.class, accountId);
		em.remove(a);
	}
}
