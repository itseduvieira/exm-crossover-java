package com.crossover.trial.journals.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.repository.CategoryRepository;
import com.crossover.trial.journals.repository.SubscriptionRepository;
import com.crossover.trial.journals.service.MailService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MailServiceTest {
	@Autowired
	private MailService mailService;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void testEmailOnPublish() {
		mailService.sendOnPublish(getCategory(new Long(1)));
	}
	
	protected List<Subscription> getSubscriptions(Category category) {
		return subscriptionRepository.findByCategory(category);
	}
	
	protected Category getCategory(Long id) {
		return categoryRepository.findOne(id);
	}
}
