package com.semaai.agent.model.targettracking;

import java.io.Serializable;

public class MyTargerGoalsDescListModel implements Serializable {
    private String id;
    private String completeness;
    private String state;
    private String definitionDescription;
    private String lineId;
    private String current;
    private String targetGoal;


    public MyTargerGoalsDescListModel(String id, String completeness, String state, String definitionDescription, String lineId , String current, String targetGoal){
        this.id = id;
        this.completeness = completeness;
        this.state = state;
        this.definitionDescription = definitionDescription;
        this.lineId = lineId;
        this.current = current;
        this.targetGoal = targetGoal;
    }

    public String getTargetGoal() {
        return targetGoal;
    }

    public void setTargetGoal(String targetGoal) {
        this.targetGoal = targetGoal;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompleteness() {
        return completeness;
    }

    public void setCompleteness(String completeness) {
        this.completeness = completeness;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDefinitionDescription() {
        return definitionDescription;
    }

    public void setDefinitionDescription(String definitionDescription) {
        this.definitionDescription = definitionDescription;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}
