Ext.define('erp.view.core.form.ReviewField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.reviewfield',
    layout: 'vbox',
    items: [],
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this;
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
	        		me.value = "BETWEEN to_date('" + Ext.Date.toString(from) + "','yyyy-mm-dd') AND to_date('" 
        				+ Ext.Date.toString(to) + "','yyyy-mm-dd')";
	        		me.items.items[0].value = from;
	        		me.items.items[1].value = to;
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
	        		me.value = "BETWEEN to_date('" + Ext.Date.toString(from) + "','yyyy-mm-dd') AND to_date('" 
	        			+ Ext.Date.toString(to) + "','yyyy-mm-dd')";
	        		me.items.items[0].value = from;
	        		me.items.items[1].value = to;
	        	}
	        }
	    });
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
	listeners: {
    	afterrender: function(){
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
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
    }
});