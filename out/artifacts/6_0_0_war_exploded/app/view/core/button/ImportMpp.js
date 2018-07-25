Ext.define('erp.view.core.button.ImportMpp',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpImportMppButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
	text: $I18N.common.button.erpImportMppButton,
	style: {
		marginLeft: '10px'
	},
	width: 100,  
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});