Ext.define('erp.view.core.button.DeliveryApply',{
	extend : 'Ext.Button',
	alias : 'widget.erpDeliveryApplyButton',
	id : 'erpDeliveryApplyButton',
	text : '交期回复',
	iconCls: 'x-button-icon-save',
	cls: 'x-btn-gray',
	width: 80,
	style: {
		marginLeft: '10px'
    },
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});