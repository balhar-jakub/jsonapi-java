package net.balhar.jsonapi.simple;

/**
 * Created by jbalhar on 5/6/2015.
 */
public class Elf {
    private String uuid;
    private String name;

    Elf(){}

    Elf(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }
}
