package com.Paypal.paypal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	public RedirectView createPayment() throws PayPalRESTException {
		String cancelUrl = "http://localhost:8080/payment/cancel";
		String successUrl = "http://localhost:8080/payment/success";
		
		Payment payment = paypalService.createPayment(85.12,
				"USD",
				"paypal",
				"sale",
				"Payment description",
				cancelUrl,
				successUrl);
		
		for(Links link: payment.getLinks()) {
			if(link.getRel().equals("approval_url")) {
				return new RedirectView(link.getHref());
			}
		}
		return new RedirectView("/payment/error");
	}
}
