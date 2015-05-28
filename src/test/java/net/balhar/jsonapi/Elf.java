package net.balhar.jsonapi;

/**
 * Created by jbalhar on 5/6/2015.
 */
public class Elf implements Identifiable {
    public static final long COUNTER = 1;
    private String uuid;
    private String name;

    public Elf(){}

    public Elf(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }
}
