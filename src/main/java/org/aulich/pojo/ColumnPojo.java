package org.aulich.pojo;

public class ColumnPojo {
    private String id;
    private String label;
    private String pattern;
    private String type;
    public ColumnPojo(String id, String label, String pattern, String type){
        this.id = id;
        this.label = label;
        this.pattern = pattern;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
