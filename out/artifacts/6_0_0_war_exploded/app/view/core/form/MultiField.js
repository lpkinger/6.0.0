/**
 * 合并字段 
 */
Ext.define('erp.view.core.form.MultiField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.multifield',
    layout: 'column',
    baseCls: 'x-field',//fieldContainer默认为x-component x-container x-container-default
    fieldConfig: null,
    initComponent : function(){
    	this.cls = (this.cls || '') + ' x-form-field-multi';
    	this.callParent(arguments);
    	var me = this;
    	var cw = me.columnWidth > 0.3 ? 0.3 : 0.45;
    	me.insert(0, {
	        xtype: 'dbfindtrigger',
	        name: me.name,
	        columnWidth: cw,
	        fieldStyle: me.fieldStyle,
	        allowBlank: me.allowBlank,
	        value: (!me.secondvalue && me.value && isNaN(me.value))?me.value.split(';')[0]:me.value,
	        groupName:me.groupName,
	        fieldConfig: me.fieldConfig,
	        editable: me.editable,
	        readOnly:me.readOnly,
	        dbCaller:me.dbCaller,
	        listeners: {
	        	change: function(f,newvalue,oldvalue){	       
	        		me.value = newvalue||f.value;
	        		if(Ext.isEmpty(f.value)) {
	        			me.secondField.setValue('');
	        		}
	        	}
	        }
	    });
    	me.insert(1, {
	        xtype: 'textfield',
	        id: me.secondname,
	        name: me.secondname,
	        groupName:me.groupName,
	        columnWidth: 1 - cw,
	        readOnly: true,
	        fieldStyle: 'background:#f1f1f1;',
	        value: (!me.secondvalue && me.value && isNaN(me.value) && me.value.indexOf(';')>0) ?me.value.split(';')[1]:me.secondvalue
	    });
	    //合并字段会将第二个字段的默认值加入secondvalue 后台处理的是 value=(第一个值;第二个值) 但是在Ext.getCmp()时取value会导致带出A;B 要去掉B值
	    if(me.value&&me.value.indexOf(';')>-1){
	    	me.value = me.value.split(';')[0];
	    }
    	me.firstField = me.items.items[0];
    	me.secondField = me.items.items[1];
    },
    isValid: function(){
    	return this.firstField.isValid();
    },
    setValue: function(value){
    	this.firstField.setValue(value);
    },
    getValue: function(){
    	return this.value;
    },
    setReadOnly: function(bool){
    	this.firstField.setReadOnly(bool);
    },
    setFieldStyle: function(style){
    	this.firstField.setFieldStyle(style);
    }
});