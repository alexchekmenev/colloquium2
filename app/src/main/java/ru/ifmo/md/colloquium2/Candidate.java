package ru.ifmo.md.colloquium2;

/**
 * Created by creed on 11.11.14.
 */
public class Candidate {
    private int id;
    private String name;
    private int result;
    private boolean status;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        if (status) {
            return name+" ("+result+" votes)";
        } else {
            return name;
        }

    }
}
