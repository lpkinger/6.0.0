/**
 * Enterprise Spreadsheet Solution
 * Copyright (c) FeyaSoft Inc 2015. All right reserved.
 * http://www.enterpriseSheet.com
 */
Ext.define('enterpriseSheet.example.GetDataWin', {
	
	extend : 'Ext.window.Window',
	
	bodyStyle : 'padding:5px;background-color:white;',
	
	resizable : false,			
    
    stateful: false,
    
    modal: true,
    
    shim : true,
    
    buttonAlign : "right",
	
	cancelText : 'Close',
    
    closable : true,
    
    closeAction : 'hide',	
    
    width : 800,
    	
    height: 400,
    
    title: 'API getJsonData() Example - retrieve json data from Sheet',
        
	layout : 'border',
	
	initComponent : function(){
		
		var json = SHEET_API.getJsonData(SHEET_API_HD, true);
		
        this.textArea = Ext.create('Ext.form.field.TextArea', {
            fieldLabel: '<b>Json data from Sheet</b>',
            style: 'margin-bottom:5px;',
            labelAlign: 'top',
            region: 'center',
            value: Ext.encode(json)
        });
		this.items = [{
            xtype: 'component',
            region: 'north',
            collapsible: false,
            split: true,
            height: 100,
            html: 'Call the following method to retrieve json data from currently sheet:<br/>' +
                  '<pre>var json = SHEET_API.getJsonData(SHEET_API_HD);<br/>' + 
                  'var jsonString = Ext.encode(json);<br/></pre>' + 
                  'You can encode it and save this as string into your database. When call SHEET_API.loadData(SHEET_API_HD, json) API, please call Ext.decode(inJson) first and inject it into loadData API.'
            }, this.textArea];
		
        this.buttons = [{
            text: 'Popup Sheet Window with content',
            handler: this.onPopup,
            scope: this
        }, {
			text:this.cancelText,
			handler:this.onCancel,
			scope:this
		}];
				
		this.callParent();		
	},
	
	onCancel : function() {
	    this.hide();	
	},
    
    onPopup : function(){
        if(!this.win){
            var hd = SHEET_API.createSheetApp({
                withoutTitlebar: true,
                withoutSheetbar: true,
                withoutToolbar: true,
                withoutContentbar: true,
                withoutSidebar: true
            });
            this.win = Ext.create('Ext.window.Window', {
                width: 950,
                height: 500,
                layout: 'fit',
                items: [hd.appCt],
                closeAction: 'hide'
            });
        }
        this.win.show();
        /*
         * normally we suggest call loadData after the sheet is rendered
         */
        var json = this.textArea.getValue();
        SHEET_API.loadData(hd, Ext.decode(json));
    }
});