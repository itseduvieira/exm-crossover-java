package com.crossover.trial.journals.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<User> getUserByLoginName(String loginName) {
		return Optional.ofNullable(userRepository.findByLoginName(loginName));
	}

	@Override
	public void subscribe(User user, Long categoryId) {
		List<Subscription> subscriptions;
		subscriptions = user.getSubscriptions();
		if (subscriptions == null) {
			subscriptions = new ArrayList<>();
		}

		if (subscriptions.stream().anyMatch(s -> s.getCategory().getId().equals(categoryId))) {
			return;
		}

		Subscription subscription = new Subscription();
		subscription.setUser(user);
		Category category = categoryRepository.findOne(categoryId);
		if(category == null) {
			throw new ServiceException("Category not found");
		}
		subscription.setCategory(category);
		subscriptions.add(subscription);
		userRepository.save(user);
	}

	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);
	}
	
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}
}
