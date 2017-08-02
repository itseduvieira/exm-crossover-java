package com.crossover.trial.journals.service;

import com.crossover.trial.journals.dto.MailDTO;
import com.crossover.trial.journals.model.Category;
import com.sendgrid.Response;

public interface MailService {
	Response sendNow(MailDTO mail);
	
	void sendOnPublish(Category category);
	
	void sendDaily();
}
