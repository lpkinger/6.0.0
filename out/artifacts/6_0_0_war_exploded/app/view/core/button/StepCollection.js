/**
 * 工序采集信息维护按钮
 */
Ext.define('erp.view.core.button.StepCollection', {
	extend : 'Ext.Button',
	alias : 'widget.erpStepCollectionButton',
	cls : 'x-btn-gray',
	id : 'StepCollection',
	text : $I18N.common.button.erpStepCollectionButton,
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	}
});