package org.aulich.pojo;

public class CellPojo {
    private Object v;
    private Object f;
    private Object p;
    public CellPojo(Object v) {
        this.v = v;
    }
    public CellPojo(Object v, Object f) {
        this.v = v;
        this.f = f;
    }
    public CellPojo(Object v, Object f, Object p) {
        this.v = v;
        this.f = f;
        this.p = p;
    }

    public Object getV() {
        return v;
    }

    public void setV(Object v) {
        this.v = v;
    }

    public Object getF() {
        return f;
    }

    public void setF(Object f) {
        this.f = f;
    }

    public Object getP() {
        return p;
    }

    public void setP(Object p) {
        this.p = p;
    }
}
