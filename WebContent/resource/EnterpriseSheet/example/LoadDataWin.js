/**
 * Enterprise Spreadsheet Solution
 * Copyright (c) FeyaSoft Inc 2015. All right reserved.
 * http://www.enterpriseSheet.com
 */
Ext.define('enterpriseSheet.example.LoadDataWin', {
	
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
    
    title: 'API loadData() Example - inject json data into Sheet',
        
	layout : 'border',
	
	initComponent : function(){
		
		this.exampleRadio = Ext.create('Ext.form.RadioGroup', {
            columns: 1,
            vertical: true,
            items: [
                {boxLabel: 'Basic Json', name: 'example', inputValue: 1, checked: true},
                {boxLabel: 'Disable Json', name: 'example', inputValue: 2},
                {boxLabel: 'Chart Json', name: 'example', inputValue: 3},
                {boxLabel: 'Checkbox Json', name: 'example', inputValue: 4},
                {boxLabel: 'Dropdown list Json', name: 'example', inputValue: 5},
                {boxLabel: 'Table template Json', name: 'example', inputValue: 6},
                {boxLabel: 'Condition Json', name: 'example', inputValue: 7},
				{boxLabel: 'Validation Json', name: 'example', inputValue: 10},
				{boxLabel: 'Cell onBlur call back', name: 'example', inputValue: 12},
				{boxLabel: 'Sparkline Json', name: 'example', inputValue: 15},
				{boxLabel: 'Combine example', name: 'example', inputValue: 100}
            ],
            listeners: {
                change: {
                    fn: function (field, newValue, oldValue) {
                        var val = newValue['example'];

                        if(1 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Basic File') + this.htmlBasicCells() + this.htmlFoot() + '</pre>');
                        } else if(2 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Disable File') +  this.htmlDisableCells() + this.htmlFoot() + '</pre>');
                        } else if(3 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Chart File') + this.htmlChartFloatings() + this.htmlChartCells() + this.htmlFoot() + '</pre>');
                        } else if(4 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Checkbox File') + this.htmlCheckboxCells() + this.htmlFoot() + '</pre>');
                        } else if(5 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Dropdown list File') + this.htmlComboboxCells() + this.htmlFoot() + '</pre>');
                        } else if(6 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Table template File') + this.htmlTableCells() + this.htmlFoot() + '</pre>');
                        } else if(7 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Condition Json File') + this.htmlConditionCells() + this.htmlFoot() + '</pre>');
                        } else if(10 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Validation Json File') + this.htmlValidationCells() + this.htmlFoot() + '</pre>');
                        } else if(12 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Cell onBlur call back') + this.htmlOnBlurCells() + this.htmlFoot() + '</pre>');
                        } else if(15 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Sparkline Json File') + this.htmlSparkline() + this.htmlFoot() + '</pre>');
                        } else if(100 === val){
                            this.centerPanel.update('<pre>' + this.htmlHead('Combine Json File') + this.htmlCombineCells() + this.htmlFoot() + '</pre>');
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
            html: '<pre>' + this.htmlHead('Basic File') + this.htmlBasicCells() + this.htmlFoot() + '</pre>'
        });
		this.items = [{
            region: 'north',
            collapsible: false,
            split: true,
            border: false,
            height: 45,
            html: 'Please select one of items to generate the related json data. ' + 
                  'In general, this json data will be automatically generated from your application and then call our loadData API to inject them into sheet.'
                  + ' Click "Inject data into Sheet" button to process.'
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
			text: "<b>Inject data into Sheet</b>",
			handler:this.onInject,
			scope:this
		}];
				
		this.callParent();		
	},
	
	// this is function to inject into sheet
	onInject : function() {
		
		// check which radio is selected ...
        var val = this.exampleRadio.getValue()['example'];
        
        // var jsonString = '{"name":"Checkbox file","sheets":[{"id":1,"name":"First","actived":true,"color":"orange"},{"id":2,"name":"Second"}],"cells":[{"i":1,"x":2,"y":2,"j":"{\"data\":\"Favorite fruit:\"}"},{"i":1,"x":3,"y":2,"j":"{\"data\":\"Banana\",\"it\":\"checkbox\",\"itn\":\"fruit\",\"itchk\":false}"},{"i":1,"x":4,"y":2,"j":"{\"data\":\"Apple\",\"it\":\"checkbox\",\"itn\":\"fruit\",\"itchk\":true}"},{"i":1,"x":5,"y":2,"j":"{\"data\":\"Orange\",\"it\":\"checkbox\",\"itn\":\"fruit\",\"itchk\":false}"},{"i":1,"x":2,"y":4,"j":"{\"data\":\"Most favorite sport:\"}"},{"i":1,"x":3,"y":4,"j":"{\"data\":\"Soccer\",\"it\":\"radio\",\"itn\":\"sports\",\"itchk\":true}"},{"i":1,"x":4,"y":4,"j":"{\"data\":\"Basketball\",\"it\":\"radio\",\"itn\":\"sports\",\"itchk\":false}"},{"i":1,"x":5,"y":4,"j":"{\"data\":\"Ski\",\"it\":\"radio\",\"itn\":\"sports\",\"itchk\":false}"}],"floatings":[],"fileConfig":[]}';
        
        //var chartJ = this.chartJson();
        //var jsonString = Ext.encode(chartJ);
        //var json = Ext.decode(jsonString);

        var json;
        if(1 === val) json = this.basicJson();
        else if(2 === val)  json = this.disableJson();
        else if(3 === val)  json = this.chartJson();
        else if(4 === val)  json = this.checkboxJson();
        else if(5 === val)  json = this.comboboxJson();
        else if(6 === val)  json = this.tableTplJson();
        else if(7 === val)  json = this.conditionJson();
		else if(10 === val)  json = this.validationJson();
        else if(12 === val)  json = this.onBlurCallbackJson();
        else if(15 === val)  json = this.sparklineJson();
		else if(100 === val)  json = this.combineJson();
 
		SHEET_API.loadData(SHEET_API_HD, json);
		this.hide();
	},
	
	onCancel : function() {
	    this.hide();	
	},
	
	/////////////////////////////////////////// this is for html data to show in screen ///////////////
	htmlHead : function(filename) {
		return 'SHEET_API.loadData(SHEET_API_HD, {' +
            	          '<br/>    fileName: \'' + filename + '\',' +
            	          '<br/>    sheets: [{' +
            	          '<br/>        name: \'First tab\',' +
            	          '<br/>        color: \'orange\',' +
            	          '<br/>        id: 1' +
            	          '<br/>    },{' +
            	          '<br/>        name: \'Second tab\',' +
            	          '<br/>        id: 2' + 
            	          '<br/>    }],';
	},
	
	htmlFoot : function() {
	    return '<br/>});';
	},
	
	htmlBasicCells : function() {
		return '<br/>    cells: [' +
            '<br/>        { sheet: 1, row: 0, col: 2, json: { width: 172 } },' +
		    '<br/>        { sheet: 1, row: 0, col: 3, json: { width: 172 } },' +
		    '<br/>        { sheet: 1, row: 0, col: 4, json: { bgc: "#FBD5B5" } },' +
		    '<br/>        { sheet: 1, row: 0, col: 5, json: { width: 172 } },' +
		    '<br/>        { sheet: 1, row: 0, col: 6, json: { width: 172 } },' +
		    
		    '<br/>        { sheet: 1, row: 2, col: 2, json: { data: "Number"}}, ' +
		    '<br/>        { sheet: 1, row: 2, col: 3, json: { data: 110}}, 	    ' +
		    '<br/>        { sheet: 1, row: 3, col: 2, json: { data: "Comma format" } },' +
		    '<br/>        { sheet: 1, row: 3, col: 3, json: { data: "12223.45678", fm: "comma" } }, ' +
		    '<br/>        { sheet: 1, row: 4, col: 2, json: { data: "Fraction format" } },' +
		    '<br/>        { sheet: 1, row: 4, col: 3, json: { data: "12.6", fm: "number", dfm: "# ?/?" } }, ' +
		    '<br/>        { sheet: 1, row: 5, col: 2, json: { data: "Percent format" } },' +
		    '<br/>        { sheet: 1, row: 5, col: 3, json: { data: "0.12345", fm: "percent", dfm: "0.00%" } }, ' +
		    '<br/>        { sheet: 1, row: 6, col: 2, json: { data: "Science format" } },' +
		    '<br/>        { sheet: 1, row: 6, col: 3, json: { data: "123.6", fm: "science"} },' +
		    '<br/>        { sheet: 1, row: 7, col: 2, json: { data: "Money format" } },' +
		    '<br/>        { sheet: 1, row: 7, col: 3, json: { data: "123.45678", fm: "money|$|2|none" } }, ' +
		    '<br/>        { sheet: 1, row: 8, col: 2, json: { data: "Custom format" } },' +
		    '<br/>        { sheet: 1, row: 8, col: 3, json: { data: "-12323.45678", fm: "number", dfm: "$#,##0.00;[Red]-$#,##0.00;[Red]ZERO" } }, ' +
		    
		    '<br/>        { sheet: 1, row: 2, col: 5, json: { data: "String" } }, ' +
		    '<br/>        { sheet: 1, row: 2, col: 6, json: { data: "Ok, this is test" } }, ' +
		    '<br/>        { sheet: 1, row: 3, col: 5, json: { data: "String Bold" } },' +
		    '<br/>        { sheet: 1, row: 3, col: 6, json: { data: "bold", fw: "bold" } }, ' +
		    '<br/>        { sheet: 1, row: 4, col: 5, json: { data: "String Italic" } },' +
		    '<br/>        { sheet: 1, row: 4, col: 6, json: { data: "Italic", fs: "italic" } }, ' +
		    '<br/>        { sheet: 1, row: 5, col: 5, json: { data: "String Underline" } },' +
		    '<br/>        { sheet: 1, row: 5, col: 6, json: { data: "Underline", u: "underline" } }, ' +
		    '<br/>        { sheet: 1, row: 6, col: 5, json: { data: "String Color" } },' +
		    '<br/>        { sheet: 1, row: 6, col: 6, json: { data: "font color", color: "#FF0000" } }, ' +
		    '<br/>        { sheet: 1, row: 7, col: 5, json: { data: "String font" } },' +
		    '<br/>        { sheet: 1, row: 7, col: 6, json: { data: "Courier New", ff: "Courier New" } }, ' +
		    '<br/>        { sheet: 1, row: 8, col: 5, json: { data: "Background" } },' +
		    '<br/>        { sheet: 1, row: 8, col: 6, json: { bgc: "#F79646" } }, ' +
		    
		    '<br/>        { sheet: 1, row: 10, col: 2, json: { data: "If formula"} }, ' +
		    '<br/>        { sheet: 1, row: 10, col: 3, json: { data: "=if(c2>100, \">100\",\"<=100\")", cal: true } }, ' +
		    '<br/>        { sheet: 1, row: 11, col: 2, json: { data: "Count formula"} }, ' +
		    '<br/>        { sheet: 1, row: 11, col: 3, json: { data: "=count(B2:C3)", cal: true } }, ' +
		    '<br/>        { sheet: 1, row: 12, col: 2, json: { data: "Sum formula"} }, ' +
		    '<br/>        { sheet: 1, row: 12, col: 3, json: { data: "=sum(1,2,3)", cal: true } }, ' +
		    '<br/>        { sheet: 1, row: 13, col: 2, json: { data: "Date Formula with format" } }, ' +
		    '<br/>        { sheet: 1, row: 13, col: 3, json: { data: "=today()", cal: true, fm: "date", dfm: "Y-m-d"}}, ' +
		    '<br/>        { sheet: 1, row: 14, col: 2, json: { data: "IF/VLOOKUP formula"} }, ' +
		    '<br/>        { sheet: 1, row: 14, col: 3, json: { data: "=IF(ISNA(VLOOKUP(C2,$D$2:$D$15,1,FALSE)), \"No\", \"Yes\")", cal: true } }, ' +
			    
		    '<br/>        { sheet: 1, row: 10, col: 5, json: { data: "Hyperlink" } },' +
		    '<br/>        { sheet: 1, row: 10, col: 6, json: { data: "www.enterpriseSheet.com", link: "www.enterprisesheet.com" } },' +
		    '<br/>        { sheet: 1, row: 12, col: 5, json: { data: "Add comment" } },' +
		    '<br/>        { sheet: 1, row: 12, col: 6, json: { data: "See comments", comment: "Great work" } }' +
            '<br/>    ]';
	},
	
	htmlDisableCells : function() {
		return '<br/>    cells: [{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 0,' +
            	'<br/>        col: 1,' +
            	'<br/>        json: {dsd: "ed"}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
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
	
	htmlChartCells : function() {
		return '<br/>    cells: [' +
		    '<br/>        {sheet: 1, row: 9, col: 5, json: { data: "Monday"}}, ' +
			'<br/>        {sheet: 1, row: 10, col: 5, json: { data: "Tuesday"}}, ' +
			'<br/>        {sheet: 1, row: 11, col: 5, json: { data: "Wednesday"}}, ' +
			'<br/>        {sheet: 1, row: 9, col: 6, json: { data: 2}}, ' +
			'<br/>        {sheet: 1, row: 10, col: 6, json: { data: -4}},' +
			'<br/>        {sheet: 1, row: 11, col: 6, json: { data: -16}},' +
			'<br/>        {sheet: 1, row: 9, col: 7, json: { data: 12}},' +
			'<br/>        {sheet: 1, row: 10, col: 7, json: { data: 4}},' +
			'<br/>        {sheet: 1, row: 11, col: 7, json: { data: -4}},' +
			'<br/>        {sheet: 1, row: 8, col: 6, json: { data: "1st item"}},' +
			'<br/>        {sheet: 1, row: 8, col: 7, json: { data: "2nd item"}}' +
            '<br/>    ]';
	},
	
	htmlChartFloatings : function() {
		return '<br/>    floatings: [{' +
                '<br/>        ftype: floor,' +
            	'<br/>        name: "chart2",' +
            	'<br/>        sheet: 1,' +
            	'<br/>        json: \'{"seriesPosition":"row","legendPosition":"right","chartType":"column","floorType":"chart","source":{"series":[["",9,6,9,7],["",10,6,10,7],["",11,6,11,7]],"categories":[["",9,5,9,5],["",10,5,10,5],["",11,5,11,5]],"labels":[["",8,6,8,6],["",8,7,8,7]],"usAbs":false, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}]},"x":695,"y":164,"width":400,"height":300,"id":"chart2","sheetId":1}\'' +
            	'<br/>    }]';
	},
	
	htmlCheckboxCells : function() {
		return '<br/>    cells: [{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 2,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Favorite fruit:"}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 3,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Banana", it: "checkbox", itn: "fruit", itchk: false}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 4,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Apple", it: "checkbox", itn: "fruit", itchk: false}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 5,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Orange", it: "checkbox", itn: "fruit", itchk: false}' +
            	'<br/>     },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 2,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "Most favorite sport:"}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 3,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "Soccer", it: "radio", itn: "sports", itchk: false}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 4,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "Basketball", it: "radio", itn: "sports", itchk: false}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 5,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "Ski", it: "radio", itn: "sports", itchk: false}' +
            	'<br/>    }]';
	},
	
	htmlComboboxCells : function() {
		return '<br/>    cells: [{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 0,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {width: 152}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 2,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Monday", drop: Ext.encode({data: "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday"})}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 4,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "5", drop: Ext.encode({data: "2,5,10,20,50"})}' +
            	'<br/>    }]';
	},
	
	htmlTableCells : function() {
		return '<br/>    cells: [{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 0,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {width: 152}' +
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 2,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "CATEGORY", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +   
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 2,' +
            	'<br/>        col: 3,' +
            	'<br/>        json: {data: "ESTIMATED", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 2,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "ACTUAL", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 3,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Bouquets", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +   
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 3,' +
            	'<br/>        col: 3,' +
            	'<br/>        json: {data: "500", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 3,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "450", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 4,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Boutonnires", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +   
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 4,' +
            	'<br/>        col: 3,' +
            	'<br/>        json: {data: "200", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 4,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "150", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 5,' +
            	'<br/>        col: 2,' +
            	'<br/>        json: {data: "Corsages", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +   
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 5,' +
            	'<br/>        col: 3,' +
            	'<br/>        json: {data: "100", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    },{' +
                '<br/>        sheet: 1,' +
            	'<br/>        row: 5,' +
            	'<br/>        col: 4,' +
            	'<br/>        json: {data: "80", tpl: \'{id: "tpl_27", span: [1,2,2,5,4]}\'}' +  
            	'<br/>    }]';
	},
	
	//////////////////////////////////////////// this is for different json ///////////////////////////
	sheets : function() {
	    return [{
                name: 'First',
                id: 1,
                color: 'orange'
            }, {
                name: 'Second',
                id: 2
            }];
	},
	
	basicCells : function() {
		return [
		    { sheet: 1, row: 0, col: 2, json: { width: 172 } },
		    { sheet: 1, row: 0, col: 3, json: { width: 172 } },
		    { sheet: 1, row: 0, col: 4, json: { bgc: "#FBD5B5" } },
		    { sheet: 1, row: 0, col: 5, json: { width: 172 } },
		    { sheet: 1, row: 0, col: 6, json: { width: 172 } },
		    
		    { sheet: 2, row: 2, col: 2, json: { data: "Number"}}, 
		    { sheet: 1, row: 2, col: 3, json: { data: 110}}, 	    
		    { sheet: 1, row: 3, col: 2, json: { data: 'Comma format' } },
		    { sheet: 1, row: 3, col: 3, json: { data: "12223.45678", fm: "comma" } }, 
		    { sheet: 1, row: 4, col: 2, json: { data: 'Fraction format' } },
		    { sheet: 1, row: 4, col: 3, json: { data: "12.6", fm: "number", dfm: "# ?/?" } }, 
		    { sheet: 1, row: 5, col: 2, json: { data: 'Percent format' } },
		    { sheet: 1, row: 5, col: 3, json: { data: "0.12345", fm: "percent", dfm: "0.00%" } }, 
		    { sheet: 1, row: 6, col: 2, json: { data: 'Science format' } },
		    { sheet: 1, row: 6, col: 3, json: { data: "123.6", fm: "science"} },
		    { sheet: 1, row: 7, col: 2, json: { data: 'Money format' } },
		    { sheet: 1, row: 7, col: 3, json: { data: "123.45678", fm: "money|$|2|none" } }, 
		    { sheet: 1, row: 8, col: 2, json: { data: 'Custom format' } },
		    { sheet: 1, row: 8, col: 3, json: { data: "-12323.45678", fm: "number", dfm: "$#,##0.00;[Red]-$#,##0.00;[Red]ZERO" } }, 
		    
		    { sheet: 1, row: 2, col: 5, json: { data: 'String' } }, 
		    { sheet: 1, row: 2, col: 6, json: { data: 'Ok, this is test' } }, 
		    { sheet: 1, row: 3, col: 5, json: { data: "String Bold" } },
		    { sheet: 1, row: 3, col: 6, json: { data: "bold", fw: "bold" } }, 
		    { sheet: 1, row: 4, col: 5, json: { data: "String Italic" } },
		    { sheet: 1, row: 4, col: 6, json: { data: "Italic", fs: "italic" } }, 
		    { sheet: 1, row: 5, col: 5, json: { data: "String Underline" } },
		    { sheet: 1, row: 5, col: 6, json: { data: "Underline", u: "underline" } }, 
		    { sheet: 1, row: 6, col: 5, json: { data: 'String Color' } },
		    { sheet: 1, row: 6, col: 6, json: { data: 'font color', color: '#FF0000' } }, 
		    { sheet: 1, row: 7, col: 5, json: { data: 'String font' } },
		    { sheet: 1, row: 7, col: 6, json: { data: 'Courier New', ff: 'Courier New' } }, 
		    { sheet: 1, row: 8, col: 5, json: { data: 'Background' } },
		    { sheet: 1, row: 8, col: 6, json: { bgc: '#F79646' } }, 
		    
		    { sheet: 1, row: 10, col: 2, json: { data: 'If formula'} }, 
		    { sheet: 1, row: 10, col: 3, json: { data: '=if(c2>100, ">100","<=100")', cal: true } }, 
		    { sheet: 1, row: 11, col: 2, json: { data: 'Count formula'} }, 
		    { sheet: 1, row: 11, col: 3, json: { data: '=count(B2:C3)', cal: true } }, 
		    { sheet: 1, row: 12, col: 2, json: { data: 'Sum formula'} }, 
		    { sheet: 1, row: 12, col: 3, json: { data: '=sum(1,2,3)', cal: true } }, 
		    { sheet: 1, row: 13, col: 2, json: { data: 'Date Formula with format' } }, 
		    { sheet: 1, row: 13, col: 3, json: { data: '=today()', cal: true, fm: 'date', dfm: 'Y-m-d'}}, 
		    { sheet: 1, row: 14, col: 2, json: { data: 'IF/VLOOKUP formula'} }, 
		    { sheet: 1, row: 14, col: 3, json: { data: '=IF(ISNA(VLOOKUP(C2,$D$2:$D$15,1,FALSE)), "No", "Yes")', cal: true } }, 
			    
		    { sheet: 1, row: 10, col: 5, json: { data: "Hyperlink" } },
		    { sheet: 1, row: 10, col: 6, json: { data: "www.enterpriseSheet.com", link: "www.enterprisesheet.com" } },
		    { sheet: 1, row: 12, col: 5, json: { data: "Add comment" } },
		    { sheet: 1, row: 12, col: 6, json: { data: "See comments", comment: "Great work" } }
		];
	},
	
	disableCells : function() {
		return [{
                sheet: 1,
                row: 0,
                col: 1,
                json: {
                    dsd: "ed"
                }
            },{
                sheet: 1,
                row: 1,
                col: 0,
                json: {
                    dsd: "ed"
                }
            },{
                sheet: 1,
                row: 3,
                col: 3,
                json: {
                    data: 'Not editable',
                    dsd: "ed"
                }
            }];
	},
	
	
	chartFloatings : function() {
		return [{
			sheet: 1,
			name: 'chart12',
            ftype: 'floor',
            json: '{"seriesPosition":"row","legendPosition":"right","chartType":"column","floorType":"chart","source":{"series":[["",9,6,9,7],["",10,6,10,7],["",11,6,11,7]], "categories":[["",9,5,9,5],["",10,5,10,5],["",11,5,11,5]], "labels":[["",8,6,8,6],["",8,7,8,7]], "usAbs": true, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}] },"x":600,"y":60,"width":400,"height":300, id: "chart12"}'               
        }];
	},
	
	/**
	chartFloatings : function() {
		return [{
			sheet: 1,
			name: 'chart12',
            ftype: 'floor',
            json: '{"seriesPosition":"row","legendPosition":"right","chartType":"area","floorType":"chart","source":{"series":[["",9,6,9,7],["",10,6,10,7],["",11,6,11,7]], "categories":[["",9,5,9,5],["",10,5,10,5],["",11,5,11,5]], "labels":[["",8,6,8,6],["",8,7,8,7]], "usAbs": true, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}] },"x":600,"y":60,"width":400,"height":300, id: "chart12"}'               
        }];
	},
	
	// this is for pie chart
	chartFloatings : function() {
		return [{
			sheet: 1,
			name: 'chart12',
            ftype: 'floor',
            json: '{"seriesPosition":"col","legendPosition":"right","chartType":"pie","floorType":"chart","source":{"series":[["",9,6,11,6]], "labels":[["",9,5,9,5],["",10,5,10,5],["",11,5,11,5]], "usAbs": true },"x":600,"y":60,"width":400,"height":300, id: "chart12"}'               
        }];
	},
	**/
	
	chartCells : function() {
		return [
		    {sheet: 1, row: 9, col: 5, json: { data: 'Monday'}}, 
			{sheet: 1, row: 10, col: 5, json: { data: 'Tuesday'}}, 
			{sheet: 1, row: 11, col: 5, json: { data: 'Wednesday'}}, 
			{sheet: 1, row: 9, col: 6, json: { data: 2}}, 
			{sheet: 1, row: 10, col: 6, json: { data: -4}},
			{sheet: 1, row: 11, col: 6, json: { data: -16}},
			{sheet: 1, row: 9, col: 7, json: { data: 12}},
			{sheet: 1, row: 10, col: 7, json: { data: 4}},
			{sheet: 1, row: 11, col: 7, json: { data: -4}},
			{sheet: 1, row: 8, col: 6, json: { data: '1st item'}},
			{sheet: 1, row: 8, col: 7, json: { data: '2nd item'}}
        ];
	},
	
	checkboxCells : function() {
		return [{ sheet: 1, row: 2, col: 2, json: { data: "Favorite fruit:" } },
		    { sheet: 1, row: 3, col: 2,  json: { data: "Banana", it: "checkbox",  itn: "fruit", itchk: false }},
		    { sheet: 1, row: 4,  col: 2, json: {  data: "Apple", it: "checkbox", itn: "fruit", itchk: true }},
		    { sheet: 1, row: 5, col: 2, json: { data: "Orange", it: "checkbox", itn: "fruit", itchk: false } },
		    { sheet: 1, row: 2, col: 4, json: { data: "Most favorite sport:" } },
		    { sheet: 1, row: 3, col: 4, json: { data: "Soccer", it: "radio", itn: "sports", itchk: true }},
		    { sheet: 1, row: 4, col: 4, json: { data: "Basketball", it: "radio", itn: "sports", itchk: false}},
		    { sheet: 1, row: 5, col: 4, json: { data: "Ski", it: "radio", itn: "sports", itchk: false}}
		];
	},
	
	comboboxCells : function() {
	    return [{ sheet: 1,  row: 0,  col: 2,  json: {  width: 152  } },
            { sheet: 1, row: 2, col: 2, json: { drop: Ext.encode({data: "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday"}), data: 'Monday' }},
            { sheet: 1, row: 4, col: 2, json: { drop: Ext.encode({data: "2,5,10,20,50"}), data: '5'}}
        ];
	},
	
	tableCells : function() {
		return [{ sheet: 1, row: 0, col: 2, json: { width: 152}}, 
		    {sheet: 1, row: 2, col: 2,  json: {  data: "CATEGORY", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}' }},
		    {sheet: 1, row: 2, col: 3,  json: {  data: "ESTIMATED", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}'}},
		    {sheet: 1, row: 2, col: 4,  json: { data: "ACTUAL", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}' }},
		    {sheet: 1, row: 3, col: 2, json: {data: "Bouquets", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}'}},
		    {sheet: 1, row: 3, col: 3, json: {  data: "500", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}'}},
		    {sheet: 1, row: 3, col: 4, json: { data: "450", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}' }},
		    {sheet: 1,  row: 4,  col: 2,  json: { data: "Boutonnires", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}' } },
		    {sheet: 1, row: 4, col: 3, json: { data: "200", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}' }},
		    {sheet: 1, row: 4,  col: 4, json: { data: "150", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}' }},
            {sheet: 1,row: 5,col: 2,json: { data: "Corsages", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}'}},
            {sheet: 1, row: 5,col: 3,json: { data: "100", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}'}},
            { sheet: 1, row: 5, col: 4, json: { data: "80", tpl: '{id: "tpl_27", span: [1,2,2,5,4]}' }}
        ];
	},
	
	//////////////////////////////////////////////////////////////////////////////
	
	basicJson : function() {
		var json = {
            fileName: 'Basic file',
            sheets: this.sheets(),
            cells: this.basicCells()
        };
		
		return json;
	},
	
	disableJson : function() {
		var json = {
            fileName: 'Disable file',
            sheets: this.sheets(),
            cells: this.disableCells()
        };
		
		return json;
	},
	
	chartJson : function() {
		var json = {
            fileName: 'Chart file',
            sheets: this.sheets(),
            floatings: this.chartFloatings(),
            cells: this.chartCells()
        };
		
		return json;
	},
	
	checkboxJson : function() {
		var json = {
            fileName: 'Checkbox file',
            sheets: this.sheets(),
            cells: this.checkboxCells()
        };
		
		return json;
	},
	
	comboboxJson : function() {
		var json = {
            fileName: 'Dropdown list file',
            sheets: this.sheets(),
            cells: this.comboboxCells()
        };
		
		return json;
	},
	
	tableTplJson : function() {
		var json = {
            fileName: 'Table template file',
            sheets: this.sheets(),
            cells: this.tableCells()
        };
		
		return json;
	},
	
	// ===========================================================================
	
	htmlConditionCells : function() {
		return  '<br/>    floatings: [' +
		        '<br/>         {sheet:1, name:"condition1",ftype:"cdt",json: "{\"name\":\"boolstyle\",\"rng\":[{\"span\":[1,2,2,4,4],\"type\":1}],\"opt\":{\"type\":\"greater\",\"base\":\"150\",\"style\":{\"cbgc\":\"rgb(248,105,107)\",\"ccolor\":\"rgb(150,0,0)\"}},\"id\":\"ext-gen2764-20141212164748566\"}"}'+
		        '<br/>    ],'+
				'<br/>    cells: [{sheet: 1, row: 2, col: 2, json: {data:100}},'+
            	'<br/>         {sheet: 1, row: 2, col: 3, json: {data:200}},'+
                '<br/>         {sheet: 1, row: 2, col: 4, json: {data:300}},'+ 		    
		        '<br/>         {sheet: 1, row: 3, col: 2, json: {data:120}},'+
			    '<br/>         {sheet: 1, row: 3, col: 3, json: {data:220}},'+
			    '<br/>         {sheet: 1, row: 3, col: 4, json: {data:150}},'+
			    '<br/>         {sheet: 1, row: 4, col: 2, json: {data:130}},'+
				'<br/>         {sheet: 1, row: 4, col: 3, json: {data:170}},'+
		        '<br/>         {sheet: 1, row: 4, col: 4, json: {data:110}}'+
            	'<br/>    }]';
	},

	conditionCells : function() {
		return [
		    {sheet: 1, row: 2, col: 2, json: {data:100}},
		    {sheet: 1, row: 2, col: 3, json: {data:200}},
			{sheet: 1, row: 2, col: 4, json: {data:300}},
		    {sheet: 1, row: 3, col: 2, json: {data:120}},
			{sheet: 1, row: 3, col: 3, json: {data:220}},
		    {sheet: 1, row: 3, col: 4, json: {data:150}},
            {sheet: 1, row: 4, col: 2, json: {data:130}},
			{sheet: 1, row: 4, col: 3, json: {data:170}},
		    {sheet: 1, row: 4, col: 4, json: {data:110}}
        ];
	},
	
	conditionFloatings : function() {
		return [
		    //{sheet:1, name:"condition1",ftype:"cdt",json: "{\"name\":\"boolstyle\",\"rng\":[{\"span\":[1,2,2,4,4],\"type\":1}],\"opt\":{\"type\":\"between\",\"base\":{\"min\":\"220\",\"max\":\"250\"},\"style\":{\"cbgc\":\"rgb(248,105,107)\",\"ccolor\":\"rgb(150,0,0)\"}},\"id\":\"1234567\"}"}
			{sheet:1, name:"condition1",ftype:"cdt",json: "{\"name\":\"boolstyle\",\"rng\":[{\"span\":[1,2,2,4,4],\"type\":1}],\"opt\":{\"type\":\"greater\",\"base\":\"150\",\"style\":{\"cbgc\":\"rgb(248,105,107)\",\"ccolor\":\"rgb(150,0,0)\"}},\"id\":\"1234567\"}"}
		];
	},
	
	conditionJson : function() {
		var json = {
            fileName: 'condition json file',
            sheets: this.sheets(),
            floatings: this.conditionFloatings(),
            cells: this.conditionCells()
        };		
		return json;
	},
	
	// ==========================================================================
	
	htmlSparkline : function() {
		return '<br/>    floatings: [{' +
		        '<br/>         sheet: 1,'+
		        '<br/>         name: "sparklineChart",'+
		        '<br/>         ftype: "cdt",'+
		        '<br/>         json: \"{"name": "minichart", "rng":[{"span":[1,7,2,7,2],"type":1}], "opt":{"base":{"span":[1,3,2,5,2],"type":1},"type":"column","pc":"rgb(0,0,128)","nc":"rgb(0,0,128)"}, "id": "sparklineChart"}\"'+
		        '<br/>    }],'+
				'<br/>    cells: [{sheet: 1, row: 3, col: 2, json: {data:-1}},'+
            	'<br/>         {sheet: 1, row: 4, col: 3, json: {data:2}},'+
                '<br/>         {sheet: 1, row: 5, col: 4, json: {data:3}}'+ 		    
            	'<br/>    }]';
	},
	
	sparklineJson : function() {
		var json = {
            fileName: 'Sparkline json file',
            sheets: this.sheets(),
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
		return json;
	},
	
	// ===========================================================================
	
	htmlValidationCells : function() {
		return  '<br/>    floatings: [' +
		        '<br/>         {sheet:1, name:"validation1",ftype:"cdt",json: "{\"name\":\"vd\",\"rng\":[{\"span\":[1,2,2,4,4],\"type\":1}],\"opt\":{\"dt\":0,\"op\":0,\"min\":120,\"max\":150,\"hint\":\"OK\",\"allow\":true}},\"id\":\"vd-1\"}"}'+
		        '<br/>    ],'+
				'<br/>    cells: [{sheet: 1, row: 2, col: 2, json: {data:100}},'+
            	'<br/>         {sheet: 1, row: 2, col: 3, json: {data:200}},'+
                '<br/>         {sheet: 1, row: 2, col: 4, json: {data:300}},'+ 		    
		        '<br/>         {sheet: 1, row: 3, col: 2, json: {data:120}},'+
			    '<br/>         {sheet: 1, row: 3, col: 3, json: {data:220}},'+
			    '<br/>         {sheet: 1, row: 3, col: 4, json: {data:150}},'+
			    '<br/>         {sheet: 1, row: 4, col: 2, json: {data:130}},'+
				'<br/>         {sheet: 1, row: 4, col: 3, json: {data:170}},'+
		        '<br/>         {sheet: 1, row: 4, col: 4, json: {data:110}}'+
            	'<br/>    }]';
	},

	validationCells : function() {
		return [
		    {sheet: 1, row: 2, col: 2, json: {data:100}},
		    {sheet: 1, row: 2, col: 3, json: {data:200}},
			{sheet: 1, row: 2, col: 4, json: {data:300}},
		    {sheet: 1, row: 3, col: 2, json: {data:120}},
			{sheet: 1, row: 3, col: 3, json: {data:220}},
		    {sheet: 1, row: 3, col: 4, json: {data:150}},
            {sheet: 1, row: 4, col: 2, json: {data:130}},
			{sheet: 1, row: 4, col: 3, json: {data:170}},
		    {sheet: 1, row: 4, col: 4, json: {data:110}}
        ];
	},
	
	validationFloatings : function() {
		return [
			{sheet:1, name:"validation1",ftype:"cdt",json: "{\"name\":\"vd\",\"rng\":[{\"span\":[1,2,2,4,4],\"type\":1}],\"opt\":{\"dt\":0,\"op\":0,\"min\":120,\"max\":150,\"hint\":\"Between 120 and 150\",\"allow\":true},\"id\":\"vd-1\"}"}
		];
	},
	
	validationJson : function() {
		var json = {
            fileName: 'validation json file',
            sheets: this.sheets(),
            floatings: this.validationFloatings(),
            cells: this.validationCells()
        };		
		return json;
	},
	
	// ==========================================================================
	
	htmlOnBlurCells : function() {
		return '<br/>    cells: [{' +
		       '<br/>       sheet: 1, row: 2, col: 2, json: {data: "call back onBlur", onBlurFn: "CELL_ONBLUR_CALLBACK_FN"}' +
		       '<br/>    }]';
	},
	
	onBlurCallbackJson : function() {
		var json = {
            fileName: 'onBlur call back',
            sheets: this.sheets(),
            cells: [
            	{sheet: 1, row: 2, col: 2, json: {data: "call back onBlur", onBlurFn: "CELL_ONBLUR_CALLBACK_FN"}}
            ]
        };		
		return json;
	},
	
	// ===========================================================================
	
	htmlCombineCells : function() {
		return  '<br/>    floatings: [{sheet:1, name:"merge1",ftype:"meg",json:"[2,2,2,6]"},' +
		        '<br/>         {sheet:1, name:"condition1",ftype:"cdt",json: "{\"name\":\"boolstyle\",\"rng\":[{\"span\":[1,5,2,7,5],\"type\":1}],\"opt\":{\"type\":\"between\",\"base\":{\"min\":\"220\",\"max\":\"250\"},\"style\":{\"cbgc\":\"rgb(248,105,107)\",\"ccolor\":\"rgb(150,0,0)\"}},\"id\":\"ext-gen2764-20141212164748566\"}"}],'+
		        '<br/>    cells: [{sheet: 1,  row: 0, col: 1, json: {width:33, woff:0}},'+
            	'<br/>         {sheet: 1, row: 2, col: 0, json: {height:27, hoff:0}},'+
                '<br/>         {sheet: 1, row: 2, col: 2, json: {data:"Examples to show function of Sheet",fz:16,fw:"bold"}},'+ 		    
		        '<br/>         {sheet: 1, row: 4, col: 2, json: {data: "Red fill if cells value between 220 and 250",blc:"#FF6600", bls: "dashed",blt:false,blf:2,btc:"#FF6600",bts:"dashed",btt:false,btf:2,ff:"Courier New"}},'+
			    '<br/>         {sheet: 1, row: 4, col: 3, json:{btc:"#FF6600",bts:"dashed",btt:false,btf:2}},'+
			    '<br/>         {sheet: 1, row: 4, col: 4, json:{btc:"#FF6600",bts:"dashed",btt:false,btf:2}},'+
			    '<br/>         {sheet: 1, row: 4, col: 5, json:{brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1,btc:"#FF6600",bts:"dashed",btt:false,btf:2}},'+
		        '<br/>         {sheet: 1, row: 4, col: 6, json: {blt:false,blf:0}},'+
		        '<br/>         {sheet: 1, row: 5, col: 2, json: {data:250,blc:"#FF6600",bls:"dashed",blt:false,blf:2}},'+
		        '<br/>         {sheet: 1, row: 5, col: 3, json: {data:120}},'+
			    '<br/>         {sheet: 1, row: 5, col: 4, json: {data:120}},'+
		        '<br/>         {sheet: 1, row: 5, col: 5, json: {data:120, brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1}},'+
		        '<br/>         {sheet: 1, row: 5, col: 6, json: {blt:false,blf:0}},'+
		        '<br/>         {sheet: 1, row: 6, col: 2, json: {data:200,blc:"#FF6600",bls:"dashed",blt:false,blf:2}},'+
		        '<br/>         {sheet: 1, row: 6, col: 3, json: {data:210}},'+
		        '<br/>         {sheet: 1, row: 6, col: 4, json: {data:220}},'+
		        '<br/>         {sheet: 1, row: 6, col: 5, json: {data:250,brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1}},'+
		        '<br/>         {sheet: 1, row: 6, col: 6, json: {blt:false,blf:0}},'+
		        '<br/>         {sheet: 1, row: 7, col: 1, json: {brt:false}},'+
		        '<br/>         {sheet: 1, row: 7, col: 2, json: {data:300,blc:"#FF6600",bls:"dashed",blt:false,blf:2,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},'+
		        '<br/>         {sheet: 1, row: 7, col: 3, json: {data:310,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},'+
		        '<br/>         {sheet: 1, row: 7, col: 4, json: {data:320,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},'+
		        '<br/>         {sheet: 1, row: 7, col: 5, json: {data:330,brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},'+
            	'<br/>    }]';
	},
	
	combineCells : function() {
		return [{sheet: 1,  row: 0, col: 1, json: {width:33, woff:0}}, 
		    {sheet: 1, row: 2, col: 0, json: {height:27, hoff:0}}, 	
		    {sheet: 1, row: 2, col: 2, json: {data:"Sheet Condition Example",fz:16,fw:"bold"}}, 		    
		    {sheet: 1, row: 4, col: 2, json: {data: "Red fill if cells value between 220 and 250",blc:"#FF6600", bls: "dashed",blt:false,blf:2,btc:"#FF6600",bts:"dashed",btt:false,btf:2,ff:"Courier New"}},
			{sheet: 1, row: 4, col: 3, json:{btc:"#FF6600",bts:"dashed",btt:false,btf:2}},
			{sheet: 1, row: 4, col: 4, json:{btc:"#FF6600",bts:"dashed",btt:false,btf:2}},
			{sheet: 1, row: 4, col: 5, json:{brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1,btc:"#FF6600",bts:"dashed",btt:false,btf:2}},
		    {sheet: 1, row: 4, col: 6, json: {blt:false,blf:0}},
		    {sheet: 1, row: 5, col: 2, json: {data:250,blc:"#FF6600",bls:"dashed",blt:false,blf:2}},
		    {sheet: 1, row: 5, col: 3, json: {data:120}},
			{sheet: 1, row: 5, col: 4, json: {data:120}},
		    {sheet: 1, row: 5, col: 5, json: {data:120, brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1}},
		    {sheet: 1, row: 5, col: 6, json: {blt:false,blf:0}},
		    {sheet: 1, row: 6, col: 2, json: {data:200,blc:"#FF6600",bls:"dashed",blt:false,blf:2}},
		    {sheet: 1, row: 6, col: 3, json: {data:210}},
		    {sheet: 1, row: 6, col: 4, json: {data:220}},
		    {sheet: 1, row: 6, col: 5, json: {data:250,brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1}},
		    {sheet: 1, row: 6, col: 6, json: {blt:false,blf:0}},
		    {sheet: 1, row: 7, col: 1, json: {brt:false}},
		    {sheet: 1, row: 7, col: 2, json: {data:300,blc:"#FF6600",bls:"dashed",blt:false,blf:2,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},
		    {sheet: 1, row: 7, col: 3, json: {data:310,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},
		    {sheet: 1, row: 7, col: 4, json: {data:320,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},
		    {sheet: 1, row: 7, col: 5, json: {data:330,brc:"#FF6600",brs:"dashed",brw:2,brt:false,pr:1,bbc:"#FF6600",bbs:"dashed",bbw:2,bbt:false,pb:1}},
        ];
	},
	
	combineFloatings : function() {
		return [
		    {sheet:1, name:"merge1",ftype:"meg",json:"[2,2,2,6]"},
		    {sheet:1, name:"condition1",ftype:"cdt",json: "{\"name\":\"boolstyle\",\"rng\":[{\"span\":[1,5,2,7,5],\"type\":1}],\"opt\":{\"type\":\"between\",\"base\":{\"min\":\"220\",\"max\":\"250\"},\"style\":{\"cbgc\":\"rgb(248,105,107)\",\"ccolor\":\"rgb(150,0,0)\"}},\"id\":\"ext-gen2764-20141212164748566\"}"}
		];
	},
	
	combineJson : function() {
		var json = {
            fileName: 'Complex Json file',
            sheets: this.sheets(),
            floatings: this.combineFloatings(),
            cells: this.combineCells()
        };		
		return json;
	}
    
});