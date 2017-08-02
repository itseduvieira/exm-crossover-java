package com.crossover.trial.journals.service;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crossover.trial.journals.dto.MailDTO;
import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.SubscriptionRepository;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@Service
public class MailServiceImpl implements MailService {

	private final static Logger log = Logger.getLogger(MailServiceImpl.class);

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private JournalService journalService;

	@Autowired
	private UserService userService;

	@Override
	public Response sendNow(MailDTO mailDTO) {
		log.info("MAIL SENT");

		Email from = new Email("test@example.com");
		String subject = "Sending with SendGrid is Fun";
		Email to = new Email("test@example.com");
		Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
		Mail mail = new Mail(from, subject, to, content);

		SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
		Request request = new Request();
		
		Response response = null;
		
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			response = sg.api(request);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			System.out.println(response.getHeaders());
		} catch (IOException ex) {
			log.error("Error at sending email to " + mailDTO.getTo());
		}
		
		return response;
	}

	@Override
	public void sendOnPublish(Category category) {
		List<Subscription> subscriptions = subscriptionRepository.findByCategory(category);
		subscriptions.stream().forEach(s -> {
			log.info(s.getUser().getEmail());

			sendNow(new MailDTO());
		});
	}

	@Override
	// @Scheduled(cron = "0 0 4 * * ?")
	@Scheduled(cron = "0 * * * * ?")
	public void sendDaily() {
		List<Journal> journalsPublishedYesterday = journalService.listFromYesterday();

		if (!journalsPublishedYesterday.isEmpty()) {
			List<User> users = userService.findAll();
			users.stream().forEach(u -> {
				log.info(u.getEmail());

				sendNow(new MailDTO());
			});
		}
	}
}
