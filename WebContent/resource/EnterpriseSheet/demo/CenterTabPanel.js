/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need have a valid license key before you can access this piece 
 * of code.
 */
Ext.define('enterpriseSheet.demo.CenterTabPanel', {
	
	extend : 'Ext.Panel',	
	region: 'center',
	layout : 'fit',
    defaults: {
        flex: 0.8
    },
	
	initComponent : function(){
		
		this.items = [SHEET_API_HD.appCt];
			
		this.callParent();	
	},
	
	updateSheet : function(itemId, titleTxt, exampleJson) {
		var json = null, url = "http://www.enterprisesheet.com/api/docs/introduce.html";
		
		if (exampleJson) json = JSON_DATA[exampleJson];		
		SHEET_API.loadData(SHEET_API_HD, json);
		
		if (itemId == "feature-disableSheet")  SHEET_API.setReadOnly(SHEET_API_HD, true); 
		else SHEET_API.setReadOnly(SHEET_API_HD, false); 
		
		if (itemId == "feature-addWidget") {	
			var wedgitJson = {
			    ftype: 'wedgit',
			    url: 'http://www.enterpriseSheet.com'
			};
			var store = SHEET_API_HD.store;
			SHEET_API.insertFloatingItem(SHEET_API_HD, store.getActivedSheetId(), wedgitJson);

        } else if(itemId == "feature-addPicture") {
			var picJson = {
				ftype: 'picture',
				url: 'http://www.google.com/images/srpr/logo11w.png',
				width: 300,
				height: 80,
				x: 400,
				y: 160
			};
			var store = SHEET_API_HD.store;
			SHEET_API.insertFloatingItem(SHEET_API_HD, store.getActivedSheetId(), picJson);
			
			// add icp for inner image ...
			var cells = [{row: 4, col: 2, json: {icp:"http://www.google.com/images/srpr/logo11w.png", imgStyle: "position:absolute;left:5px;top:5px;width:50%;height:50%;", data:""}} ];
			SHEET_API.updateCells(SHEET_API_HD, cells);
			
		} else if ( itemId.lastIndexOf("feature-condition-", 0) === 0 ) {
			SHEET_API.setCondition(SHEET_API_HD, [[1, 25, 3, 27, 5]], 'boolstyle', {
			     type:"max",
			     base: 10,
			     style:{"cbgc":"rgb(248,105,107)","ccolor":"rgb(150,0,0)"} 
			});
		} 
	},
	
	// for name mgr -----
	handleNameMgr : function(itemId, titleTxt, exampleJson) {
		var json = null, url = "http://www.enterprisesheet.com/api/docs/introduce.html";
		
		if (exampleJson) json = JSON_DATA[exampleJson];		
		SHEET_API.loadData(SHEET_API_HD, json);	
		SHEET_API.setReadOnly(SHEET_API_HD, false); 	
		
		var result = SHEET_API.getDefinedNameContent(SHEET_API_HD, "rangeData");
		console.log("SHEET_API.getDefinedNameContent(SHEET_API_HD, 'rangeData') = " + Ext.encode(result));
	},
	
	// for name mgr -----
	handleNameMgrAdd : function(itemId, titleTxt, exampleJson) {
		var json = null;
		
		if (exampleJson) json = JSON_DATA[exampleJson];		
		
		SHEET_API.loadData(SHEET_API_HD, json);	
		SHEET_API.setReadOnly(SHEET_API_HD, false); 	
		SHEET_API.createNamedRange(SHEET_API_HD, {name: 'ADDED', rangeOfAddress: "'Sheet1'!$B$4:$C$6"});
		var result = SHEET_API.getDefinedNameContent(SHEET_API_HD, "ADDED");
		console.log("SHEET_API.getDefinedNameContent(SHEET_API_HD, 'ADDED') = " + Ext.encode(result));
		SHEET_API.updateCells(SHEET_API_HD, [{
			sheet: 1,
			row: 10,
			col: 3,
			json: {
				data: '=sum(ADDED)'
			}
		}]);		
	},
	
	handleNameMgrDel : function() {
		alert("Please click 'name mgr add' first if you did not this.");
		SHEET_API.setReadOnly(SHEET_API_HD, false); 
		SHEET_API.deleteNamedRangeByName(SHEET_API_HD, "ADDED");
	},
	
	sheetTabHandler : function(itemId, titleTxt, exampleJson) {
		
		if (itemId == "feature-sheet-add") {
			var newTab = {
			    name: 'sheet5',
				color: 'red'
			};
			
	        SHEET_API.addSheetTab(SHEET_API_HD, newTab, function(sheetId) {
			    alert("ok, sheet tab is added with id =" + sheetId);
			    var cells = [{ sheet: sheetId, row: 2, col: 2, json: { data: "Add data to the new tab"}}]; 
			    SHEET_API.updateCells(SHEET_API_HD, cells);
			}, this);
	        
		} else if (itemId == "feature-sheet-retrieve") {
			var json = SHEET_API.getSheetTabData(SHEET_API_HD);
    	    alert(Ext.encode(json));
		} else if (itemId == "feature-sheet-freeze") {			
			SHEET_API.freezeSheet(SHEET_API_HD, 2, 2);			
		} else if (itemId == "feature-sheet-applyBorder") {
			
			var json = JSON_DATA["emptyOneSheetJson"];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 2, 2, 4, 5]], {position: "all", color:"red", width:2} );
			   	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 6, 2, 8, 5]], {position:"topbottom", color:"blue", width:3, lineType:"dashed"} );
			   	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 10, 2, 12, 5]], {position:"outside", color:"orange", width:2, lineType:"dotted"} );
			   	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 14, 2, 16, 5]], {position:"bottom", color:"red", width:2, lineType:"solid"} );
			   	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 18, 2, 20, 2]], {position:"left", color:"purple"} );
		    }, this);	
		    
		} else if (itemId == "feature-sheet-group") {			
			var json = JSON_DATA["featureGroup"];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.updateGroups(SHEET_API_HD, 
		    		[{dir: 'row', start: 2, end: 10}, {dir: 'row', start: 1, end: 17}]
		    	);
		        SHEET_API.cancelGroup(SHEET_API_HD, "row", 18, 19);
		    }, this);			    
		} 
		
	},
	
	specialHandler : function(itemId, titleTxt, exampleJson) {
		
		if (itemId == "feature-special-table") {	
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.applyTableTpl(SHEET_API_HD, 1, 16, 6, 19, 8, "tpl_50", false);
		    }, this);			    
		} else if (itemId == "feature-special-addNewRow") {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.insertRow(SHEET_API_HD, 1, 6, 2);
		    }, this);
		} else if (itemId == "feature-special-addNewCol") {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.insertColumn(SHEET_API_HD, 1, 7, 2);
		    }, this);
		} else if (itemId == "feature-special-setrowcolnumber") {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.setMaxRowNumber(30);
			    SHEET_API.setMaxColNumber(15);
		    }, this);
			
		} else if (itemId == "feature-special-comment") {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	var cellsJson = [
				    { sheet: 1, row: 9, col: 2, json: { comment: 'Great work', commentEdit: 'hide' }, applyWay: 'apply' }
				];
		    	SHEET_API.updateCells(SHEET_API_HD, cellsJson);
		    	
		    	//SHEET_API.deleteCommentForCoord(SHEET_API_HD, [1, 9, 2, 9, 2]);    	
		    	//SHEET_API.mergeCellForSpan(SHEET_API_HD, [1, 11, 2, 12, 5]);
		    	
		    	// get all comments ...
		    	console.log(SHEET_API.getCellsComment(SHEET_API_HD));	
		    	console.log(SHEET_API.getCellsComment(SHEET_API_HD, 2));
		    	
		    }, this);
		    
		} else if (itemId.lastIndexOf("feature-special-dataBindingVariable") == 0) {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.applyTableTpl(SHEET_API_HD, 1, 4, 4, 5, 5, "tpl_29", false);
		    	SHEET_API.applyTableTpl(SHEET_API_HD, 1, 7, 2, 7, 2, "tpl_29", false);
		    	SHEET_API.applyTableTpl(SHEET_API_HD, 1, 13, 2, 20, 5, "tpl_29", false);
		    	SHEET_API.toggleGridLine(SHEET_API_HD, true);
		    }, this);
		} else if ( itemId.lastIndexOf("feature-special-filter") == 0 ) {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	// SHEET_API.applyTableTpl(SHEET_API_HD, 1, 4, 2, 8, 4, "tpl_29", true);
		    }, this);
		} else if ( itemId.lastIndexOf("feature-special-tabs-filter") == 0 ) {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.addFilter2Span(SHEET_API_HD, [1, 3, 6, 0, 6], {
		    		6 : {
		    			'type': 'match',
		    			'values': ['test']
		    		}
		    	}, false);
		    	SHEET_API.addFilter2Span(SHEET_API_HD, [2, 3, 7, 0, 7], {
		    		7 : {
		    			'type': 'match',
		    			'values': ['ok']
		    		}
		    	}, false);
		    }, this);
		    
		    var sheet = SHEET_API_HD.sheet; 
		    sheet.on({       
		    	scope: this,       
		    	'switchsheet': function(oldSheetId, sheetId) {                 
		    		if(sheetId != oldSheetId) {
						SHEET_API.removeFilter(SHEET_API_HD, oldSheetId, false);
					}
		    	} 
		    });				
		} else if (itemId.lastIndexOf("feature-special-survey") == 0) {
			var json = JSON_DATA[exampleJson];		
		    SHEET_API.loadData(SHEET_API_HD, json, function() {
		    	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 3, 2, 3, 10]], {position:"bottom", color:"grey", width:2, lineType:"solid"} );
		    	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 9, 4, 9, 8]], {position:"outside", color:"green", width:1, lineType:"solid"} );
		    	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 12, 2, 12, 6]], {position:"outside", color:"green", width:1, lineType:"solid"} );
		    	SHEET_API.applyCellsBorder(SHEET_API_HD, [[1, 15, 2, 15, 6]], {position:"outside", color:"green", width:1, lineType:"solid"} );
		    	SHEET_API.toggleGridLine(SHEET_API_HD, true, 1);
		    	SHEET_API.toggleGridLine(SHEET_API_HD, true, 2);
		    	SHEET_API.toggleGridLine(SHEET_API_HD, true, 3);
		    	SHEET_API.freezeSheet(SHEET_API_HD, 4, 0);
		    	SHEET_API.applyTableTpl(SHEET_API_HD, 2, 5, 2, 9, 7, "tpl_26", false);
		    	SHEET_API.applyCellsBorder(SHEET_API_HD, [[3, 5, 2, 12, 12]], {position:"outside", color:"green", width:1, lineType:"solid"} );
		    }, this);
		} 
		
	},
	
	/**
	 * load from user ...
	 */
	loadUserCases : function(itemId) {
		
		var copyFromId = "";
		if (itemId == "cases-feature") {
			copyFromId = "HIgdywyToVE_";  
		} else if (itemId == "cases-calendar") {
			copyFromId = "VBi7xLabN*0_"; 
		} else if (itemId == "cases-expense") {
			copyFromId = "kpmIajRcY9U_"; 
		} else if (itemId == "cases-wedding") {
			copyFromId = "9sIewMSyXyI_"; 
		} else if (itemId == "cases-garden") {
			copyFromId = "9t1Z80Ys0tg_"; 
		} else if (itemId == "cases-weight") {
			copyFromId = "Cite8RW1D7Y_"; 
		} 

		var loadingMask = new Ext.LoadMask({target:Ext.getBody(), msg: SLANG['waiting']});
        loadingMask.show();
        
		Ext.Ajax.request({
			url: SCONFIG.urls['copyFromTpl'],
			params: {
				tplFileId: copyFromId
			},
			success: function(response, options){
				var json = Ext.decode(response.responseText);
				if(true == json.success || "true" == json.success){
					loadingMask.hide();
					SHEET_API.loadFile(SHEET_API_HD, json.fileId);
				}
			},
			failure: function(){	
				loadingMask.hide();
			},
			scope: this
		});
		
		if (this.items.getAt(1)) this.remove(this.items.getAt(1));
		
	},
	
	/**
	 * this is for title bar action 
	 */
	titlebarAction : function(itemId, titleTxt) {
		
		if (itemId == "feature-titlebar-toggleGridLine") {	
			SHEET_API.toggleGridLine(SHEET_API_HD, true);
		} else if (itemId == "feature-titlebar-chart") {	
			SHEET_API.showSidebarBtnWin(SHEET_API_HD, "chart");
		} else if (itemId == "feature-titlebar-table") {	
			SHEET_API.showSidebarBtnWin(SHEET_API_HD, "tableStyle");
		} else if (itemId == "feature-titlebar-cellStyle") {	
			SHEET_API.showSidebarBtnWin(SHEET_API_HD, "cellStyle");
		} else if (itemId == "feature-titlebar-search") {	
			SHEET_API.showSidebarBtnWin(SHEET_API_HD, "search");
		} else if (itemId == "feature-titlebar-picture") {	
			SHEET_API.showSidebarBtnWin(SHEET_API_HD, "picture");
		} else if (itemId == "feature-titlebar-widget") {	
			SHEET_API.showSidebarBtnWin(SHEET_API_HD, "widget");
		} else if (itemId == "feature-titlebar-condition") {	
			SHEET_API.showSidebarBtnWin(SHEET_API_HD, "condition");
		} else if (itemId == "feature-titlebar-freeze") {
			SHEET_API.toggleFreeze(SHEET_API_HD);
		}
	},
	
	/**
	 * this is for add customized formula
	 */
	customizedFormula : function(itemId, titleTxt, exampleJson) {	
		var json = JSON_DATA[exampleJson];		
		SHEET_API.loadData(SHEET_API_HD, json);
		
		SHEET_API.addCalculates(JSON_DATA.featureCusomizedFormula);
		
		var cells = [{ sheet: 1, row: 8, col: 3, json: { data: "=customerCal1(B4,B5,B6)", cal:true }}];
	    SHEET_API.updateCells(SHEET_API_HD, cells);	
	},
	
	/**
	 * Test performance
	 */
	performanceSheet : function(itemId, titleTxt) {
		
		var loadingMask = new Ext.LoadMask({target:Ext.getBody(), msg: SLANG['waiting']});
        loadingMask.show();
        
        var callback = function(){
        	loadingMask.hide();	
        };
		
		var xrow = 1000, yCol = 10;
		var tabName = xrow * 10;
		
		if (itemId == "performance-render-50000") {	
			xrow = 5000;
		} else if (itemId == "performance-render-100000") {	
			xrow = 10000;
		} else if (itemId == "performance-render-60-200") {	
			xrow = 60;
			yCol = 200;
			tabName = 60 * 200;
		}
			
		var cells = [], count = 0, addFlag = false;
		
		if (itemId == "performance-render-60-200") {		
			var hideCol = { sheet: 1, row: 0, col: 2, json: { hidden: "true"} };
			var row1Height = {sheet: 1, row: 1, col: 0, json: {height: 100} }; 
			var hideRow0 = { sheet: 1, row: 15, col: 0, json: { hidden: "true"} };
			var hideRow1 = { sheet: 1, row: 20, col: 0, json: { hidden: "true"} };
			var hideRow2 = { sheet: 1, row: 30, col: 0, json: { hidden: "true"} };
			var hideRow3 = { sheet: 1, row: 40, col: 0, json: { hidden: "true"} };
			cells.push(hideCol);
			cells.push(row1Height);
			cells.push(hideRow0);
			cells.push(hideRow1);
			cells.push(hideRow2);
			cells.push(hideRow3);
		}
		
        for(var i = 1; i <= xrow; i++){
            for(var j = 1; j <= yCol; j++){
			    var data = i*j;	
			    
			    if (itemId == "performance-render-60-200") {
			    	if (i == 1) {
			    		cells.push({sheet:1, row:i, col:j, json:{data: "Text " + data, rotation: 270}});
			    	} else {
			    	    cells.push({sheet:1, row:i, col:j, json:{data: data}});	
			    	}
				    count++;
			    } else {
				    cells.push({sheet:1, row:i, col:j, json:{data: data}});			    
				    count++;
			    }
			    
			    if (count > 3000) {
			    	if (!addFlag) {
		                SHEET_API.loadData(SHEET_API_HD, {
							fileName: titleTxt,
							sheets: [{ name: tabName, id: 1}],
							floatings: [
						       { sheet:1, name:"rowGroups", ftype:"rowgroup", json: "[{level:3, span:[5, 920]},{level:2, span:[7,170]},{level:1, span:[11,150]}]" },
						       { sheet:1, name:"colGroups", ftype:"colgroup", json: "[{level:1, span:[15, 60]}]" }
						    ],
							cells: cells
						}, callback, this);
		                addFlag = true;
			    	} else {
			    		SHEET_API.loadMoreData(SHEET_API_HD, {
							cells: cells
						}, callback, this);
			    	}
	                cells = [];
	                count = 0;
			    }
            }
        }
        
        if (count > 0) {
        	if (!addFlag) {
                SHEET_API.loadData(SHEET_API_HD, {
					fileName: titleTxt,
					sheets: [{ name: tabName, id: 1}],
					cells: cells
				}, callback, this);
	    	} else {
	    		SHEET_API.loadMoreData(SHEET_API_HD, {
					cells: cells
				}, callback, this);
	    	}
            cells = [];
        }
        
        // now add more condition ...
        if (itemId == "performance-render-60-200") {     	
        	SHEET_API.freezeSheet(SHEET_API_HD, 2, 2);
        	SHEET_API.hideColumn(SHEET_API_HD, 16, 55, 1);
        }
		
	},
	
	performanceSheetTab : function(itemId, titleTxt) {
		
		var loadingMask = new Ext.LoadMask({target:Ext.getBody(), msg: SLANG['waiting']});
        loadingMask.show();
        
        var callback = function(){
        	loadingMask.hide();	
        };
        
        var cells = [];
        cells.push({sheet:1, row:2, col:2, json:{data: "Click Second tab to load data in tab 2", fw:"bold" }});	
        for(var i = 10; i <= 30; i++){
	        for(var j = 1; j <= 100; j++){
	        	var data = i + j;
	        	cells.push({sheet:1, row:i, col:j, json:{data: data}});		
	        }
        }
        cells.push({sheet:1, row:2, col:j, json:{data: data}});

        SHEET_API.loadData(SHEET_API_HD, {
			fileName: titleTxt,
			sheets: [{ name: "First tab", id: 1, color: 'red'}, { name: "Second Tab", id: 2, color: 'green'}],
			floatings: [ { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" } ],
			cells: cells
		}, callback, this);  
        
        // ===========================================================================================
        
        // listen to tab switch action
        var cells2 = [];
        
        // Please define call back function in here ...
        var callbackFnForTab2 = function() {
        	// do nothing ...
        };
        
        var sheet = SHEET_API_HD.sheet;
        TAB2_DATA_LOADED = false;
		sheet.on({
		    scope: this,
		    'switchsheet': function(oldSheetId, sheetId) {
		    	if (sheetId == 2 && !TAB2_DATA_LOADED) {
		    		// alert("call backend to load tab2 data during tab switch.");
		    		TAB2_DATA_LOADED = true;
    		
		    		Ext.Ajax.request({
					    url: 'surveyDemo/getCells',
					    success: function(response){
					        var objs = Ext.decode(response.responseText);
					        console.log(objs);
					        for (var i=0; i<objs.cellObjs.length; i++) {
					        	var result = objs.cellObjs[i];
					            cells2.push({sheet: result.sheet, row: result.row, col: result.col, json: result.json });
					        }				        
					        cells2.push({sheet:2, row:23, col:1, json:{data: '=sum(1,2,3)', cal: true }});
					        
					        SHEET_API.loadTabData(SHEET_API_HD, {
				    			floatings: [
				    			    { sheet:2, name:"merge21", ftype:"meg", json:"[2, 2, 4, 4]" },
				    			    { sheet:2, name:"merge22", ftype:"meg", json:"[2, 6, 6, 8]" },
							    ],
				    			cells: cells2
				    		}, callbackFnForTab2, this);
					        
					    }
					});	  		
		    	}
		    }
		});	
	},
	
	
	/**
	 * import a xlsx file
	 */
	restApiHandler : function(itemId) {
		//var url = "http://localhost:8080/sheet/js/cubedrive/demo/resources/html/REST/RestImportXslx.html";
	    var url = "http://www.enterprisesheet.com/sheet/js/EnterpriseSheet/demo/resources/html/REST/RestImportXslx.html";
		var myWindow = window.open(url);
	},
	
	/**
	 * this is for out link ..
	 */
	outLink : function(itemId) {
		window.location.href = "http://www.enterprisesheet.com/api";
	},
	
	/**
	 * this is for define column data type
	 */
	colDataTypeHandler : function(itemId, titleTxt, exampleJson) {	
		var json = JSON_DATA[exampleJson];		
		SHEET_API.loadData(SHEET_API_HD, json);
		SHEET_API.setFocus(SHEET_API_HD, 2, 1);
	},
	
	// this is the case for recalculate formula ....
	formulaRefreshExample : function(){
		var json = JSON_DATA["emptyTwoSheetJson"];		
		var sheet = SHEET_API_HD.sheet, store = sheet.getStore();	
		
		window['REFRESH_FORMULA_FN'] = function(){
			/*
			 * here we just update the data directly, which won't cause recalculate
			 */
			var cellJson = store.getCell(1, 1, 1);
			cellJson.data = 999;
			
			/*
			 * here we have 2 ways to recalculate
			 * 1. if we know which cell is changed, we can get all related formula cells and recalulate them
			 */
			/*
			var changes = {}, pos = [1, 1, 1];
			changes[pos.join(store.separateChar)] = pos;
			//this function will find all related formula cells of the passed changed cells lookup
			var cals = sheet.findDirectRecalculateCellsForCells(changes);
			//here we call refreshRange and pass second param as the formula cells we want to recalculate			 
			sheet.refreshRange(undefined, cals);
			*/
			/*
			 * 2. if we don't know which cell is changed, or we just want to simply recalculate all formula cells
			 * we just call refreshRange to recalculate all formulas by set the second param as true
			 */
			sheet.refreshRange(undefined, true);
		}
		
	    SHEET_API.addCalculates({   
	    	
	    	'getAbacusValue': {
	            fn: function(){	            	
	                return Math.floor(Math.random()*100+100);
	            }
	        }
	    });
		
	    SHEET_API.loadData(SHEET_API_HD, json, function() {
	    	sheet.updateCells([{
		    	 sheet:1,
		    	 row:4,
		    	 col:3,
		    	 json:{
		    		 data: "Refresh Formula", 
		    		 it: "button", 
		    		 btnStyle: "color:#FFF; background-color:#900;font-size:105%;cursor:pointer;", 
		    		 onBtnClickFn: "REFRESH_FORMULA_FN"        
		    	 }
	    	}]);
	    }, this);	
	},
	
	getAllRangeMeTreeRefered : function() {
		var json = {
	    	fileName: 'GetAllRef',
            sheets: [ {name: 'Comments', id: 1, color: 'red' }, {name: 'Comment2', id: 2, color: 'red' }],
            floatings: [
               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
            ],
            cells: [
                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
                {sheet: 1, row: 0, col: 2, json: {width: 300}},
	            {sheet: 1, row: 2, col: 2, json: {data: "Get all reference in tree structure", fw:"bold", fz:14 }},
     
	            { sheet: 1, row: 1, col: 1, json: { data: '=sum(A5,A6)', cal: true } },
	            { sheet: 1, row: 2, col: 1, json: { data: '=sum(A10,A11)', cal: true } },
	            { sheet: 1, row: 5, col: 2, json: { data: '=sum(A1:A2,A3,5)'} }
		    ]
	    };		
	    SHEET_API.loadData(SHEET_API_HD, json, function() {
	    	var result = SHEET_API.getAllRangeMeTreeRefered(SHEET_API_HD, 1, 5, 2);
	    	console.log(result);
	    }, this);
	},
	
	// this is cross file ref example
	getCrossFileRef : function() {
		var sheet = Ext.create('EnterpriseSheet.pure.sheet.Basic', {});
		var sheet2 = Ext.create('EnterpriseSheet.pure.sheet.Basic', {});
		var store = sheet.getStore();
		var store2 = sheet2.getStore();
		
		var json2 = {
			fileName: 'CrossFile',
			sheets: [
			    {name: 'Sheet1', id: 1 }
			],	    
			cells: [
			    {sheet: 1, row: 1, col: 1, json: {data: 'I am from other file'}},
			    {sheet: 1, row: 2, col: 1, json: {data: 22}},
			    {sheet: 1, row: 3, col: 1, json: {data: 49}},
			    //this cell is a formula refer to the other file
			    {sheet: 1, row: 4, col: 1, json: {data: '=sum([CurFile]List!B4:B6)'}}
			]
		};
		
		/*
		 * load file for these 2 sheets
		 */
		store2.loadJsonFile(json2);
		
		var json = {
			fileName: 'CurFile',
		    sheets: [
		        {name: 'List', id: 1, color: 'red' }
		    ],
		    floatings: [
		        {sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
				{sheet:1, name:"validation1",ftype:"cdt", json: "{name:\"vd\",rng:[{span:[1,4,2,5,4],type:1}],opt:{dt:3,op:0,list:[\"Apple\",\"Orange\",\"Banana\",\"Kiwi\"],hint:\"Fruit\",allow:true},id:\"vd-1\"}"}
			],
		    cells: [        
			    {sheet: 1, row: 2, col: 2, json: {data: "Add Date validation to the table", fw:"bold", fz:14 }}, 	        
			    {sheet: 1, row: 4, col: 2, json: {data: 1}},
			    {sheet: 1, row: 4, col: 3, json: {data: 11}},
			    {sheet: 1, row: 4, col: 4, json: {data: 111}},	    
			    {sheet: 1, row: 6, col: 1, json: {data:"=TODAY()"}},
			    {sheet: 1, row: 6, col: 2, json: {data:"=A6*A6"}},
			    //this cell is a formula refer to the other file
			    {sheet: 1, row: 6, col: 3, json: {data:"=sum([CrossFile]Sheet1!A:A)"}}
			]
		};
		store.loadJsonFile(json);
		
		/*
		 * add reference to each other
		 */
		sheet.addCrossFileReference(store2.getFileName(), sheet2);
		sheet2.addCrossFileReference(store.getFileName(), sheet);
		/*
		 * get the result after add reference
		 */
		console.log('[CrossFile]Sheet1!A4'+store2.getCell(1, 4, 1).data+' = '+sheet2.getCellValue(1, 4, 1).data);
		console.log('[CurFile]Sheet1!C6'+store.getCell(1, 6, 3).data+' = '+sheet.getCellValue(1, 6, 3).data);
		
		console.log('example of getCrossFileRef function');
		console.log(sheet.getCrossFileRef('CrossFile', 'Sheet1!A1'));
		console.log(sheet.getCrossFileRef('', '[CrossFile]Sheet1!A1'));
		
		//when update a cell in sheet, it will cause the cell in sheet2 change
		console.log('change [CurFile]Sheet1!B4 to 201')
		sheet.updateCells([{
			sheet: 1, 
			row: 4,
			col: 2,
			json: {
				data: 201
			}
		}], function(){	
			//store2.insertColumn(1, 1, 1)
			//sheet2.sortSpan([1, 1, 2, 3, 2], undefined, 'asc');
			//sheet2.mergeCell([1, 1, 1, 1, 2]);
			
			//sheet2.unmergeCell([1, 1, 1, 2, 2]);
			console.log('The related cells in both files are changed');
			console.log('[CrossFile]Sheet1!A4 = '+sheet2.getCellValue(1, 4, 1).data);
			//console.log(sheet.getCellValue(1, 6, 1).data);
			//console.log(store.getCell(1, 6, 3).data);
			//console.log(sheet.getCellValue(1, 6, 3).arg);
			console.log('[CurFile]Sheet1!C6 = '+sheet.getCellValue(1, 6, 3).data);	
			//sheet2.unmergeCell([1, 1, 1, 1, 2]);
			//console.log(sheet.getCellValue(1, 6, 3).data);
		});
		
	}
});
