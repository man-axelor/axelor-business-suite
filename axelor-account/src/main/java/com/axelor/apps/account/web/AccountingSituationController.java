/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.web;

import com.axelor.apps.account.db.AccountingSituation;
import com.axelor.apps.account.db.repo.AccountingSituationRepository;
import com.axelor.apps.account.service.AccountCustomerService;
import com.axelor.apps.account.service.AccountingSituationService;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class AccountingSituationController {

	@Inject
	private AccountCustomerService acs;
	
	@Inject
	private AccountingSituationRepository accountingSituationRepo;
	
	public void updateCustomerAccount(ActionRequest request, ActionResponse response)  {
		
		AccountingSituation accountingSituation = request.getContext().asType(AccountingSituation.class);
		accountingSituation = accountingSituationRepo.find(accountingSituation.getId());
		
		try {
			if(accountingSituation != null)  {
				acs.updateCustomerAccount(accountingSituation);
			}
		}
		catch(Exception e)  { TraceBackService.trace(response, e); }
	}

	/**
	 * return the domain of the field companyInBankDetails in the view.
	 * @see AccountingSituationService#createDomainForBankDetails(AccountingSituation, boolean)
	 * @param request
	 * @param response
	 */
	public void createInBankDetailsDomain(ActionRequest request, ActionResponse response) {
		AccountingSituation accountingSituation = request.getContext().asType(AccountingSituation.class);
		String domain = Beans.get(AccountingSituationService.class).createDomainForBankDetails(accountingSituation, true);
		if (!domain.equals("")) {
			response.setAttr("companyInBankDetails","domain", domain);
		}
		else {
			response.setAttr("companyInBankDetails","self.id in (0)", domain);
		}
	}

	/**
	 * return the domain of the field companyOutBankDetails in the view.
	 * @see AccountingSituationService#createDomainForBankDetails(AccountingSituation, boolean)
	 * @param request
	 * @param response
	 */
	public void createOutBankDetailsDomain(ActionRequest request, ActionResponse response) {
		AccountingSituation accountingSituation = request.getContext().asType(AccountingSituation.class);
		String domain = Beans.get(AccountingSituationService.class).createDomainForBankDetails(accountingSituation, false);
		if (!domain.equals("")) {
			response.setAttr("companyOutBankDetails","domain", domain);
		}
		else {
			response.setAttr("companyOutBankDetails","domain", "self.id in (0)");
		}
	}
}
