package com.crossover.trial.journals.service;

import com.crossover.trial.journals.model.Category;

public interface MailService {	
	void sendOnPublish(Category category);
	
	void sendDaily();
}
