package com.Paypal.paypal;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PaypalService {

	private APIContext apiContext;
	
	public PaypalService(APIContext apiContext) {
		this.apiContext = apiContext;
	}
	
//----->Crea el pago	
	public Payment createPayment(Double total,String currency,String method, String intent, String description,
			String cancelUrl, String successUrl) throws PayPalRESTException {
		
		Amount amount = new Amount();
		
		amount.setCurrency(currency);
		amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total));
		
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);
		
		List<Transaction> transactions = new LinkedList<>();
		transactions.add(transaction);
		
		Payer payer = new Payer();
		payer.setPaymentMethod(method);
		
		Payment payment = new Payment();
		payment.setIntent(intent);
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		
		payment.setRedirectUrls(redirectUrls);
		
		return payment.create(apiContext);
	}
	
//----->Ejecuta el pago
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
		
		Payment payment = new Payment();
		payment.setId(paymentId);
		
		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payerId);
		
		return payment.execute(apiContext, paymentExecution);
	}
}
