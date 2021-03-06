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
package com.axelor.apps.account.service.batch;

import com.axelor.apps.account.db.AccountingBatch;
import com.axelor.apps.account.db.repo.AccountingBatchRepository;
import com.axelor.apps.base.db.Batch;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.apps.base.service.administration.AbstractBatchService;
import com.axelor.db.Model;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.IException;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;

/**
 * InvoiceBatchService est une classe implémentant l'ensemble des batchs de
 * comptabilité et assimilé.
 * 
 * @author Geoffrey DUBAUX
 * 
 * @version 0.1
 */
public class AccountingBatchService extends AbstractBatchService {

	@Override
	protected Class<? extends Model> getModelClass() {
		return AccountingBatch.class;
	}

	@Override
	public Batch run(Model batchModel) throws AxelorException {

		Batch batch;
		AccountingBatch accountingBatch = (AccountingBatch) batchModel;

		switch (accountingBatch.getActionSelect()) {
		case AccountingBatchRepository.ACTION_REIMBURSEMENT:
			if(accountingBatch.getReimbursementTypeSelect() == AccountingBatchRepository.REIMBURSEMENT_TYPE_EXPORT)  {
				batch = reimbursementExport(accountingBatch);
			}
			else if(accountingBatch.getReimbursementTypeSelect() == 
					AccountingBatchRepository.REIMBURSEMENT_TYPE_IMPORT)  {
				batch = reimbursementImport(accountingBatch);
			}
			batch = null;
			break;
		case AccountingBatchRepository.ACTION_REMINDER:
			batch = reminder(accountingBatch);
			break;
		case AccountingBatchRepository.ACTION_INTERBANK_PAYMENT_ORDER:
			if(accountingBatch.getInterbankPaymentOrderTypeSelect() == AccountingBatchRepository.INTERBANK_PAYMENT_ORDER_TYPE_IMPORT)  {
				batch = interbankPaymentOrderImport(accountingBatch);
			}
			else if(accountingBatch.getInterbankPaymentOrderTypeSelect() == AccountingBatchRepository.INTERBANK_PAYMENT_ORDER_TYPE_REJECT_IMPORT)  {
				batch = interbankPaymentOrderRejectImport(accountingBatch);
			}
			batch = null;
			break;
		case AccountingBatchRepository.ACTION_DOUBTFUL_CUSTOMER:
			batch = doubtfulCustomer(accountingBatch);
			break;
		case AccountingBatchRepository.ACTION_ACCOUNT_CUSTOMER:
			batch = accountCustomer(accountingBatch);
			break;
		case AccountingBatchRepository.ACTION_MOVE_LINE_EXPORT:
			batch = moveLineExport(accountingBatch);
			break;
		case AccountingBatchRepository.ACTION_CREDIT_TRANSFER:
			batch = creditTransfer(accountingBatch);
			break;
		default:
			throw new AxelorException(String.format(I18n.get(IExceptionMessage.BASE_BATCH_1), accountingBatch.getActionSelect(), accountingBatch.getCode()), IException.INCONSISTENCY);
		}
		
		return batch;
	}
	
	
	public Batch reminder(AccountingBatch accountingBatch) {
		
		return Beans.get(BatchReminder.class).run(accountingBatch);
		
	}

	
	public Batch doubtfulCustomer(AccountingBatch accountingBatch) {
		
		return Beans.get(BatchDoubtfulCustomer.class).run(accountingBatch);
	}
	
	public Batch reimbursementExport(AccountingBatch accountingBatch) {
		
		return Beans.get(BatchReimbursementExport.class).run(accountingBatch);
	}
	
	public Batch reimbursementImport(AccountingBatch accountingBatch) {
		
		return Beans.get(BatchReimbursementImport.class).run(accountingBatch);
		
	}
	
	public Batch interbankPaymentOrderImport(AccountingBatch accountingBatch) {
		
		return Beans.get(BatchInterbankPaymentOrderImport.class).run(accountingBatch);
		
	}
	
	public Batch interbankPaymentOrderRejectImport(AccountingBatch accountingBatch) {
		
		return Beans.get(BatchInterbankPaymentOrderRejectImport.class).run(accountingBatch);
		
	}
	
	public Batch accountCustomer(AccountingBatch accountingBatch) {

		return Beans.get(BatchAccountCustomer.class).run(accountingBatch);

	}

	public Batch moveLineExport(AccountingBatch accountingBatch) {

		return Beans.get(BatchMoveLineExport.class).run(accountingBatch);

	}

	public Batch creditTransfer(AccountingBatch accountingBatch) {
		Class<? extends BatchStrategy> batchStrategyClass;

		switch (accountingBatch.getCreditTransferTypeSelect()) {
		case AccountingBatchRepository.CREDIT_TRANSFER_EXPENSE_PAYMENT:
			batchStrategyClass = BatchCreditTransferExpensePayment.class;
			break;
		case AccountingBatchRepository.CREDIT_TRANSFER_SUPPLIER_PAYMENT:
			batchStrategyClass = BatchCreditTransferSupplierPayment.class;
			break;
		case AccountingBatchRepository.CREDIT_TRANSFER_CUSTOMER_REIMBURSEMENT:
			switch (accountingBatch.getCustomerReimbursementTypeSelect()) {
			case AccountingBatchRepository.CUSTOMER_REIMBURSEMENT_CUSTOMER_REFUND:
				batchStrategyClass = BatchCreditTransferCustomerRefund.class;
				break;
			case AccountingBatchRepository.CUSTOMER_REIMBURSEMENT_PARTNER_CREDIT_BALANCE:
				batchStrategyClass = BatchCreditTransferPartnerReimbursement.class;
				break;
			default:
				throw new IllegalArgumentException("Unknown customer reimbursement type");
			}
			break;
		default:
			throw new IllegalArgumentException(
					String.format("Unknown credit transfer type: %d", accountingBatch.getCreditTransferTypeSelect()));
		}

		return Beans.get(batchStrategyClass).run(accountingBatch);
	}

	public Batch directDebit(AccountingBatch accountingBatch) {
		throw new UnsupportedOperationException(I18n.get("This batch requires the bank payment module."));
	}

}
