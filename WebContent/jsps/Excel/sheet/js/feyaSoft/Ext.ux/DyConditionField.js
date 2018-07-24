Ext.form.DyConditionField= Ext.extend(Ext.Container, {
	usefield:true,
    type:'',
    caption:'',
    relation:'',
    layout: 'column',
    value:'',
    name:'',
    initComponent : function(){ 
    	Ext.form.DyConditionField.superclass.initComponent.call(this);
    	var me = this;
    	me.insert(0, {
    		xtype: 'displayfield',
    		columnWidth:0.4,
    		value: me.caption,
    		align:'right',
    	    name:  me.name
    	});
    	me.insert(1,{
    		xtype: 'displayfield',
    		value: me.relation,
    		columnWidth:0.2,
    	    name:  me.name+"_relation"
		});
    	me.insert(2, {			
			xtype:me.type,
			columnWidth:0.4,
			hideLabel:true,
			name:me.name+"_condition",
			listeners: {
	        	change: function(field, newValue, oldValue ){
                    field.value=newValue;
	        	}
	        }
    	});
    	
	},	
	reset: function(){
		var me = this;
		me.items.items[0].reset();
		me.items.items[1].reset();
	},
	
	listeners: {
    	afterrender: function(){
    		//this.getEl().dom.style.height = 22;
    		this.getEl().dom.style.overflow = 'hidden';
    	}
    },
    getValue: function(){//以;隔开{类型;值}
    	var me = this;
    	if(me.items.items[2].value){
    	return me.items.items[0].name + ';' + me.items.items[1].value+";"+me.items.items[2].value;
    	}
    	else return null;
    },
    setValue: function(value){
    	if(value != null && value.toString().trim() != ''){
    		this.items.items[2].setValue(value);
    		
    	}
    },
    isValid: function(){
    	return true;
    }
});
Ext.reg('dyconfield', Ext.form.DyConditionField);