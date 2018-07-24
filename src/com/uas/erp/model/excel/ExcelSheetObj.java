package com.uas.erp.model.excel;


public class ExcelSheetObj {
	
    private String name;	
	private Integer id;
	private Boolean actived;
	private Boolean hidden;
    private String color;
    private Integer width;
    private Integer maxRow;
    private Integer maxCol;
    private Integer totalCellNum;
    private String extraInfo;
    private Boolean allCellDataLoaded;
    private Integer tabOrder;
	
	public ExcelSheetObj(ExcelSheet tab) {
		this.name = tab.getsheetname();
		this.id = tab.getsheetid();
		this.actived = tab.getsheetactive();
		this.hidden = tab.getsheethidden();
        this.color = tab.getsheetcolor();
        this.extraInfo = tab.getsheetextrainfo();
        this.width = tab.getsheetwidth();
        this.tabOrder = tab.getsheetorder();
	}
	
	public ExcelSheetObj(String name, Integer id, boolean actived) {
		this.name = name;
		this.id = id;
		this.actived = actived;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getActived() {
		return actived;
	}

	public void setActived(Boolean actived) {
		this.actived = actived;
	}

    public Integer getMaxRow() {
        return maxRow;
    }
    
    public void setMaxRow(Integer maxRow) {
        this.maxRow = maxRow;
    }
    
    public Integer getMaxCol() {
        return maxCol;
    }
    
    public void setMaxCol(Integer maxCol) {
        this.maxCol = maxCol;
    }
    
    public Integer getTotalCellNum() {
        return totalCellNum;
    }
    
    public void setTotalCellNum(Integer totalCellNum) {
        this.totalCellNum = totalCellNum;
    }

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

    public Boolean getAllCellDataLoaded() {
        return allCellDataLoaded;
    }

    public void setAllCellDataLoaded(Boolean allCellDataLoaded) {
        this.allCellDataLoaded = allCellDataLoaded;
    }

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Integer getTabOrder() {
		return tabOrder;
	}

	public void setTabOrder(Integer tabOrder) {
		this.tabOrder = tabOrder;
	}
	
	
}

