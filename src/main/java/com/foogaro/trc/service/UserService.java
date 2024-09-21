package com.foogaro.trc.service;

import com.foogaro.trc.entity.User;
import com.foogaro.trc.repository.UserRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final Random RANDOM = new Random();
    public static final int MAX_SLEEPY = 2;
    public static final long MAX_USERID = 1000;

    @Autowired
    private UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public Optional<User> read(Long userId) {
        return userRepository.findById(userId);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findOneSlowly() {
        int sleepy = RANDOM.nextInt(1, MAX_SLEEPY + 1);
        long userId = RANDOM.nextLong(1, MAX_USERID);
        return findOneSlowly(sleepy, userId);
    }

    public User findOneSlowly(int sleepy, long userId) {
        logger.info("Find One - sleepy: {} - userId: {}", sleepy, userId);
        return userRepository.findOneSlowly(sleepy, userId);
    }

    public void run() {
        logger.info("Executing through...");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long maxUserId = UserService.MAX_USERID;

            for (int j = 0; j < maxUserId; j++) {
                int finalJ = j;
                executor.submit(() -> {
                    try {
                        findOneSlowly(1, finalJ);
                        if (finalJ % 10 == 0) {
                            logger.info("Execute through {} users", finalJ);
                        }
                    } catch (Exception e) {
                        logger.error("Error while executing through for sleepy: 1, userId: {}", finalJ, e);
                    }
                });
                executor.submit(() -> {
                    try {
                        findOneSlowly(2, finalJ);
                        if (finalJ % 10 == 0) {
                            logger.info("Execute through {} users", finalJ);
                        }
                    } catch (Exception e) {
                        logger.error("Error while executing through for sleepy: 2, userId: {}", finalJ, e);
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Executing through...DONE!");
    }

    public void load() {
        Faker faker = new Faker();
        logger.info("Creating users...");
        var range = LongStream.rangeClosed(1, MAX_USERID);
        range.parallel().forEach(j -> {
            User user = new User();
            try {
                user.setName(faker.name().fullName());
                user.setEmail(faker.internet().emailAddress());
                create(user);
            } catch (Exception e) {
                logger.error("Error while creating new user: {}", user, e);
            }
        });
        logger.info("Creating users DONE!");
    }
}
