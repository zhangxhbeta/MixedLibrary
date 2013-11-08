package com.topdt.mobilePay.app.lifeAssistant.inter;

import com.topdt.mobilePay.app.lifeAssistant.model.PayParentOut;

public interface LifeAssistantPayInter {
	public PayParentOut getPaymentInfoMation(String paymentType,
			String paymentNumber);

	public String payFee(String paymentType, String paymentNumber,
			String paymentMoney, String paymentPassword);

}
