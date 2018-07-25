Ext.define('erp.view.core.button.OrderSchedulingAnalyse',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpOrderSchedulingAnalyseButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
	text: $I18N.common.button.erpOrderSchedulingAnalyseButton,
	style: {
		marginLeft: '10px'
    },
    width: 150,  
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});