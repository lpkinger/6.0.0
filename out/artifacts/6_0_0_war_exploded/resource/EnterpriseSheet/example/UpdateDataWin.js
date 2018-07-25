/**
 * Enterprise Spreadsheet Solution
 * Copyright (c) FeyaSoft Inc 2015. All right reserved.
 * http://www.enterpriseSheet.com
 */
Ext.define('enterpriseSheet.example.UpdateDataWin', {
	
	extend : 'Ext.window.Window',
	
	bodyStyle : 'padding:5px;background-color:white;',
	
	resizable : false,			
    
    stateful: false,
    
    modal: true,
    
    shim : true,
    
    buttonAlign : "right",
	
	cancelText : 'Cancel',
    
    closable : true,
    
    closeAction : 'hide',	
    
    width : 800,
    	
    height: 500,
    
    title: 'API updateCells() Example - update cells data',
        
	layout : 'border',
	
	initComponent : function(){
		
		this.exampleRadio = Ext.create('Ext.form.RadioGroup', {
            columns: 1,
            vertical: true,
            items: [
                {boxLabel: 'Update Rows/Columns', name: 'example', inputValue: 1, checked: true},
                {boxLabel: 'Disable', name: 'example', inputValue: 2},
				{boxLabel: 'Cell color', name: 'example', inputValue: 3},
				{boxLabel: 'Cell align / indent', name: 'example', inputValue: 4},
				{boxLabel: 'Formula', name: 'example', inputValue: 5},
				{boxLabel: 'Cross Sheet Reference', name: 'example', inputValue: 6},
				{boxLabel: 'Money format', name: 'example', inputValue: 7},
				{boxLabel: 'Date format', name: 'example', inputValue: 71},
				{boxLabel: 'Custom format', name: 'example', inputValue: 8},
				{boxLabel: 'Cell link', name: 'example', inputValue: 9},
				{boxLabel: 'Cell comment', name: 'example', inputValue: 10},
				{boxLabel: 'Cell font style', name: 'example', inputValue: 11},
				{boxLabel: 'Insert a widget', name: 'example', inputValue: 12}
            ],
            listeners: {
                change: {
                    fn: function (field, newValue, oldValue) {
                        var val = newValue['example'];

                        if(1 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlColRows() + this.htmlFoot() + '</pre>');
                        } else if(2 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlDisableCells() + this.htmlFoot() + '</pre>');
                        } else if(3 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCellColor() + this.htmlFoot() + '</pre>');
                        } else if(4 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCellAlign() + this.htmlFoot() + '</pre>');
                        } else if(5 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlFormula() + this.htmlFoot() + '</pre>');
                        } else if(6 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCrossSheet() + this.htmlFoot() + '</pre>');
                        } else if(7 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCellMoney() + this.htmlFoot() + '</pre>');
                        } else if(8 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCellCustom() + this.htmlFoot() + '</pre>');
                        } else if(9 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCellLink() + this.htmlFoot() + '</pre>');
                        } else if(10 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCellComment() + this.htmlFoot() + '</pre>');
                        } else if(11 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlFontComment() + this.htmlFoot() + '</pre>');
                        } else if(12 === val){
                            this.centerPanel.update('<pre>' + this.htmlInsertWedgit() + '</pre>');
                        } else if(71 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead() + this.htmlCellDate() + this.htmlFoot() + '</pre>');
                        } 
                    },
                    scope: this
                }
            }
        });
		
        this.centerPanel = Ext.create('Ext.Panel', {
            title: 'Inject Json Data',
            region: 'center',
            anchor: '100%',
            bodyStyle : 'padding:2px 0px 5px 10px;',
            split: true,
            autoScroll: true,
            html: '<pre>' + this.htmlHead() + this.htmlColRows() + this.htmlFoot() + '</pre>'
        });
		this.items = [{
            region: 'north',
            collapsible: false,
            split: true,
            border: false,
            height: 45,
            html: 'Please select one of items to generate the related json data. ' + 
                  'In general, this json data will be automatically generated from your application and then call our loadData API to update them into sheet.'
                  + ' Click "Update cell data" button to process.'
                  + '<br/>View Detail Code at:  <font color=blue>*/js/cubedrive/Example/*.js </font>'
        }, {
        	region: 'center',
        	border: false,
        	split: true,
            layout : 'border',
            items: [{
            	region: 'west',
            	title: 'Examples',
            	collapsible: true,
            	width: 170,
            	split: true,
            	items: [this.exampleRadio]
            }, this.centerPanel]
        }];
		
		this.buttons = [{
			text:this.cancelText,
			handler:this.onCancel,
			scope:this
		},{
			text: "<b>Update cell data</b>",
			handler:this.onInject,
			scope:this
		}];
				
		this.callParent();		
	},
	
	// this is function to inject into sheet
	onInject : function() {
		
		// check which radio is selected ...
        var val = this.exampleRadio.getValue()['example'];

        var cellsJson;
        if(1 === val) cellsJson = this.rowColCells();
        else if(2 === val)  cellsJson = this.disableCells();
        else if(3 === val)  cellsJson = this.colorCells();
        else if(4 === val)  cellsJson = this.alignCells();
        else if(5 === val)  cellsJson = this.formulaCells();
        else if(6 === val)  cellsJson = this.crossSheetCells();
        else if(7 === val)  cellsJson = this.moneyJson();
		else if(71 === val)  cellsJson = this.dateJson();
		else if(8 === val)  cellsJson = this.customJson();
		else if(9 === val)  cellsJson = this.linkJson();
		else if(10 === val)  cellsJson = this.commentJson();
		else if(11 === val)  cellsJson = this.fontJson();
		else if(12 === val)  {
			cellsJson = {
				ftype: 'wedgit',
                url: 'http://www.enterpriseSheet.com'	
			};
			var store = SHEET_API_HD.store;
			SHEET_API.insertFloatingItem(SHEET_API_HD, store.getActivedSheetId(), cellsJson);
			this.hide();
			return;
		}
 
		SHEET_API.updateCells(SHEET_API_HD, cellsJson);
		this.hide();
	},
	
	onCancel : function() {
	    this.hide();	
	},
	
	/////////////////////////////////////////// this is for html data to show in screen ///////////////
	htmlHead : function(filename) {
		return 'SHEET_API.updateCells(SHEET_API_HD, ';
	},
	
	htmlFoot : function() {
	    return '<br/>);';
	},
	
	htmlColRows : function() {
		return '<br/>    [' +
            '<br/>        { row: 0, col: 1, json: { width: 20, bgc: "#FBD5B5"} },' +
		    '<br/>        { row: 0, col: 2, json: { width: 172 } },' +
		    '<br/>        { row: 1, col: 0, json: { height: 10, bgc: "#FBD5B5" } },' +
		    '<br/>        { row: 2, col: 0, json: { height: 40 } },' +	
            '<br/>    ]';
	},
	
	htmlDisableCells : function() {
		return '<br/>    [{' +
            	'<br/>        row: 0,' +
            	'<br/>        col: 1,' +
            	'<br/>        json: {dsd: "ed"}' +
            	'<br/>    },{' +
            	'<br/>        row: 1,' +
            	'<br/>        col: 0,' +
            	'<br/>        json: {dsd: "ed"}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 3,' +
            	'<br/>        col: 3,' +
            	'<br/>        json: {data: "not editable", dsd: "ed"}' +
            	'<br/>    }]';
	},
	
    htmlCellColor : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "background color", bgc: "#FBD5B5" } },' +
		    '<br/>        { row: 2, col: 3, json: { data: "font color", color: "#FBD5B5"} },' +
		    '<br/>        { row: 2, col: 4, json: { data: "both", bgc: "#FBD5B5", color: "blue"} }' +
            '<br/>    ]';
	},
	
	htmlCellAlign : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 0, json: { height: 40 } },' +
		    '<br/>        { row: 0, col: 2, json: { width: 172 } },' +
		    '<br/>        { row: 2, col: 2, json: { data: "align right", ta: "right" } },' +
			'<br/>        { row: 3, col: 2, json: { data: "align center", ta: "center"} },' +
			'<br/>        { row: 4, col: 2, json: { data: "indent 10 px", ti: "10px"} },' +
			'<br/>        { row: 2, col: 3, json: { data: "vertical align", va: "bottom" } }' +
            '<br/>    ]';
	},
	
	htmlFormula : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: 100} },' +
		    '<br/>        { row: 3, col: 2, json: { data: "=b2", cal: true} },' +
		    '<br/>        { row: 4, col: 2, json: { data: "=sum(1,2)", cal: true} },' +
			'<br/>        { row: 2, col: 3, json: { data: "=10+b2", cal: true}},' +
			'<br/>        { row: 3, col: 3, json: { data: "=IF(ISNA(VLOOKUP(C2,$D$2:$D$15,1,FALSE)), \"No\", \"Yes\")", cal: true}}' +
            '<br/>    ]';
	},
	
	htmlCrossSheet : function() {
	    return '<br/>    [' +
		    '<br/>        { sheet: 2, row: 2, col: 2, json: { data: 100} },' +
		    '<br/>        { row: 3, col: 2, json: { data: "=\'Second\'!b2", cal: true} },' +
		    '<br/>        { row: 4, col: 2, json: { data: "=sum(\'Second\'!b2,2)", cal: true} },' +
			'<br/>        { row: 2, col: 3, json: { data: "=\'Second\'!b2+b2", cal: true}},' +
            '<br/>    ]';
	},
	
	htmlCellMoney : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "12345.678", fm: "money|$|2|none"} },' +
		    '<br/>        { row: 3, col: 2, json: { data: "12345.678", fm: "money|RMB|2|none"} },' +
		    '<br/>        { row: 4, col: 2, json: { data: "12345.678", fm: "money|$|1|none"} },' +
			'<br/>        { row: 5, col: 2, json: { data: "12345.678", fm: "money|¥|1|none"} },' +
			'<br/>        { row: 2, col: 5, json: { data: "-12345.678", fm: "money|$|2|negative1"} },' +
		    '<br/>        { row: 3, col: 5, json: { data: "-12345.678", fm: "money|$|2|negative2"} },' +
			'<br/>        { row: 4, col: 5, json: { data: "-12345.678", fm: "money|$|2|negative3"} },' +
            '<br/>    ]';
	},
	
	htmlCellDate : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "Y/m/d" } },' +
		    '<br/>        { row: 3, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "Y-m-d" } },' +
		    '<br/>        { row: 4, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "F d, Y"} },' +
			'<br/>        { row: 5, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "M d, Y"} },' +
			'<br/>        { row: 2, col: 5, json: { data: "2015-01-27", fm: "date", dfm: "d-m-y"} },' +
		    '<br/>        { row: 3, col: 5, json: { data: "2015-01-27", fm: "date", dfm: "d-M-y"} },' +
			'<br/>        { row: 4, col: 5, json: { data: "2015-01-27", fm: "date", dfm: "l, M d, Y"} },' +
            '<br/>    ]';
	},
	
	htmlCellCustom : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "[magenta]RMB¥#,##0.00;[green]-RMB¥#,##0.00;[red]zero;@" } },' +
		    '<br/>        { row: 3, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "[Blue]#,##0.00;[Red]-#,##0.00;[yellow]zero;@" } },' +
		    '<br/>        { row: 4, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "$#,##0.00;[Red]-$#,##0.00;[Red]ZERO" } },' +
			'<br/>        { row: 5, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "#,##0.00;[Red]-#,##0.00" } },' +
			'<br/>        { row: 2, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "# ??/??" }},' +
		    '<br/>        { row: 3, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "# ??/16" }},' +
			'<br/>        { row: 4, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "0.00%" }},' +
			'<br/>        { row: 5, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "0.0e+00" }},' +
            '<br/>    ]';
	},
	
	htmlCellLink : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "link", link: "www.enterpriseSheet.com" } },' +
		    '<br/>        { row: 2, col: 3, json: { data: "www.google.com", link: "www.google.com"} },' +
            '<br/>    ]';
	},
	
	htmlCellComment : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "comment", comment: "Great work" } }' +
            '<br/>    ]';
	},
	
	htmlFontComment : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "bold", fw: "bold" }},' +
			'<br/>        { row: 2, col: 3, json: { data: "Italic", fs: "italic" } },' +
			'<br/>        { row: 2, col: 4, json: { data: "Underline", u: "underline" } },' +
			'<br/>        { row: 3, col: 2, json: { data: "Courier New", ff: "Courier New" } },' +
			'<br/>        { row: 3, col: 3, json: { data: "font size", fz: 14 } },' +
			'<br/>        { row: 3, col: 4, json: { data: "Strike", s: "line-through" } },' +
			'<br/>        { row: 3, col: 5, json: { data: "Overline", o: "overline" } }' +
            '<br/>    ]';	
	},
	
	htmlInsertWedgit : function() {
	    return '<br/>        var store = SHEET_API_HD.store;' +
        '<br/>        SHEET_API.insertFloatingItem(SHEET_API_HD, store.getActivedSheetId(), {' +
        '<br/>            ftype: "wedgit",' +
        '<br/>            url: "http://www.enterpriseSheet.com"' +
        '<br/>        });' 
	},
	
	htmlGreaterCondComment : function() {
	    return '<br/>    [' +
		    '<br/>        { row: 2, col: 2, json: { data: "bold", fw: "bold" }},' +
			'<br/>        { row: 2, col: 3, json: { data: "Italic", fs: "italic" } },' +
			'<br/>        { row: 2, col: 4, json: { data: "Underline", u: "underline" } },' +
			'<br/>        { row: 3, col: 2, json: { data: "Courier New", ff: "Courier New" } },' +
			'<br/>        { row: 3, col: 3, json: { data: "font size", fz: 14 } },' +
			'<br/>        { row: 3, col: 4, json: { data: "Strike", s: "line-through" } },' +
			'<br/>        { row: 3, col: 5, json: { data: "Overline", o: "overline" } }' +
            '<br/>    ]';	
	},
	
	//////////////////////////////////////////// this is for different json ///////////////////////////
	
	rowColCells : function() {
		return [
		    { row: 0, col: 1, json: { width: 20, bgc: "#FBD5B5" } },
		    { row: 0, col: 2, json: { width: 172 } },
		    { row: 1, col: 0, json: { height: 10, bgc: "#FBD5B5" } },
		    { row: 2, col: 0, json: { height: 40 } }
		];
	},
	
	disableCells : function() {
		return [
		    { row: 0, col: 1, json: { dsd: "ed" }},
			{ row: 1, col: 0, json: { dsd: "ed" }},
			{ row: 3, col: 3, json: { data: 'Not editable', dsd: "ed" }}
	    ];
	},
	
	colorCells : function() {
		return [		   
		   { row: 2, col: 2, json: { data: 'background color', bgc: "#FBD5B5" } },
		   { row: 2, col: 3, json: { data: 'font color', color: "#FBD5B5"} },
		   { row: 2, col: 4, json: { data: 'both', bgc: "#FBD5B5", color: 'blue'} }
		];
	},
	
	alignCells : function() {
		return [
		    { row: 2, col: 0, json: { height: 40 } },
		    { row: 0, col: 2, json: { width: 172 } },
		    { row: 2, col: 2, json: { data: 'align right', ta: 'right' } },
		    { row: 3, col: 2, json: { data: 'align center', ta: 'center'} },
		    { row: 4, col: 2, json: { data: 'indent 10 px', ti: '10px'} },
			{ row: 2, col: 3, json: { data: 'vertical align', va: 'bottom' } },
		];
	},
	
	formulaCells : function() {
		return [
		    { row: 2, col: 2, json: { data: '100'} },
		    { row: 3, col: 2, json: { data: "=b2", cal: true} },
		    { row: 4, col: 2, json: { data: "=sum(1,2)", cal: true} },
			{ row: 2, col: 3, json: { data: "=10+b2", cal: true}},
			{ row: 3, col: 3, json: { data: "=IF(ISNA(VLOOKUP(C2,$D$2:$D$15,1,FALSE)), \"No\", \"Yes\")", cal: true}},
		];
	},
	
	crossSheetCells : function() {
		return [
		    { sheet: 2, row: 2, col: 2, json: { data: 100} },
		    { row: 3, col: 2, json: { data: "='Second'!b2", cal: true} },
		    { row: 4, col: 2, json: { data: "=sum('Second'!b2,2)", cal: true} },
			{ row: 2, col: 3, json: { data: "='Second'!b2+b2", cal: true}}
		];
	},
	
	moneyJson : function() {
		return [
		    { row: 2, col: 2, json: { data: "12345.678", fm: "money|$|2|none"} },
		    { row: 3, col: 2, json: { data: "12345.678", fm: "money|RMB|2|none"} },
			{ row: 4, col: 2, json: { data: "12345.678", fm: "money|$|1|none"} },
			{ row: 5, col: 2, json: { data: "12345.678", fm: "money|¥|1|none"} },
			{ row: 2, col: 5, json: { data: "-12345.678", fm: "money|$|2|negative1"} },
			{ row: 3, col: 5, json: { data: "-12345.678", fm: "money|$|2|negative2"} },
			{ row: 4, col: 5, json: { data: "-12345.678", fm: "money|$|2|negative3"} },
		];
	},
	
	dateJson : function() {
		return [
		    { row: 2, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "Y/m/d"} },
		    { row: 3, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "Y-m-d"} },
			{ row: 4, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "F d, Y"} },
			{ row: 5, col: 2, json: { data: "2015-01-27", fm: "date", dfm: "M d, Y"} },
			{ row: 2, col: 5, json: { data: "2015-01-27", fm: "date", dfm: "d-m-y"} },
			{ row: 3, col: 5, json: { data: "2015-01-27", fm: "date", dfm: "d-M-y"} },
			{ row: 4, col: 5, json: { data: "2015-01-27", fm: "date", dfm: "l, M d, Y"} },
		];
	},
	
	customJson : function() {
		return [
		    { row: 2, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "[magenta]RMB¥#,##0.00;[green]-RMB¥#,##0.00;[red]zero;@" } },
		    { row: 3, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "[Blue]#,##0.00;[Red]-#,##0.00;[yellow]zero;@" } },
			{ row: 4, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "$#,##0.00;[Red]-$#,##0.00;[Red]ZERO" } },
			{ row: 5, col: 2, json: { data: "-12323.45678", fm: "number", dfm: "#,##0.00;[Red]-#,##0.00" } },
			{ row: 2, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "# ??/??" } },
			{ row: 3, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "# ??/16" } },
			{ row: 4, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "0.00%" } },
			{ row: 5, col: 5, json: { data: "-12323.45678", fm: "number", dfm: "0.0e+00" } },
		];
	},
	
	linkJson : function() {
		return [
			{ row: 2, col: 2, json: { data: "link", link: "www.enterpriseSheet.com" } },
			{ row: 2, col: 3, json: { data: "www.google.com", link: "www.google.com"} }
		];
	},
	
	commentJson : function() {
		return [
			{ row: 2, col: 2, json: { data: "comment", comment: 'Great work' } }
		];
	},
	
	fontJson : function() {
		return [
			{ row: 2, col: 2, json: { data: "bold", fw: "bold" }},
			{ row: 2, col: 3, json: { data: "Italic", fs: "italic" } },
			{ row: 2, col: 4, json: { data: "Underline", u: "underline" } },
			{ row: 3, col: 2, json: { data: "Courier New", ff: "Courier New" } },
			{ row: 3, col: 3, json: { data: "font size", fz: 14 } },
			{ row: 3, col: 4, json: { data: "Strike", s: 'line-through' } },
			{ row: 3, col: 5, json: { data: "Overline", o: 'overline' } }
		];
	},
	
	greaterCondJson : function() {
		return []
	}
});