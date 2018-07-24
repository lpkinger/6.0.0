/**
 * From-To dbfindtrigger
 * @author yingp
 */
Ext.define('erp.view.core.form.FtFindField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.ftfindfield',
    layout: 'column',
    value: "BETWEEN '' AND ''",
    valuePrint:"",
    baseCls: null,
    autoScroll: false,
    height: 24,
    items: [],
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this, allowBlank = (Ext.isDefined(me.allowBlank) ? me.allowBlank : true);
    	Ext.apply(me.fieldDefaults, {
    		allowBlank: allowBlank
    	});
    	me.from = me.insert(0, {
	        xtype: 'dbfindtrigger',
	        id: me.name + '_from',
	        name: me.name,
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(f){
	        		var from = f.value;
	        		var to = me.items.items[1].value;
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		if(from == ''){
	        			me.value = '';
	        			me.valuePrint = '';
	        		} else {
	        			me.value = "BETWEEN '" + from + "' AND '" + to + "'";
		        		if(me.ownerCt){
		        			var tablename = me.ownerCt.tablename;
		        			me.valuePrint="{"+tablename+"."+me.name+"}>='"+from+"' and {"+tablename+"."+me.name+"}<='"+to+"'";
		        		}
	        		}
	        	},
	        	select:function(combo,records,eOpts){
					var con ="";
					var which = 'form';
					var cal = combo.dbCaller||caller;
					var key = combo.triggerName||combo.name;
				    Ext.each(records,function(data){
					   con = !Ext.isEmpty(data.data[Ext.util.Format.lowercase(combo.searchFieldArray)]) ? (combo.searchFieldArray + " = '" + data.data[Ext.util.Format.lowercase(combo.searchFieldArray)].replace(/\'/g,"''")  + "'") : null;
				    });
					me.setSelectValue(combo,which, cal, key, combo.getCondition(con)); //光标移开后自动dbfind
				}
	        }
	    });
    	me.to = me.insert(1, {
	        xtype: 'dbfindtrigger',
	        id: me.name + '_to',
	        name: me.name,
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(f){
	        		var from = me.items.items[0].value;
	        		var to = f.value;
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		if(from == ''){
	        			me.value = '';
	        			me.valuePrint = '';
	        		} else {
	        			me.value = "BETWEEN '" + from + "' AND '" + to + "'";
	        			if(me.ownerCt){
		        			var tablename = me.ownerCt.tablename;
		        			me.valuePrint="{"+tablename+"."+me.name+"}>='"+from+"' and {"+tablename+"."+me.name+"}<='"+to+"'";
		        		}
	        		}
	        	},
	        	select:function(combo,records,eOpts){
					var con ="";
					var which = 'form';
					var cal = combo.dbCaller||caller;
					var key = combo.triggerName||combo.name;
				    Ext.each(records,function(data){
					   con = !Ext.isEmpty(data.data[Ext.util.Format.lowercase(combo.searchFieldArray)]) ? (combo.searchFieldArray + " = '" + data.data[Ext.util.Format.lowercase(combo.searchFieldArray)].replace(/\'/g,"''")  + "'") : null;
				    });
					me.setSelectValue(combo,which, cal, key, combo.getCondition(con)); //光标移开后自动dbfind
				}
	        }
	    });
	},
	setValue: function(v){
		var f = Ext.getCmp(window.onTriggerClick);
		if(f){
			f.setValue(v);
		}
		if(v != null && v != '' && contains(v, '~', true)){
			this.items.items[0].setValue(v.split('~')[0]);
    		this.items.items[1].setValue(v.split('~')[1]);
		}
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
    	if(this.items.items[0].value != null && this.items.items[0].value != ''){
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
    getFilter: function() {
    	var me = this, fromVal = me.from.getValue(), toVal = me.to.getValue();
    	return (fromVal || toVal) ? {
    		"gte": fromVal,
    		"lte": toVal
    	} : null;
    },
    setSelectValue: function(trigger,which, caller, field, condition) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'common/autoDbfind.action',
			params: {
				which: which,
				caller: caller,
				field: field,
				condition: condition,
				_config:getUrlParam('_config')
			},
			async: false,
			method: 'post',
			callback: function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				if (res.data) {
					var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					Ext.Array.each(res.dbfinds,function(ds){
						if (trigger.name.toUpperCase() == ds.field.toUpperCase()){
							trigger.setValue(data[0][ds.dbGridField]);
						}
					});
				} else {
					if (me.autoShowTriggerWin)
					me.onTriggerClick();
				}
			}
		});
	}
});