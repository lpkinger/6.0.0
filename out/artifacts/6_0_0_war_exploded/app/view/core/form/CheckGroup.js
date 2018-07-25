/**
 * DB: CG
 * value以逗号隔开
 */
Ext.define('erp.view.core.form.CheckGroup', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.checkgroup',
    layout: 'hbox',
    height: 22,
    value: "",
    items: [],
    separator: ',',
    initComponent : function(){ 
    	this.columnWidth = 1;
    	this.callParent(arguments);
    	var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsData.action',
	   		async: false,
	   		params: {
	   			caller: 'DbfindSetUI',
	   			fields: 'ds_whichdbfind,ds_likefield,ds_uifixedcondition',
	   			condition: 'ds_whichui=\'' + me.name + '\' AND ds_caller is null'
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
    			if(localJson.success){
    				if(localJson.data != null){
						me.getFieldValues(localJson.data.ds_whichdbfind, 
								localJson.data.ds_likefield, localJson.data.ds_uifixedcondition, me.name);
					}
	   			}
	   		}
		});
	},
	getFieldValues: function(caller, field, condition){
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldDatas.action',
	   		async: false,
	   		params: {
	   			caller: caller,
	   			field: field,
	   			condition: condition
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return null;
	   			}
    			if(localJson.success){
    				if(localJson.data != null){
    					me.setValue(localJson.data.replace(/#/g, me.separator));
    				}
	   			} else {
	   				return;
	   			}
	   		}
		});
	},
	changeValue: function(){
		var me = this,
			items = Ext.ComponentQuery.query('checkbox[checked=true]');
		if(items.length > 0) {
			me.value  = '';
			Ext.each(items, function(item){
				if(me.value != '') {
					me.value += ' OR ';
				}
				if(me.value ==''){
					me.value += item.boxLabel + "'";
				}else{
					me.value += me.name + "='" + item.boxLabel + "'";
				}
			});
		} else {
			me.value = null;
		}
		if(me.value != null){
			me.value = me.value.substring(0,me.value.length-1);
		}
	},
	listeners: {
    	afterrender: function(){
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    	}
    },
    reset: function(){
		
	},
    getValue: function(){
    	
    },
    isValid: function(){
    	return true;
    },
    setValue: function(value){
    	var me = this;
    	if(!Ext.isEmpty(value)){
    		var arr = value.split(me.separator);
    		Ext.each(arr, function(v, idx){
    			me.insert(idx, {
    				xtype: 'checkbox',
    				boxLabel: v,
    				name: me.name,
    				flex: 1,
    				listeners: {
    					change: function(f){
    						me.changeValue();
    					}
    				}
    			});
    		});
    	}
    },
    clear: function(){
    	this.removeAll();
    },
    checkValue: function(value){
    	var me = this;
    	if(!Ext.isEmpty(value)){
    		var arr = value.split(me.separator),
    			a;
    		Ext.each(me.items.items, function(item){
    			item.setValue(false);
    			item.hide();
    		});
    		Ext.each(arr, function(v, idx){
    			a = me.down('checkbox[boxLabel=' + v + ']');
    			if(a) {
    				a.show();
    			}
    		});
    	}
    }
});