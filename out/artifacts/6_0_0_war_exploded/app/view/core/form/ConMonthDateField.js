/**
 * From-To monthdatefield
 * @author yingp
 */
Ext.define('erp.view.core.form.ConMonthDateField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.conmonthdatefield',
    layout: 'column',
    value: null,
    items: [],
    initComponent : function(){
    	this.cls = (this.cls || '') + ' x-form-field-multi';
    	this.callParent(arguments);
    	var me = this;
    	me.from = me.insert(0, Ext.create('erp.view.core.form.MonthDateField', {
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        value: me.value,
	        listeners: {
	        	change: function(){
	        		var from = me.items.items[0].value;
	        		var to = me.items.items[1].value;
	        		me.items.items[1].setMinValue(from);
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		me.setValueString(from, to);
	        	}
	        }
	    }));
    	me.to = me.insert(1, Ext.create('erp.view.core.form.MonthDateField', {
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        value: me.value,
	        listeners: {
	        	change: function(){
	        		var from = me.items.items[0].value;
	        		var to = me.items.items[1].value;
	        		me.items.items[0].setMaxValue(to);
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		me.setValueString(from, to);
	        	}
	        }
	    }));
	},
	getValue: function(){
		return this.value;
	},
	setValue: function(fromVal, toVal){
		var from = this.items.items[0];
		var to = this.items.items[1];
		from.setValue(fromVal);
		to.setValue(toVal || fromVal);
	},
	setValueString: function(from, to) {
		this.firstVal = from;
		this.secondVal = to;
		if(from && to) {
			// 针对使用到from-to期间的视图，传入参数
			if(this.name.toUpperCase().indexOf('YM_VIEW_PARAM') > -1 || 
					(this.logic && this.logic.toUpperCase() == 'YM_VIEW_PARAM'))
				this.value = 'ym_view_param.set_from(' + from + ')=' + from + 
					' AND ym_view_param.set_to(' + to + ')=' + to;
			else
				this.value = "BETWEEN " + from + " AND " + to;
		} else
			this.value = null;
	},
	listeners: {
    	afterrender: function(){
    		var from = this.items.items[0].value;
    		var to = this.items.items[1].value;
    		this.items.items[1].setMinValue(from);
    		this.items.items[0].setMaxValue(to);
    		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
    		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
    		this.setValueString(from, to);
    	}
    },
    getFilter: function() {
    	var me = this, fromVal = me.from.getValue(), toVal = me.to.getValue();
    	return (fromVal || toVal) ? {
    		"gte": fromVal || toVal || null,
    		"lte": toVal || fromVal || null
    	} : null;
    }
});