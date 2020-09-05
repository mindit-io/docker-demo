package io.mindit.dockerdemo;

import io.mindit.dockerdemo.es.ElasticsearchUser;
import io.mindit.dockerdemo.es.ElasticsearchUserRepository;
import io.mindit.dockerdemo.jpa.User;
import io.mindit.dockerdemo.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/users")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElasticsearchUserRepository elasticsearchUserRepository;

    @PostMapping
    public @ResponseBody
    String addNewUser(@RequestParam String name, @RequestParam String email) {
        User n = new User();
        n.setName(name);
        n.setEmail(email);

        //we retrieve the saved user to get the database id before saving to Elasticsearch
        User savedUser = userRepository.save(n);
        elasticsearchUserRepository.save(new ElasticsearchUser(savedUser));

        return "Saved";
    }

    @GetMapping
    public @ResponseBody
    Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/count")
    public @ResponseBody
    long countUserFromDb() {
        return userRepository.count();
    }

    @GetMapping(path = "/search-count")
    public @ResponseBody
    long countUserFromEs() {
        return elasticsearchUserRepository.count();
    }

}
