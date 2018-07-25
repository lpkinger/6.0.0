/**
 * 替代关系按钮
 */
Ext.define('erp.view.core.button.SubRelation', {
	extend : 'Ext.Button',
	alias : 'widget.erpSubRelationButton',
	cls : 'x-btn-gray',
	id : 'SubRelation',
	text : $I18N.common.button.erpSubRelationButton,
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	}
});