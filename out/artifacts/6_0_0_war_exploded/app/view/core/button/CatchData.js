/**
 * 获取数据
 */
Ext.define('erp.view.core.button.CatchData', {
	extend : 'Ext.Button',
	alias : 'widget.erpCatchDataButton',
	param : [],
	id : 'erpCatchDataButton',
	text : $I18N.common.button.erpCatchDataButton,
	iconCls : 'x-button-icon-reset',
	cls : 'x-btn-gray',
	width : 100,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});