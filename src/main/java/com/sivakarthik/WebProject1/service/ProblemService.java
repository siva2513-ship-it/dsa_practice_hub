package com.sivakarthik.WebProject1.service;

import java.util.ArrayList;
import java.util.List;

import com.sivakarthik.WebProject1.model.Problem;

public class ProblemService {
    private List<Problem> allProblems;

    //C
    public ProblemService(){
        allProblems = new ArrayList<>();
    }
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

    //U
    public Problem updateProblem(int contestId, String id, Problem problem){
        for(int i = 0; i < allProblems.size(); i++){
            Problem p = allProblems.get(i);
            if(p.getContestId().equals(contestId) && p.getIndex().equals(id)){
                allProblems.set(i, problem);
                return p;
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
}

