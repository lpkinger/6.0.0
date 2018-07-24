/**
 * Enterprise Spreadsheet Solution
 * Copyright (c) FeyaSoft Inc 2015. All right reserved.
 * http://www.enterpriseSheet.com
 */
Ext.onReady(function(){
    /**
     * setup the config
     */
    SCONFIG.setupDir('');
         
    /**
     * Define those 2 method as global variable
     */
    SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI', {
        openFileByOnlyLoadDataFlag: true
    });
    
    SHEET_API_HD = SHEET_API.createSheetApp({
        //withoutTitlebar: true,
        //withoutSheetbar: true,
        //withoutToolbar: true,
        //withoutContentbar: true,
        //withoutSidebar: true,
        renderTo: 'sheet-markup',
        style: 'background:white;border-left:1px solid silver;',
        height: '100%'
    });

    document.documentElement.style.background = 'none';
    Ext.getBody().setStyle('background-image', 'none');
    
    /**
     * This is function for cell onBlur call back - should be defined in the client js code
     */
    CELL_ONBLUR_CALLBACK_FN = function(value, row, column) {
	    alert("Cell value: " + value + "; Row: " + row + "; Colum: " + column);
	};
    
    /**
     * This function prove how to use EnterpriseSheet loadData API.
     * 
     * By using EnterpriseSheet load loadData API, you can generate the 
     * related json data which can be based on your application logic from  
     * your database and inject it into the Sheet Grid.
     */
    loadData2Grid = function() {
        var id = 'load-example-win';
        var loadWin = Ext.getCmp(id);
        if(!loadWin){
            loadWin = Ext.create('enterpriseSheet.example.LoadDataWin', {
                id: id
			});
        }
    	loadWin.show();
    };
    
    /**
     * This function prove how to use EnterpriseSheet getJsonData API.
     * 
     * By using EnterpriseSheet getJsonData API, you can generate the 
     * json data and save it into your backend. You can use this saved
     * jsondata for load later.
     */
    getDataFromGrid = function() {
        var retrieveWin = Ext.create('enterpriseSheet.example.GetDataWin', {});
    	retrieveWin.show();
    };
	
	/**
	 * This method will get sheet tab data in array format.
	 */
	getSheetTabData = function() {
        var json = SHEET_API.getSheetTabData(SHEET_API_HD);
    	alert(Ext.encode(json));
    };
	
	/**
	 * Add new tab to the existing sheet
	 */
	addNewTab = function() {
	    var newTab = {
		    name: 'sheet5',
			color: 'red'
		};
        SHEET_API.addSheetTab(SHEET_API_HD, newTab, function() {
		    alert("ok, sheet tab is added");
		}, this);
    };
	
	toggleGridLine = function() {
        SHEET_API.toggleGridLine(SHEET_API_HD, true);
    };
	
	/**
     * this method will get data from panel as Json format
     * you can call your method to submit it to your server.
     */
    submitData = function() {
    	var json = SHEET_API.getJsonData(SHEET_API_HD);
		console.log(Ext.encode(json));
    	alert(Ext.encode(json));
    };
            
    loadById = function(){
        SHEET_API.loadFile(SHEET_API_HD, '*u5OSUAZO3A_');
    };
            
    updateCells = function(){
	    var updateDataWin = Ext.create('enterpriseSheet.example.UpdateDataWin', {});
    	updateDataWin.show();
	};
		
    update10000Cells = function() {
        var cells = [];
        for(var i = 1; i <= 100; i++){
            for(var j = 1; j <= 100; j++){
                var cal = (i === j && 2 < i && 2 < j), data;
                if(cal){
                    data = '=A'+i+'+B'+j;
                }else{
                    data = Math.round(Math.random()*100);
                }
                cells.push({
                    'row': i,
                    'col': j,
                    'json': {
                        'data': data,
                        'cal': cal
                    }
                });
            }
        }
        
        SHEET_API.updateCells(SHEET_API_HD, cells);
    };
    
    saveData = function(){
        SHEET_API.saveData(SHEET_API_HD);
    };
            
    toggleReadOnly = function(){
        SHEET_API.setReadOnly(SHEET_API_HD, !SHEET_API_HD.sheet.isReadOnly());
    };
    
    var win;
    popupSheetWin = function(){
        if(!win){
            var hd = SHEET_API.createSheetApp({
                withoutTitlebar: true,
                //withoutSheetbar: true,
                //withoutToolbar: true,
                //withoutContentbar: true,
                withoutSidebar: true
            });
            win = Ext.create('Ext.window.Window', {
                width: 900,
                height: 500,
                layout: 'fit',
                items: [hd.appCt],
                closeAction: 'hide'
            });
        }
        win.show();
    };
    
    updateTab = function(){
        var store = SHEET_API_HD.store;
        SHEET_API.updateSheetTab(SHEET_API_HD, {
            'name': 'Market',
            'color': 'darkblue',
            'position': 2
        });
    };
	
    var getCellWin;
    getCell = function(){
        if(!getCellWin){
            getCellWin = Ext.create('Ext.window.Window', {
                title: 'Input row index and column index to get cell data',
                modal: true,
                width: 400,
                height: 300,
                layout: 'form',
                closeAction: 'hide',
                bodyStyle: 'padding: 20px;background:white;',
                items: [{
                    xtype: 'numberfield',
                    fieldLabel: 'Row index',
                    name: 'rowIndex',
                    allowBlank: false,
                    minValue: 0,
                    anchor: '100%'
                }, {
                    xtype: 'numberfield',
                    fieldLabel: 'Column index',
                    name: 'colIndex',
                    allowBlank: false,
                    minValue: 0,
                    anchor: '100%'
                }, {
                    xtype: 'textarea',
                    name: 'cellData',
                    fieldLabel: 'Cell data',
                    anchor: '100%',
                    height: 150
                }],
                buttons: [{
                    text: 'Get cell data',
                    handler: function(){
                        var rowField = getCellWin.query('numberfield[name=rowIndex]')[0];
                        var colField = getCellWin.query('numberfield[name=colIndex]')[0];
                        if(rowField.isValid() && colField.isValid()){
                            var data = SHEET_API.getCell(SHEET_API_HD, undefined, rowField.getValue(), colField.getValue());
                            getCellWin.query('textarea[name=cellData]')[0].setValue(Ext.encode(data));
                        }
                    }
                }]
            });
        }
        getCellWin.show();
    };
    
    deleteTab = function(){
        var store = SHEET_API_HD.store;
        SHEET_API.deleteSheetTab(SHEET_API_HD, store.getActivedSheetId(), function(){

        });
    };
            
    copyTab = function(){
        var store = SHEET_API_HD.store;
        SHEET_API.copySheetTab(SHEET_API_HD, store.getActivedSheetId(), 'new tab name', function(){
                                     
        });
    };
            
    insertWedgit = function(){
        var store = SHEET_API_HD.store;
        SHEET_API.insertFloatingItem(SHEET_API_HD, store.getActivedSheetId(), {
            ftype: 'wedgit',
            url: 'http://www.apple.com'
        });
    };
            
    insertPicture = function(){
        var store = SHEET_API_HD.store;
        SHEET_API.insertFloatingItem(SHEET_API_HD, store.getActivedSheetId(), {
            ftype: 'picture',
            url: 'http://images.apple.com/iphone/home/images/productbrowser/compare.png'
        });
    };
            
    insertChart = function(){
            var json = {
            fileName: 'Sparkline json file',
            sheets: [{
                     name: 'First',
                     id: 1
                     }],
            floatings: [{
                        sheet: 1,
                        name: 'sparklineChart',
                        ftype: 'cdt',
                        json: '{"name": "minichart", "rng":[{"span":[1,7,2,7,2],"type":1}], "opt":{"base":{"span":[1,3,2,5,2],"type":1},"type":"column","pc":"rgb(0,0,128)","nc":"rgb(0,0,128)"}, "id": "sparklineChart"}'
                        }],
            cells: [
                    {sheet: 1, row: 3, col: 2, json: { data: -1}},
                    {sheet: 1, row: 4, col: 2, json: { data: 2}},
                    {sheet: 1, row: 5, col: 2, json: { data: 3}}
                    ]
    };
            
            SHEET_API.loadData(SHEET_API_HD, json);
            return;
        var store = SHEET_API_HD.store;
        SHEET_API.insertFloatingItem(SHEET_API_HD, store.getActivedSheetId(), {
            ftype: 'chart',
            seriesPosition:'row',
            legendPosition:'right',
            chartType:'column',
            source:{
                series:[['',8,5,8,7],['',9,5,9,7],['',10,5,10,7],['',11,5,11,7]],
                usAbs:false
            },
            x:370,
            y:272,
            width:400,
            height:300
        });
    };
            
    setCondition = function(){
        var json = {
            fileName: 'Sparkline json file',
            sheets: [{
                name: 'First',
                id: 1
            }],
            cells: [
                {sheet: 1, row: 3, col: 3, json: { data: 2}},
                {sheet: 1, row: 4, col: 3, json: { data: -5}},
                {sheet: 1, row: 5, col: 3, json: { data: 3}}
            ]
        };
            
        SHEET_API.loadData(SHEET_API_HD, json);
        SHEET_API.setCondition(SHEET_API_HD, [[1, 3, 3, 5, 3]], 'colorbar', {
            neg: "rgb(255,127,0)",
            pos: "rgb(0,128,255)"
        });
    };

    clearCondition = function(){
        SHEET_API.clearCondition(SHEET_API_HD, [[1, 4, 3, 4, 3]]);
    };
           
    hideColumn = function(){
        SHEET_API.hideColumn(SHEET_API_HD, 1, 3);
    };

    hideRow = function(){
        SHEET_API.hideRow(SHEET_API_HD, 1, 3);
    }
            
    addGroup = function(){
        SHEET_API.updateGroups(SHEET_API_HD, [{
            dir: 'row',
            start: 2,
            end: 5
        }, {
            dir: 'row',
            start: 3,
            end: 5
        }, {
            dir: 'col',
            start: 2,
            end: 5
        }, {
            dir: 'col',
            start: 2,
            end: 4
        },{
            dir: 'col',
            start: 2,
            end: 3
        }]);
    }
            
    dataBinding = function(){
        var json = {
            fileName: 'Sparkline json file',
            sheets: [{
                name: 'First',
                id: 1
            }],
            cells: [
                {sheet: 1, row: 0, col: 0, json: { config:Ext.encode({noGridLine: true})}},
                {sheet: 1, row: 1, col: 0, json: { height: 2}},
                {sheet: 1, row: 2, col: 0, json: { height: 40}},
                {sheet: 1, row: 6, col: 0, json: { hidden: true}},
                {sheet: 1, row: 0, col: 1, json: { width: 2}},
                {sheet: 1, row: 0, col: 2, json: { width: 200}},
                {sheet: 1, row: 0, col: 3, json: { width: 150}},
                {sheet: 1, row: 2, col: 2, json: { data: 'INVOICE', fz: 20, color:'teal', fw:'bold', va:'middle'}},
                {sheet: 1, row: 2, col: 3, json: { data: '[INVOICE NO]', vname:'invoice_no', fw:'bold', va:'middle'}},
                {sheet: 1, row: 4, col: 2, json: { data: "CATEGORY", tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 4, col: 3, json: { data: "ESTIMATED", tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 4, col: 4, json: { data: "ACTUAL", tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 5, col: 2, json: { data: "[CATEGORY]", tpl: '{id: "tpl_59", span: [1,4,2,7,4]}', vname:'list.category'}},
                {sheet: 1, row: 5, col: 3, json: { data: "[ESTIMATED]", tpl: '{id: "tpl_59", span: [1,4,2,7,4]}', vname:'list.estimated'}},
                {sheet: 1, row: 5, col: 4, json: { data: "[ACTUAL]", tpl: '{id: "tpl_59", span: [1,4,2,7,4]}', vname:'list.actual'}},
                {sheet: 1, row: 6, col: 2, json: { tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 6, col: 3, json: { tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 6, col: 4, json: { tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 7, col: 2, json: { tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 7, col: 3, json: { data:"=SUM(C5:C6)", cal:true, tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}},
                {sheet: 1, row: 7, col: 4, json: { data:"=SUM(D5:D6)", cal:true, tpl: '{id: "tpl_59", span: [1,4,2,7,4]}'}}
            ]
        };
        
        SHEET_API.loadData(SHEET_API_HD, json);
    }
    var count = 0;
    changeDataSource = function(){
        if(0 == count%2){
            SHEET_API.setValueToVariable(SHEET_API_HD, {
                'invoice_no': 'ord-20150513-001',
                'list.category': ['Bouquets', 'Boutonnires', 'Corsages', 'Apple', 'Google', 'Bing'],
                'list.estimated': [1, 2, 3, 4, 5, 6],
                'list.actual': [7, 8, 9, 12, 10, 11]
            });
        }else{
            SHEET_API.setValueToVariable(SHEET_API_HD, {
                'invoice_no': 'ord-20150513-002',
                'list.category': ['Apple', 'Google', 'Bing'],
                'list.estimated': [4, 5, 6],
                'list.actual': [7, 8, 9]
            });
        }
        count++;
    }
    getCellVariables = function(){
        alert(Ext.encode(SHEET_API.getCellVariables(SHEET_API_HD)))
    }
            
    copyRange = function(){
        SHEET_API.copyPasteRange(SHEET_API_HD, [[1, 1, 1, 2, 2]], [1, 3, 3, 4, 4], 'default', false);
    }
            
    mergeCell = function(){
        SHEET_API.mergeCellForSpan(SHEET_API_HD, [0, 2, 2, 4, 4]);
    }
            
    deleteComment = function(){
        SHEET_API.deleteCommentForCoord(SHEET_API_HD, [0, 2, 2, 4, 4]);
    }
            
    SHEET_API_HD.sheet.on({
        /*
         * @param {String} name: the name of the group
         * @param {Boolean} expand: true means this group level is expanded, or it's collapsed
         * @param {Integer} sheetId: the sheet id
         * @param {Array} rows: the row index array of this row group level
         * @param {Array} oldRowGroups: the old row group array before this action
         * @param {Array} rowGroups: the row group array after this action
         */
        'togglerowgroup': function(name, expand, sheetId, rows, oldRowGroups, rowGroups){
            alert((expand ? 'Expand' : 'Collapse')+(' row '+rows[0]+' to row '+rows[rows.length-1]))
        },
        /*
         * @param {String} name: the name of the group
         * @param {Boolean} expand: true means this group level is expanded, or it's collapsed
         * @param {Integer} sheetId: the sheet id
         * @param {Array} cols: the col index array of this col group level
         * @param {Array} oldColGroups: the old col group array before this action
         * @param {Array} colGroups: the col group array after this action
         */
        'togglecolgroup': function(name, expand, sheetId, cols, oldColGroups, colGroups){
            var store = SHEET_API_HD.store;
            alert((expand ? 'Expand' : 'Collapse')+(' column '+store.getColName(cols[0])+' to column '+store.getColName(cols[cols.length-1])))
        },
        /*
         * @param {Boolean} state: true for expand, false for collapse
         * @param {Integer} sheetId: the sheet id
         * @param {Array} showns: the row index array to show
         * @param {Array} hiddens: the row index array to hide
         * @param {Array} oldGroups: the old row group array before this action
         * @param {Array} groups: the row group array after this action
         * @param {Array} newLevels: the row group level array after this action
         */
        'togglerowgrouplevel': function(state, sheetId, showns, hiddens, oldGroups, newGroups, oldLevels, newLevels){
            alert((state ? 'Expand' : 'Collapse')+' row group level');
        },
        /*
         * @param {Boolean} state: true for expand, false for collapse
         * @param {Integer} sheetId: the sheet id
         * @param {Array} showns: the column index array to show
         * @param {Array} hiddens: the column index array to hide
         * @param {Array} oldGroups: the old col group array before this action
         * @param {Array} groups: the col group array after this action
         * @param {Array} newLevels: the col group level array after this action                  
         */
        'togglecolgrouplevel': function(state, sheetId, showns, hiddens, oldGroups, newGroups, oldLevels, newLevels){
            alert((state ? 'Expand' : 'Collapse')+' column group level');
        }
    });
});