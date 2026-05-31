package com.sivakarthik.WebProject1.model;

import java.util.List;

public class Problem {
    private Integer contestId;
    private String index;
    private String name;
    private Integer rating;
    private List<String> tags;

    //Default Constructor
    public Problem(){

    }

    //Parameterised Constructor
    public Problem(Integer contestId, String index, String name, Integer rating, List<String> tags){
        this.contestId = contestId;
        this.index = index;
        this.name = name;
        this.rating = rating;
        this.tags = tags;
    }

    //GETTERS AND SETTERS
    public Integer getContestId(){
        return this.contestId;
    }
    public String getIndex(){
        return this.index;
    }
    public String getName(){
        return this.name;
    }
    public Integer getRating(){
        return this.rating;
    }
    public List<String> getTags(){
        return this.tags;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setContestId(Integer contestId){
        this.contestId = contestId;
    }
    public void setIndex(String index){
        this.index = index;
    }
    public void setRating(Integer rating){
        this.rating = rating;
    }
    public void setTags(List<String> tags){
        this.tags = tags;
    }

}
