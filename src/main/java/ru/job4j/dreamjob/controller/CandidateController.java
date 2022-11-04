package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.CandidateStore;

import java.time.LocalDateTime;

@Controller
public class CandidateController {

    private final CandidateStore candidateStore = CandidateStore.instOf();

    @GetMapping("/candidates")
    public String candidates(Model model) {
        model.addAttribute("candidates", candidateStore.findAll());
        return "candidates";
    }

    @GetMapping("/formAddCandidate")
    public String addCandidate(Model model) {
        model.addAttribute("candidate", new Candidate(0, "Заполните имя", "Заполните описание", LocalDateTime.now()));
        return "addCandidate";
    }

    @PostMapping("/createCandidate")
    public String createCandidate(@ModelAttribute Candidate candidate) {
        System.out.println(candidate);
        candidateStore.add(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/formUpdateCandidate/{candidateId}")
    public String formUpdateCandidate(Model model, @PathVariable("candidateId") int id) {
        model.addAttribute("candidate", candidateStore.findById(id));
        return "updateCandidate";
    }

    @PostMapping("/updateCandidate")
    public String updateCandidate(@ModelAttribute Candidate candidate) {
        candidateStore.update(candidate);
        return "redirect:/candidates";
    }

}
