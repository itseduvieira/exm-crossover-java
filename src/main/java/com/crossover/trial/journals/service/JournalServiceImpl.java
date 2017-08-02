package com.crossover.trial.journals.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.crossover.trial.journals.controller.PublisherController;
import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.CategoryRepository;
import com.crossover.trial.journals.repository.UserRepository;
import com.crossover.trial.journals.model.Subscription;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.crossover.trial.journals.repository.JournalRepository;

@Service
public class JournalServiceImpl implements JournalService {

	private final static Logger log = Logger.getLogger(JournalServiceImpl.class);

	@Autowired
	private JournalRepository journalRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private MailService mailService;

	@Override
	public List<Journal> listAll(User user) {
		User persistentUser = userRepository.findOne(user.getId());
		List<Subscription> subscriptions = persistentUser.getSubscriptions();
		
		if (subscriptions != null) {
			List<Long> ids = new ArrayList<>(subscriptions.size());
			subscriptions.stream().forEach(s -> ids.add(s.getCategory().getId()));
			
			return journalRepository.findByCategoryIdIn(ids);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<Journal> publisherList(Publisher publisher) {
		Iterable<Journal> journals = journalRepository.findByPublisher(publisher);
		return StreamSupport.stream(journals.spliterator(), false).collect(Collectors.toList());
	}

	@Override
	public Journal publish(Publisher publisher, Journal journal, Long categoryId) throws ServiceException {
		Category category = categoryRepository.findOne(categoryId);
		
		if(category == null) {
			throw new ServiceException("Category not found");
		}
		
		journal.setPublisher(publisher);
		journal.setCategory(category);
		
		try {
			Journal result = journalRepository.save(journal);
			
			mailService.sendOnPublish(journal, category);
			
			return result;
		} catch (DataIntegrityViolationException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public void unPublish(Publisher publisher, Long id) throws ServiceException {
		Journal journal = journalRepository.findOne(id);
		
		if (journal == null) {
			throw new ServiceException("Journal doesn't exist");
		}

		if (!journal.getPublisher().getId().equals(publisher.getId())) {
			throw new ServiceException("Journal cannot be removed");
		}
		
		String filePath = PublisherController.getFileName(publisher.getId(), journal.getUuid());
		File file = new File(filePath);
		
		if (file.exists()) {
			boolean deleted = file.delete();
			
			if (!deleted) {
				log.error("File " + filePath + " cannot be deleted");
			}
		}
		
		journalRepository.delete(journal);
	}
	
	@Override
	public List<Journal> listFromYesterday() {
		Calendar yesterdayStart = new GregorianCalendar();
		yesterdayStart.set(Calendar.HOUR_OF_DAY, 0);
		yesterdayStart.set(Calendar.MINUTE, 0);
		yesterdayStart.set(Calendar.SECOND, 0);
		yesterdayStart.set(Calendar.MILLISECOND, 0);
		yesterdayStart.add(Calendar.DATE, -1);
		
		Calendar yesterdayEnd = new GregorianCalendar();
		yesterdayEnd.set(Calendar.HOUR_OF_DAY, 0);
		yesterdayEnd.set(Calendar.MINUTE, 0);
		yesterdayEnd.set(Calendar.SECOND, 0);
		yesterdayEnd.set(Calendar.MILLISECOND, 0);
		yesterdayEnd.add(Calendar.MILLISECOND, -1);

		return journalRepository.findByPublishDate(yesterdayStart.getTime(), yesterdayEnd.getTime());
	}
}
