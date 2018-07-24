Ext.define('erp.view.oa.knowledge.VersionCheckbox', {
	extend : 'Ext.form.field.Checkbox',
	layout : 'fit',
	alias : 'widget.erpCheckBox',
	boxLabel : '保存时生成新版本',
	labelAlign : 'right',
	labelStyle : 'font-size:15px;font-color:#CDC9C9;background:#EBEBEB',
	cls : 'form-field-allowBlank',
	initComponent : function() {
		this.callParent(arguments);
	}
});