package org.aulich.pojo;

import java.util.ArrayList;
import java.util.List;

public class LinePojo {
    private List<CellPojo> c;
    public LinePojo() {
        this.c = new ArrayList<>();
    }
    public LinePojo(List<CellPojo> c) {
        this.c = c;
    }

    public List<CellPojo> getC() {
        return c;
    }

    public void setC(List<CellPojo> c) {
        this.c = c;
    }
}
