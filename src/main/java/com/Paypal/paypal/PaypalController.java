package com.Paypal.paypal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@Controller
public class PaypalController {

	private PaypalService paypalService;

	public PaypalController(PaypalService paypalService) {
		this.paypalService = paypalService;
	}
	
	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	@PostMapping("/payment/create")
	public RedirectView createPayment(@RequestParam("method") String method,@RequestParam("amount") String amount,
			@RequestParam("currency") String currency,@RequestParam("description") String description) throws PayPalRESTException {
		String cancelUrl = "http://localhost:8080/payment/cancel";
		String successUrl = "http://localhost:8080/payment/success";
		
		Payment payment = paypalService.createPayment(Double.valueOf(amount),
				currency,
				method,
				"sale",
				description,
				cancelUrl,
				successUrl);
		
		for(Links link: payment.getLinks()) {
			if(link.getRel().equals("approval_url")) {
				return new RedirectView(link.getHref());
			}
		}
		return new RedirectView("/payment/error");
	}
	
	@GetMapping("/payment/success")
	public String paymentSuccess(@RequestParam("paymentId") String paymentId, @RequestParam("payerId") String payerId) throws PayPalRESTException {
		
		Payment payment = paypalService.executePayment(paymentId, payerId);
		
		if(payment.getState().equals("approved")) {
			return "paymentSuccess";
		}
		
		return "paymentSuccess";
	}
	
	@GetMapping("/payment/cancel")
	public String paymentCancel() {
		return "paymentCancel";
	}
	
	@GetMapping("/payment/error")
	public String paymentError() {
		return "paymentError";
	}
}
