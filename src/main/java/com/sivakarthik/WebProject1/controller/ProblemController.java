package com.sivakarthik.WebProject1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sivakarthik.WebProject1.model.Problem;
import com.sivakarthik.WebProject1.service.ProblemService;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    //Get
    @GetMapping
    public ResponseEntity<List<Problem>> getAll() {
        return ResponseEntity.ok(problemService.getAllProblems());
    }
    @GetMapping("/cf")
    public ResponseEntity<String> getCfProblems() {
        return ResponseEntity.ok(problemService.getProblemsFromCodeforces());
    }
    @GetMapping("/{contestId}/{index}")
    public ResponseEntity<Problem> getOne(@PathVariable Integer contestId, @PathVariable String index) {
        Problem problem = problemService.getOneProblem(contestId, index);
        if (problem != null) {
            return ResponseEntity.ok(problem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/cf/tags/{tag}")
    public ResponseEntity<List<Problem>> getAllProblemsOfTag(@PathVariable String tag) {
        return ResponseEntity.ok(problemService.getAllProblemsOfTag(tag));
    }
    @GetMapping("/cf/load")
    public String loadProblems() throws Exception {
        problemService.loadProblemsFromCodeforces();
        return "Loaded Successfully";
    }

    //Post
    @PostMapping
    public ResponseEntity<Problem> create(@RequestBody Problem p) {
        return ResponseEntity.ok(problemService.createProblem(p));
    }

    //Put
    @PutMapping("/{contestId}/{index}")
    public ResponseEntity<Problem> update(@PathVariable Integer contestId, @PathVariable String index, @RequestBody Problem p) {
        Problem updated = problemService.updateProblem(contestId, index, p);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Delete
    @DeleteMapping("/{contestId}/{index}")
    public ResponseEntity<Void> delete(@PathVariable Integer contestId, @PathVariable String index) {
        boolean deleted = problemService.deleteProblem(contestId, index);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
