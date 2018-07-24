Ext.define('erp.view.core.button.FlowBack',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpFlowBackButton',
	text: $I18N.common.button.erpFlowBackButton,
	iconCls: 'x-button-icon-modify',
	cls: 'x-btn-gray',
	margin:'0 5 0 0',
	id: 'erpFlowBackButton',
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});