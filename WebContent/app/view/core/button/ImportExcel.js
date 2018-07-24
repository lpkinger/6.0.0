Ext.define('erp.view.core.button.ImportExcel',{ 
		extend: 'Ext.form.Panel', 
		alias: 'widget.erpImportExcelButton',
		iconCls: 'x-button-icon-excel',
    	text: $I18N.common.button.erpImportExcelButton,
    	style: {
    		marginLeft: '10px'
        },
        id:'fileform',
        layout:'column',
        width: 100,  
		 initComponent : function(){ 
			this.callParent(arguments); 
		 },
		 bodyStyle: 'background: transparent no-repeat 0 0; border:0;background-color:#f0f0f0',
		  items: [{
			xtype: 'filefield',
			name: 'file',
			buttonOnly: true,
	        hideLabel: true,
	       /* disabled:true,*/
	        width: 90,
	        height: 17,
	        id:'excelfile',
			buttonConfig: {
				iconCls: 'x-button-icon-excel',
				text: $I18N.common.button.erpImportExcelButton,
				cls: 'x-btn-gray',
				id:'filebutton',
	        },
	        }]
	});