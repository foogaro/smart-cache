package com.foogaro.trc.controller;

import com.foogaro.trc.entity.User;
import com.foogaro.trc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path="/")
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<User> read(@PathVariable("id") Long userId) {
        return userService.read(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping(path="/")
    public ResponseEntity<User> update(@RequestBody User user) {
        return ResponseEntity.ok(userService.update(user));
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path="/")
    public @ResponseBody List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping(path="/{id}/{sleepy}")
    public ResponseEntity<User> findOneSlowly(@PathVariable("id") Long userId, @PathVariable("sleepy") int sleepy) {
        return ResponseEntity.ok(userService.findOneSlowly(sleepy, userId));
    }

    @GetMapping(path="/random")
    public ResponseEntity<User> random() {
        return ResponseEntity.ok(userService.findOneSlowly());
    }

    @GetMapping(path="/load")
    public ResponseEntity load() {
        userService.load();
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path="/run")
    public ResponseEntity run() {
        userService.run();
        return ResponseEntity.noContent().build();
    }
}
