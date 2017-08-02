package com.crossover.trial.journals.service;

import java.util.List;
import java.util.Optional;

import com.crossover.trial.journals.dto.MailDTO;
import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Journal;
import com.sendgrid.Response;

public interface MailService {
	Optional<Response> sendNow(MailDTO mail);
	
	void sendOnPublish(Journal journal, Category category);
	
	void sendDaily();
	
	void sendToAllUsers(List<Journal> journalsPublishedYesterday);
}
