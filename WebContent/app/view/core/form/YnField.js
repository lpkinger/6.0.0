/**
*yes/no 是/否 
*/
Ext.define('erp.view.core.form.YnField', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.erpYnField',
    initComponent : function(){ 
    	this.store = Ext.create('Ext.data.Store', {
            fields: ['display', 'value'],
            data : [
                {"display": $I18N.common.form.yes, "value": '-1'},
                {"display": $I18N.common.form.no, "value": '0'}
            ]
        });
    	this.displayField = 'display';
    	this.valueField = 'value';
    	this.queryMode = 'local';
    	this.editable = false;
		this.dirty = false;
		var me = this;
		/*me.value = (me.value != 0 && me.value != -1) ? 0 : me.value;*/
		me.addEvents({
			change: true
		});
		this.callParent(arguments);
    },
    getValue: function(){
    	return this.value;
    }
});