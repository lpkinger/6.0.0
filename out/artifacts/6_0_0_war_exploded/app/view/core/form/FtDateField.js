/**
 * From-To datefield
 * @author yingp
 */
Ext.define('erp.view.core.form.FtDateField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.ftdatefield',
    layout: 'column',
    value: "BETWEEN '' AND ''",
    height: 22,
    items: [],
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this, allowBlank = (Ext.isDefined(me.allowBlank) ? me.allowBlank : true);
    	Ext.apply(me.fieldDefaults, {
    		allowBlank: allowBlank
    	});
    	me.insert(0, {
	        xtype: 'datefield',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(){
	        		var from = me.items.items[0].value;
	        		var to = me.items.items[1].value;
	        		me.items.items[1].setMinValue(from);
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		me.value = "BETWEEN to_date('" + Ext.Date.format(from,'Y-m-d') + " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND to_date('" 
        				+ Ext.Date.format(to,'Y-m-d') + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
	        		me.firstItem.value = from;
	        		me.secondItem.value = to;
	        	}
	        }
	    });
    	me.insert(1, {
	        xtype: 'datefield',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(){
	        		var from = me.items.items[0].value;
	        		var to = me.items.items[1].value;
	        		me.items.items[0].setMaxValue(to);
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		me.value = "BETWEEN to_date('" + Ext.Date.format(from,'Y-m-d') + " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND to_date('" 
	        			+ Ext.Date.format(to,'Y-m-d') + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
	        		me.firstItem.value = from;
	        		me.secondItem.value = to;
	        	}
	        }
	    });
    	this.firstItem = this.items.items[0];
    	this.secondItem = this.items.items[1];
	},
	reset: function(){
		this.items.items[0].reset();
		this.items.items[1].reset();
	},
	setValue: function(value){
		if(value != null && value != '' && contains(value, '~', true)){
    		this.items.items[0].setValue(value.split('~')[0]);
    		this.items.items[1].setValue(value.split('~')[1]);
    	}
	},
	setValues: function(first, second) {
		this.firstItem.setValue(first);
		this.secondItem.setValue(second || first || null);
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
    getValue: function(){
    	if(this.items.items[0].value != null && this.items.items[0].value != ''){
    		return Ext.Date.format(this.items.items[0].value, 'Y-m-d') + '~' + Ext.Date.format(this.items.items[1].value, 'Y-m-d');
    	} else {
    		return '';
    	}
    },
    isValid: function(){
    	return true;
    },
    getFilter: function() {
    	var me = this, fromVal = me.firstItem.getValue(), toVal = me.secondItem.getValue();
    	return (fromVal || toVal) ? {
    		"gte": fromVal ? Ext.Date.format(fromVal, 'Y-m-d') : null,
    	    "lte": toVal ? Ext.Date.format(toVal, 'Y-m-d') : null
    	} : null;
    }
});