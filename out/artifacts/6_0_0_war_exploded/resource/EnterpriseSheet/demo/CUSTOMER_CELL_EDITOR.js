/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
Ext.define('customer.CellEditor', {
	
	extend : 'Ext.window.Window',
    bodyStyle : 'background-color:white;padding:10px;',         
	width : 450,    	
	height: 200,
    plain: true,          
    resizable: false,       
    shim : true,  
    modal: true,
	prepareButton : Ext.emptyFn,  
	title: "Custom Cell Editor",
	
	initComponent : function(){
	    
		this.nameField = Ext.create('Ext.form.field.Text', { 
			fieldLabel: "Name",		
			labelWidth: 70,
			allowBlank:false,
			anchor: '100%'
		});
		
		this.companyField = Ext.create('Ext.form.field.Text', { 
			fieldLabel: "Company",		
			labelWidth: 70,
			allowBlank:false,
			anchor: '100%'
		});
		
		this.items = [this.nameField, this.companyField];
		
		this.dockedItems = [{
			xtype: 'container',
			dock: 'right',
			width: 100,
			style: 'padding:0px 10px;',
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			items: [{
				xtype: 'button',
				text: "Update",
				handler: this.onUpdate,
				scope: this
			}, {
				xtype: 'button',
				text: "Cancel",
				style: 'margin-top:10px;',
				handler: this.onCancel,
				scope: this
			}]
		}];
		
		this.callParent();	
		
		this.on('show', function() {
			this.nameField.setValue(this.jsonData.data);
			
			// get sheet next cell for company name ...
			var cellData = SHEET_API.getCell(SHEET_API_HD, 1, this.row, this.column+1);			
			this.companyField.setValue(cellData.data);
			
		}, this);
	},
	
	// check whether field is ok ...
	onUpdate : function() {
		var cells = [];
		cells.push({sheet: 1, row: this.row,  col: this.column, json: { data: this.nameField.getValue()}, applyWay: 'apply' });
		cells.push({sheet: 1, row: this.row,  col: this.column+1, json: { data: this.companyField.getValue()}, applyWay: 'apply' });

		SHEET_API.updateCells(SHEET_API_HD, cells);
		this.close();
	},
	
	onCancel : function() {
	    this.close();	
	}
});