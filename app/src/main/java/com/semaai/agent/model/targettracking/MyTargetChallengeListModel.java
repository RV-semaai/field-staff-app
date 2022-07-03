package com.semaai.agent.model.targettracking;

import java.io.Serializable;

public class MyTargetChallengeListModel implements Serializable {
    private String id;
    private String name;
    private String targetGoal;
    private String definitionFullSuffix;
    private String challengeId;

    public MyTargetChallengeListModel(){

    }

    public MyTargetChallengeListModel(String id, String name, String targetGoal, String definitionFullSuffix, String challengeId){
        this.id = id;
        this.name = name;
        this.targetGoal = targetGoal;
        this.definitionFullSuffix = definitionFullSuffix;
        this.challengeId = challengeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetGoal() {
        return targetGoal;
    }

    public String getDefinitionFullSuffix() {
        return definitionFullSuffix;
    }


}
