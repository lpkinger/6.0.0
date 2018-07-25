Ext.define('erp.view.ma.jprocess.MultiField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.multifield',
    layout: 'column',
    baseCls: 'x-field',//fieldContainer默认为x-component x-container x-container-default
    cls: 'x-form-field-multi',
    height: 23,
    fieldConfig: null,
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this;
    	var cw = me.columnWidth > 0.3 ? 0.3 : 0.45;
    	me.insert(0, {
	        xtype: 'dbfindtrigger',
	        name: me.name,
	        columnWidth: cw,
	        fieldStyle: me.fieldStyle,
	        allowBlank: me.allowBlank,
	        value: me.value,
	        groupName:me.groupName,
	        fieldConfig: me.fieldConfig,
	        editable: false,
	        autoDbfind:false,
	        listeners: {
	        	change: function(f){
	        		me.value = f.value;
	        		if(Ext.isEmpty(f.value)) {
	        			me.secondField.setValue('');
	        		}
	        	},
	        	aftertrigger : function(t, r) {
					t.setValue(r.get('em_code'));
					me.secondField.setValue(r.get('em_name'));
				}
	        }
	    });
    	me.insert(1, {
	        xtype: 'textfield',
	        name: me.secondname,
	        groupName:me.groupName,
	        columnWidth: 1 - cw,
	        readOnly: true,
	        fieldStyle: 'background:#f1f1f1;',
	        value: me.secondvalue
	    });
    	me.firstField = me.items.items[0];
    	me.secondField = me.items.items[1];
    },
    listeners: {
    	afterrender: function(){//去掉fieldContainer默认的高度和滚动样式
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    	}
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