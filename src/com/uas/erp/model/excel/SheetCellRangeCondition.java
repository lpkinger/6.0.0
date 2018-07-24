package com.uas.erp.model.excel;

public class SheetCellRangeCondition{
    private final int tabId;
    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;

    public SheetCellRangeCondition(int tabId, int startRow, int startCol, int endRow, int endCol) {
        super();
        this.tabId = tabId;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
    }

    public int getTabId() {
        return tabId;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getEndCol() {
        return endCol;
    }
}
