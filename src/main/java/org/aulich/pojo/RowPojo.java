package org.aulich.pojo;

import java.util.List;

public class RowPojo {
    private List<LinePojo> c;

    public RowPojo(List<LinePojo> c){
        this.c = c;
    }

    public List<LinePojo> getC() {
        return c;
    }

    public void setC(List<LinePojo> c) {
        this.c = c;
    }
}
