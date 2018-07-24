Ext.define('erp.view.core.button.NeedMakePrepared', {
	extend : 'Ext.Button',
	alias : 'widget.erpNeedMakePreparedButton',
	cls : 'x-btn-gray',
	id : 'NeedMakePrepared',
	text : $I18N.common.button.erpNeedMakePreparedButton,
	width : 100,
	initComponent : function() {
		this.callParent(arguments);
	}
});