package com.example.fileshare;

public class Files {


    public String name,link,type;


    public Files(){

    }

    public Files(String name, String link, String type) {
        this.name = name;
        this.link = link;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
