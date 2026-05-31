package com.sivakarthik.WebProject1.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivakarthik.WebProject1.model.Problem;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class ProblemService {

    private final RestTemplate restTemplate;
    private List<Problem> allProblems;

    public ProblemService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.allProblems = new ArrayList<>();
    }

    //C
    public Problem createProblem(Problem p){
        allProblems.add(p);
        return p;
    }

    //R
    public List<Problem> getAllProblems(){
        return allProblems;
    }
    public Problem getOneProblem(int contestId,String id){
        for(int i = 0; i < allProblems.size(); i++){
            Problem p = allProblems.get(i);
            if(p.getContestId().equals(contestId) && p.getIndex().equals(id)){
                return p;
            }
        }
        return null;
    }
    public List<Problem> getAllProblemsOfTag(String tag){
        List<Problem> problemsOfTag = new ArrayList<>();
        for(int i=0;i<allProblems.size();i++){
            Problem p = allProblems.get(i);
            if(p.getTags() != null && p.getTags().contains(tag)){
                problemsOfTag.add(p);
            }
        }
        return problemsOfTag;
    }

    //U
    public Problem updateProblem(int contestId, String id, Problem problem){
        for(int i = 0; i < allProblems.size(); i++){
            Problem p = allProblems.get(i);
            if(p.getContestId().equals(contestId) && p.getIndex().equals(id)){
                allProblems.set(i, problem);
                return problem;
            }
        }
        return null;
    }

    //D
    public boolean deleteProblem(int contestId, String id){
        for(int i = 0; i < allProblems.size(); i++){
            Problem p = allProblems.get(i);
            if(p.getContestId().equals(contestId) && p.getIndex().equals(id)){
                allProblems.remove(i);
                return true;
            }
        }
        return false;
    }

    public String getProblemsFromCodeforces() {
        String url = "https://codeforces.com/api/problemset.problems";

        return restTemplate.getForObject(url, String.class);
    }

    public void loadProblemsFromCodeforces() throws Exception {

        String url = "https://codeforces.com/api/problemset.problems";

        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response);

        JsonNode problems = root.path("result").path("problems");

        allProblems.clear();

        for (JsonNode p : problems) {

            Problem problem = new Problem();

            problem.setContestId(p.path("contestId").asInt());
            problem.setIndex(p.path("index").asText());
            problem.setName(p.path("name").asText());

            if (!p.path("rating").isMissingNode()) {
                problem.setRating(p.path("rating").asInt());
            }

            List<String> tags = new ArrayList<>();

            for (JsonNode tag : p.path("tags")) {
                tags.add(tag.asText());
            }

            problem.setTags(tags);

            allProblems.add(problem);
        }
    }

}