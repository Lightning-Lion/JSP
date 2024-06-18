package com.hoarp.web.data;

public class Department {
    private String name;
    private String intro;
    private String features;
    private String research;

    public Department(String name, String intro, String features, String research) {
        this.name = name;
        this.intro = intro;
        this.features = features;
        this.research = research;
    }

    public String getName() {
        return name;
    }

    public String getIntro() {
        return intro;
    }

    public String getFeatures() {
        return features;
    }

    public String getResearch() {
        return research;
    }
}
