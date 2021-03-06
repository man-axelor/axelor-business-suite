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
package com.axelor.apps.bankpayment.service.batch;

import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.i18n.I18n;

public abstract class BatchDirectDebit extends com.axelor.apps.account.service.batch.BatchStrategy {

	@Override
	protected void stop() {
		StringBuilder sb = new StringBuilder();
		sb.append(I18n.get(IExceptionMessage.ABSTRACT_BATCH_REPORT)).append(" ");
		sb.append(String.format(I18n.get(IExceptionMessage.ABSTRACT_BATCH_DONE_SINGULAR,
				IExceptionMessage.ABSTRACT_BATCH_DONE_PLURAL, batch.getDone()) + " ", batch.getDone()));
		sb.append(String.format(I18n.get(IExceptionMessage.ABSTRACT_BATCH_ANOMALY_SINGULAR,
				IExceptionMessage.ABSTRACT_BATCH_ANOMALY_PLURAL, batch.getAnomaly()), batch.getAnomaly()));
		addComment(sb.toString());
		super.stop();
	}

}
