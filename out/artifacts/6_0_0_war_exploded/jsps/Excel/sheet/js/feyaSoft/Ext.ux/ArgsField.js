Ext.form.ArgsField= Ext.extend(Ext.Container, {
    value:'',
    usefield:true,
   layout: 'column',
    argnum:'',
    layoutConfig: {columns: this.argnum},  
    initComponent : function(){ 
    	Ext.form.ArgsField.superclass.initComponent.call(this);
    	var me = this;
    	var args=this.value.split(",");
    	for(var i=0;i<args.length;i++){
    		var xtype=args[i].split(";")[1];
    		var name=args[i].split(";")[0];
    		if(i==0){
    		me.insert(i, {
        		xtype:xtype,
        		columnWidth:0.2,
        		hideLabel: true,   
        		allowBlank:false,
        		blankText:'必填项',
        	    name:  name,
        	    listeners: {
    	        	change: function(field, newValue, oldValue ){
                        field.value=newValue;
    	        	}
    	        }
        	});
    		}else {
    			me.insert(i, {
            		xtype:xtype,
            		columnWidth:0.2,
            		align:'right',
            		allowBlank:false,
            		blankText:'必填项',
            	    name:  name,
            	    listeners: {
        	        	change: function(field, newValue, oldValue ){
                            field.value=newValue;
        	        	}
        	        }
            	});
    			
    			
    		}
    	}    
	},	
	reset: function(){
		var me = this;
		
	},
	
	listeners: {
    	afterrender: function(){
    		//this.getEl().dom.style.height = 22;
    		this.getEl().dom.style.overflow = 'hidden';
    	}
    },
    getValue: function(){//以;隔开{类型;值}
    	var me = this;
    	var items=me.items.items;
    	var arr=new Array();
    	//标示 这是单元格条件
    	for(var i=0;i<items.length;i++){
    		arr.push(items[i].name+";"+items[i].value);
    	}
    	if(arr.length>0){
    	return me.fieldLabel+":"+arr.toString();
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
Ext.reg('argsfield', Ext.form.ArgsField);