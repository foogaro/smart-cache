package com.foogaro.trc;

import com.foogaro.trc.entity.User;
import com.foogaro.trc.service.UserService;
import com.github.javafaker.Faker;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.stream.LongStream;

@Component
public class DataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //execute();
    }

    private void execute() {
        logger.info("Creating users...");
        ProgressBarBuilder progressBarBuilder = new ProgressBarBuilder();
        progressBarBuilder.setStyle(ProgressBarStyle.COLORFUL_UNICODE_BAR);
        progressBarBuilder.setInitialMax(UserService.MAX_USERID);
        progressBarBuilder.setTaskName(String.format("Creating USER"));
        progressBarBuilder.showSpeed();
        progressBarBuilder.startsFrom(0, Duration.ZERO);
        ProgressBar progressBar = progressBarBuilder.build();

        Faker faker = new Faker();
        var range = LongStream.rangeClosed(1, UserService.MAX_USERID);
        range.parallel().forEach(j -> {
            User user = new User();
            try {
                user.setName(faker.name().fullName());
                user.setEmail(faker.internet().emailAddress());
                userService.create(user);
                progressBar.stepBy(1);
            } catch (Exception e) {
                logger.error("Error while creating new user: {}", user, e);
            }
        });
        logger.info("Creating users DONE!");

        progressBar.stepTo(UserService.MAX_USERID);
        progressBar.close();
    }

}
