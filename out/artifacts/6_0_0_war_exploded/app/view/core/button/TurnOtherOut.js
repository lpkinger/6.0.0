/**
 * 转其它出库单
 */
Ext.define('erp.view.core.button.TurnOtherOut', {
	extend : 'Ext.Button',
	alias : 'widget.erpTurnOtherOutButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpTurnOtherOutButton,
	style : {
		marginLeft : '10px'
	},
	width : 110,
	initComponent : function() {
		this.callParent(arguments);
	}
});