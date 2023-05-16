package com.site.mySite.post.controllers;

import com.site.mySite.post.models.Post;
import com.site.mySite.post.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('permission:read')")
    public String mainPage(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("title", "Главная страница");
        model.addAttribute("posts", posts);
        return "post/main";
    }

    @GetMapping("/addNewPost")
    @PreAuthorize("hasAuthority('permission:write')")
    public String addNewPost(Model model) {
        return "post/addNewPost";
    }

    @PostMapping("/addNewPost")
    @PreAuthorize("hasAuthority('permission:write')")
    public String addNewPost(@RequestParam String title, @RequestParam String anons,
                             @RequestParam String text, Model model) {
        Post post = new Post(title, anons, text);
        postRepository.save(post);
        return "redirect:/";
    }

    @GetMapping("/post/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public String postDetails(@PathVariable (value = "id") long id, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "post/postDetails";
    }

    @GetMapping("/post/{id}/edit")
    @PreAuthorize("hasAuthority('permission:write')")
    public String postEdit(@PathVariable (value = "id") long id, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "post/postEdit";
    }

    @PostMapping("/post/{id}/edit")
    @PreAuthorize("hasAuthority('permission:write')")
    public String updatePost(@PathVariable (value = "id") long id, @RequestParam String title,
                             @RequestParam String anons, @RequestParam String text, Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setAnons(anons);
        post.setText(text);
        postRepository.save(post);
        return "redirect:/post/{id}";
    }

    @PostMapping("/post/{id}/remove")
    @PreAuthorize("hasAuthority('permission:write')")
    public String deletePost(@PathVariable (value = "id") long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
        return "redirect:/";
    }
}
