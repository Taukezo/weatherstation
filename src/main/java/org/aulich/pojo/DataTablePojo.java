package org.aulich.pojo;

import java.util.List;

public class DataTablePojo {
    private List<ColumnPojo> cols;
    private List<LinePojo> rows;
    public DataTablePojo(List<ColumnPojo> cols, List<LinePojo> rows){
        this.cols = cols;
        this.rows = rows;
    }

    public List<ColumnPojo> getCols() {
        return cols;
    }

    public void setCols(List<ColumnPojo> cols) {
        this.cols = cols;
    }

    public List<LinePojo> getRows() {
        return rows;
    }

    public void setRows(List<LinePojo> rows) {
        this.rows = rows;
    }
}
