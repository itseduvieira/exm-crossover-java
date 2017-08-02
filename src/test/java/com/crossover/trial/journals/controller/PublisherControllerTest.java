package com.crossover.trial.journals.controller;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.service.CurrentUser;
import com.crossover.trial.journals.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PublisherControllerTest {
    @Autowired
    private PublisherController publisherController;
    @Autowired
    private UserService userService;

    @Test
    public void handleFileUploadTest() {
        final MockMultipartFile multipartFile = new MockMultipartFile("test", new byte[]{});
        final TestingAuthenticationToken principal = new TestingAuthenticationToken(getUser("publisher1"), null);
        final RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        assertEquals("redirect:/publisher/publish", publisherController.handleFileUpload("test", 1L, multipartFile, redirectAttributes, principal));
    }

    protected CurrentUser getUser(String name) {
        Optional<User> user = userService.getUserByLoginName(name);
        if (!user.isPresent()) {
            fail(name + " doesn't exist");
        }
        return new CurrentUser(user.get());
    }
}
