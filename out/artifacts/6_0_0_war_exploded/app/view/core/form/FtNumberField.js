/**
 * From-To numberfield
 * @author yingp
 */
Ext.define('erp.view.core.form.FtNumberField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.erpFtNumberField',
    layout: 'column',
    value: "",
    height: 24,
    items: [],
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this, allowBlank = (Ext.isDefined(me.allowBlank) ? me.allowBlank : true);
    	Ext.apply(me.fieldDefaults, {
    		allowBlank: allowBlank
    	});
    	me.from = me.insert(0, {
	        xtype: 'numberfield',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        minValue:me.minValue,
	        listeners: {
	        	change: function(f){
	        		var from = f.value;
	        		var to = me.items.items[1].value;
	        		from = from == null ? to == null ? '' : to : from;
	        		to = to == null   ? from == null ? '' : from : to;
	        		if(from == ''){
	        			me.value = '';
	        		} else {
	        			me.value = "BETWEEN " + from + " AND " + to;
	        		}
	        	}
	        }
	    });
    	me.to = me.insert(1, {
	        xtype: 'numberfield',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        minValue:me.minValue,
	        listeners: {
	        	change: function(f){
	        		var from = me.items.items[0].value;
	        		var to = f.value;
	        		from = from == null  ? to == null ? '' : to : from;
	        		to = to == null  ? from == null ? '' : from : to;
	        		if(from == ''){
	        			me.value = '';
	        		} else {
	        			me.value = "BETWEEN " + from + " AND " + to;
	        		}
	        	}
	        }
	    });
	},
	listeners: {
    	afterrender: function(){
    		var tb = this.getEl().dom;
    		if(tb.nodeName == 'TABLE') {
    			return;
    		}
    		tb.childNodes[1].style.height = 22;
    		tb.childNodes[1].style.overflow = 'hidden';
    	}
    },
    reset: function(){
		this.items.items[0].reset();
		this.items.items[1].reset();
	},
    getValue: function(){
    	if(this.value != null && this.value != ''){
    		if(this.items.items[0].value == null || this.items.items[0].value == ''){
    			return this.items.items[1].value + '~' + this.items.items[1].value;
    		} else if(this.items.items[1].value == null || this.items.items[1].value == ''){
    			return this.items.items[0].value + '~' + this.items.items[0].value;
    		} else {
    			return this.items.items[0].value + '~' + this.items.items[1].value;
    		}
    	} else {
    		return '';
    	}
    },
    isValid: function(){
    	return true;
    },
    setValue: function(value){
    	if(value != null && value != '' && contains(value, '~', true)){
    		this.items.items[0].setValue(value.split('~')[0]);
    		this.items.items[1].setValue(value.split('~')[1]);
    	}
    },
    getFilter: function() {
    	var me = this, fromVal = me.from.getValue(), toVal = me.to.getValue();
    	return (fromVal || toVal) ? {
    		"gte": fromVal,
    		"lte": toVal
    	} : null;
    }
});