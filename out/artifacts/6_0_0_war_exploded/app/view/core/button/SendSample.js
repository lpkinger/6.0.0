/**
 * 送样按钮
 */
Ext.define('erp.view.core.button.SendSample', {
	extend : 'Ext.Button',
	alias : 'widget.erpSendSampleButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpSendSampleButton,
	style : {
		marginLeft : '10px'
	},
	width : 60,
	initComponent : function() {
		this.callParent(arguments);
	}
});