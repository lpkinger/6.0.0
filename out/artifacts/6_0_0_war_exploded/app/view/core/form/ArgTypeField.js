Ext.define('erp.view.core.form.ArgTypeField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.argtypefield',
    height: 22,
    layout: 'column',
    items: [],
    initComponent : function(){
    	this.callParent(arguments);
    	var me = this;
    	me.insert(0, {
	        xtype: 'textfield',
	        columnWidth: 0.6,
	        editable: false,
	        emptyText:'参数名',
	        allowBlank:false,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(field,newValue){
	        		me.value=field.value+";"+me.items.items[1].getValue();
	        	},
	        	blur:function(field){
	        		var value=field.value;
	        		if(value){
	        		if(!/^#/.test(value)){
	        		field.setValue("#"+field.value);
	        		}
	        		}
	        	}
	        }
	    });
    	me.insert(1, {
    		xtype: 'combo',
    		columnWidth: 0.4,
    		editable: false,
    		allowBlank:false,
    		fieldStyle: 'background:#C1CDC1',
    		store: Ext.create('Ext.data.Store', {
    		    fields: ['display', 'value'],
    		    data : [
    		        {"display":"-参数类型-", "value":"0"},
    		        {"display":"字符", "value": "textfield"},
    		        {"display":"数值", "value": "numberfield"},
    		        {"display":"日期", "value": "datefield"}
    		    ]
    		}),
    	    queryMode: 'local',
    	    displayField: 'display',
    	    valueField: 'value',
    	    value: "0",
    	    listeners: {
    	    	select: function(combo, records, obj){
    	    		me.value=me.items.items[0].getValue()+";"+combo.value;
    	    	}
    	    }
    	});
	},
	setValue: function(v){
		var text=v.split(";");
		 this.items.items[0].setValue(text[0]);
		 this.items.items[1].setValue(text[1]);
	},
	listeners: {
    	afterrender: function(field){
    		if(field.value){
    			field.setValue(field.value);
    		}
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    	}
    }
});