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

    // C
    public Problem createProblem(Problem p) {
        allProblems.add(p);
        return p;
    }

    // R
    public List<Problem> getAllProblems() {
        if (allProblems.isEmpty()) {
            try {
                loadProblemsFromCodeforces();
            } catch (Exception e) {
                System.err.println("Lazy loading failed: " + e.getMessage());
            }
        }
        return allProblems;
    }

    public Problem getOneProblem(int contestId, String id) {
        // Ensure problems are loaded
        if (allProblems.isEmpty()) {
            getAllProblems();
        }
        for (int i = 0; i < allProblems.size(); i++) {
            Problem p = allProblems.get(i);
            if (p.getContestId().equals(contestId) && p.getIndex().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public List<Problem> getAllProblemsOfTag(String tag) {
        if (allProblems.isEmpty()) {
            getAllProblems();
        }
        List<Problem> problemsOfTag = new ArrayList<>();
        for (int i = 0; i < allProblems.size(); i++) {
            Problem p = allProblems.get(i);
            if (p.getTags() != null && p.getTags().contains(tag)) {
                problemsOfTag.add(p);
            }
        }
        return problemsOfTag;
    }

    public List<Problem> getFilteredProblems(String topic, Integer ratingMin, Integer ratingMax, String difficulty,
            String search, Integer limit) {
        if (allProblems.isEmpty()) {
            getAllProblems();
        }
        List<Problem> result = new ArrayList<>();
        for (Problem p : allProblems) {
            // Filter by search name
            if (search != null && !search.isEmpty()) {
                if (p.getName() == null || !p.getName().toLowerCase().contains(search.toLowerCase())) {
                    continue;
                }
            }

            // Filter by ratingMin
            if (ratingMin != null) {
                if (p.getRating() == null || p.getRating() < ratingMin) {
                    continue;
                }
            }

            // Filter by ratingMax
            if (ratingMax != null) {
                if (p.getRating() == null || p.getRating() > ratingMax) {
                    continue;
                }
            }

            // Filter by difficulty
            if (difficulty != null && !difficulty.isEmpty()) {
                if (p.getRating() == null) {
                    continue;
                }
                int rating = p.getRating();
                if ("easy".equalsIgnoreCase(difficulty)) {
                    if (rating > 1200)
                        continue;
                } else if ("medium".equalsIgnoreCase(difficulty)) {
                    if (rating <= 1200 || rating > 1700)
                        continue;
                } else if ("hard".equalsIgnoreCase(difficulty)) {
                    if (rating <= 1700)
                        continue;
                }
            }

            // Filter by DSA Topic matching
            if (topic != null && !topic.isEmpty()) {
                if (!matchesDsaTopic(p, topic)) {
                    continue;
                }
            }

            result.add(p);
        }

        if (limit != null && limit > 0 && result.size() > limit) {
            return result.subList(0, limit);
        }
        return result;
    }

    private boolean matchesDsaTopic(Problem p, String topic) {
        if (topic == null || topic.isEmpty())
            return true;
        List<String> tags = p.getTags();
        if (tags == null)
            return false;

        String normalized = topic.toLowerCase().replace("-", " ").replace("_", " ").trim();
        switch (normalized) {
            case "dynamic programming":
            case "dp":
                return tags.contains("dp");
            case "greedy":
                return tags.contains("greedy");
            case "graphs":
                return tags.contains("graphs") || tags.contains("dfs and similar") || tags.contains("shortest paths")
                        || tags.contains("flows") || tags.contains("graph matchings");
            case "trees":
            case "binary trees":
                return tags.contains("trees");
            case "binary search":
                return tags.contains("binary search") || tags.contains("ternary search");
            case "two pointers":
                return tags.contains("two pointers");
            case "math":
                return tags.contains("math") || tags.contains("geometry");
            case "number theory":
                return tags.contains("number theory");
            case "bit manipulation":
            case "bitmasks":
                return tags.contains("bitmasks");
            case "data structures":
                return tags.contains("data structures");
            case "strings":
                return tags.contains("strings") || tags.contains("string suffix structures");
            case "sorting":
            case "sortings":
                return tags.contains("sortings");
            case "searching":
                return tags.contains("binary search") || tags.contains("ternary search")
                        || tags.contains("dfs and similar");
            case "prefix sum":
                return tags.contains("data structures") || tags.contains("implementation");
            case "hashing":
                return tags.contains("hashing");
            case "stack":
            case "queue":
            case "linked list":
                return tags.contains("data structures");
            case "recursion":
            case "backtracking":
                return tags.contains("dfs and similar") || tags.contains("brute force");
            case "bfs":
            case "dfs":
                return tags.contains("dfs and similar") || tags.contains("graphs");
            case "shortest paths":
                return tags.contains("shortest paths") || tags.contains("graphs");
            case "disjoint set union":
            case "dsu":
                return tags.contains("dsu") || tags.contains("data structures");
            case "combinatorics":
                return tags.contains("combinatorics");
            default:
                for (String t : tags) {
                    if (t.toLowerCase().contains(normalized)) {
                        return true;
                    }
                }
                return false;
        }
    }

    // U
    public Problem updateProblem(int contestId, String id, Problem problem) {
        for (int i = 0; i < allProblems.size(); i++) {
            Problem p = allProblems.get(i);
            if (p.getContestId().equals(contestId) && p.getIndex().equals(id)) {
                allProblems.set(i, problem);
                return problem;
            }
        }
        return null;
    }

    // D
    public boolean deleteProblem(int contestId, String id) {
        for (int i = 0; i < allProblems.size(); i++) {
            Problem p = allProblems.get(i);
            if (p.getContestId().equals(contestId) && p.getIndex().equals(id)) {
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

        List<Problem> tempProblems = new ArrayList<>();
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
            tempProblems.add(problem);
        }

        if (!tempProblems.isEmpty()) {
            allProblems = tempProblems;
        }
    }

}