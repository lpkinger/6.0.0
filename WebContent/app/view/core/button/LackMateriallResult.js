Ext.define('erp.view.core.button.LackMateriallResult',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpLackMateriallResultButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
	text: $I18N.common.button.erpLackMateriallResultButton,
	style: {
		marginLeft: '10px'
    },
    width: 120,  
   // disabled:true,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});