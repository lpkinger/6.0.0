Ext.define('erp.view.core.form.ScopeField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.erpFtNumberField',
    layout: 'column',
    value: "",
    items: [],
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this;
    	me.insert(0, {
	        xtype: 'numberfield',
	        maxValue:99,
	        minValue:0, 
	        id: me.name + '_from',
	        name: me.name + '_from',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	blur: function(f){
	        	  if(f.value<0||f.value>100){
	        	  f.reset();
	        	  }
	        		var to = me.items.items[1].value;
                    if(to&&to!=''&&f.value>to){
                       f.reset();
                       showError('起始值不能大于结束值');
                       return;
                    }  
	        	}
	        }
	    });
    	me.insert(1, {
	        xtype: 'numberfield',
	        columnWidth: 0.5,
	        maxValue:100,
	        minValue:0,
	        id: me.name + '_to',
	        name: me.name + '_to',
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	blur: function(f){
	        		var from = me.items.items[0].value;
	        		var to = f.value;
	        		 if(f.value<0||f.value>100){
	        	      f.reset();
	        	  }	        	
                  if(from&&from>to){
                       f.reset();
                       showError('结束值不能小于起始值');
                       return;
                  }
	        	}
	        }
	    });
    	if(me.value){
    		var value=me.value;
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
    }
});