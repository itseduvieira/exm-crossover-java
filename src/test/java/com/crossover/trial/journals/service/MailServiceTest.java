package com.crossover.trial.journals.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.dto.MailDTO;
import com.crossover.trial.journals.service.MailService;
import com.sendgrid.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MailServiceTest {
	@Autowired
	private MailService mailService;
	
	@Test
	public void sendEmail() {
		MailDTO mailDTO = new MailDTO();
		mailDTO.setTo("eduardofelipevieira@gmail.com");
		mailDTO.setSubject("New Journal Published");
		mailDTO.setMessage("<h1>New Journal Published</h1><p>New Journal on Category</p>");
		
		Optional<Response> response = mailService.sendNow(mailDTO);
		assertTrue(response.isPresent());
		assertEquals(response.get().getStatusCode(), 202);
	}

	@Test
	public void sendDailySuccess() {
		mailService.sendDaily();
		assertTrue(true);
	}
}
