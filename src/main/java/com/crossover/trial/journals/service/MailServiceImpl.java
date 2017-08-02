package com.crossover.trial.journals.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.repository.SubscriptionRepository;

@Service
public class MailServiceImpl implements MailService {
	
	private final static Logger log = Logger.getLogger(MailServiceImpl.class);
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	private boolean sendNow() {
		log.debug("MAIL SENT");
		
		return true;
	}

	@Override
	public void sendOnPublish(Category category) {
		List<Subscription> subscriptions = subscriptionRepository.findByCategory(category);
		subscriptions.stream().forEach(s -> log.debug(s.getUser().getLoginName()));
		
		sendNow();
	}

	@Override
	//@Scheduled(cron = "0 0 4 * * ?")
	@Scheduled(cron = "*/25 * * * * ?")
	//@Async
	public void sendDaily() {
		sendNow();
	}

}
