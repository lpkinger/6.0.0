/**
 * 描述状态的字段 
 */
Ext.define('erp.view.core.form.StatusField', {
    extend: 'Ext.form.field.Base',
    alias: 'widget.statusfield',
    initComponent : function(){
    	this.value = this.value + '-' + $I18N.common.status[this.value];
    	this.callParent(arguments);
    }
});