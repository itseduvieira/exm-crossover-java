package com.crossover.trial.journals.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	public Optional<Response> sendNow(MailDTO mailDTO) {
		Email from = new Email(mailDTO.getFrom());
		String subject = mailDTO.getSubject();
		Email to = new Email(mailDTO.getTo());
		Content content = new Content("text/html", mailDTO.getMessage());
		Mail mail = new Mail(from, subject, to, content);

		SendGrid sg = new SendGrid("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");
		Request request = new Request();
		
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			log.info(response.getStatusCode());
			log.info(response.getBody());
			log.info(response.getHeaders());
			return  Optional.ofNullable(response);
		} catch (IOException ex) {
			log.error("Error at sending email to " + mailDTO.getTo(), ex);
			return Optional.empty();
		}
	}

	@Override
	public void sendOnPublish(Journal journal, Category category) {
		List<Subscription> subscriptions = subscriptionRepository.findByCategory(category);
		subscriptions.stream().forEach(s -> {
			log.info("Sending email of new publish " + category.getName() + 
					" to " + s.getUser().getEmail());
			
			MailDTO mailDTO = new MailDTO();
			mailDTO.setTo(s.getUser().getEmail());
			mailDTO.setSubject("New Journal Published");
			mailDTO.setMessage("<h1>New Journal Published</h1><p>" + 
					journal.getName() + " on " + category.getName() + "</p>");

			Optional<Response> response = sendNow(mailDTO);
			if(response.isPresent()) {
				log.debug("Status code " + response.get().getStatusCode());
			}
		});
	}

	// Run every 4AM
	@Override
	@Scheduled(cron = "0 0 4 * * ?")
	public void sendDaily() {
		sendToAllUsers(journalService.listFromYesterday());
	}
	
	@Override
	public void sendToAllUsers(List<Journal> journalsPublishedYesterday) {
		if (journalsPublishedYesterday.isEmpty()) {
			log.debug("There is no new journals to send");
			return;
		}

		MailDTO mailDTO = new MailDTO();

		StringBuilder message = new StringBuilder();
		message.append("<h1>Daily Digest</h1><p>List of new journals:</p><ul>");

		journalsPublishedYesterday.forEach(j ->
			message.append("<li>" + j.getName() + "</li>"));

		message.append("</ul>");

		mailDTO.setSubject("New Journals Published");
		mailDTO.setMessage(message.toString());

		List<User> users = userService.findAll();
		users.stream().forEach(u -> {
			log.info("Sending digest email to " + u.getEmail());
			log.info(message.toString());

			mailDTO.setTo(u.getEmail());

			Optional<Response> response = sendNow(mailDTO);
			if(response.isPresent()) {
				log.debug("Status code " + response.get().getStatusCode());
			}
		});
	}
}
