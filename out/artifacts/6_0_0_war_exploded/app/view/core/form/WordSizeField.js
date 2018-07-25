Ext.define('erp.view.core.form.WordSizeField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.wordsizefield',
    layout : 'column', 
    combineErrors: true,
    msgTarget: 'under',
    height:22,
    defaults:{
    	hideLabel: true
    },
    initComponent : function(){
	    this.callParent(arguments);	
	    var data = this.getData();
	    this.add(this.getItems(data));
    },
    getItems:function(data){
    	return  [
             {xtype: 'combo', fieldLabel: 'size1', editable: true,name: 'size1', columnWidth: 0.3, allowBlank: false,margin: '0 15 0 0'},             
             {xtype: 'displayfield', value: '(',margin: '0 5 0 5',columnWidth: 0.04},
             {xtype: 'combo',    fieldLabel: 'size2', name: 'size2', columnWidth: 0.3, allowBlank: false, margin: '0 5 0 0',
             store: Ext.create('Ext.data.Store', {
            	 		fields: ['display', 'value'],
            	 		data : data,
    		    	}),
	    	     queryMode: 'local',
	    	     displayField: 'display',
	    	     valueField: 'value',
	    	     scope:this,
	    	     listConfig:{
	    	    	 maxHeight: 150,
	    	     },
	    	     value: new Date().getFullYear(),
             },
             {xtype: 'displayfield', value: ')',margin: '0 15 0 0',columnWidth: 0.05},
             {xtype: 'textfield',    fieldLabel: 'size3', name: 'size3', columnWidth: 0.3, allowBlank: false},
             {xtype: 'displayfield',value:'号'}
          ];
  
    },
    getData:function(){
       var array= new Array();
       var year=new Date().getFullYear();
       for(var i=year;i>year-21;i--){
    	   var object=new Object();
    	   object.display=i;
    	   object.value=year;
    	   array.push(object);
       } 
       return array;
    },
    setValue: function(value){
    	if(value != null && value.toString().trim() != ''){
    		this.items.items[0].setValue(value.trim().substring(0, value.lastIndexOf('(')));
    		this.items.items[2].setValue(value.trim().substring(value.lastIndexOf('(') + 1, value.lastIndexOf(')')));
    		this.items.items[4].setValue(value.trim().substring(value.lastIndexOf(')') + 1, value.lastIndexOf('号')));
    	}
    },
    getValue: function(value){
    	return this.items.items[0].value + "(" + this.items.items[2].value + ")" + this.items.items[4] + "号";
    },
    setReadOnly: function(bool){
    	this.items.items[0].setReadOnly(bool);
    	this.items.items[2].setReadOnly(bool);
    	this.items.items[4].setReadOnly(bool);
    },
    setFieldStyle: function(style){
    	this.items.items[0].setFieldStyle(style);
    	this.items.items[2].setFieldStyle(style);
    	this.items.items[4].setFieldStyle(style);
    },
    listeners: {
    	afterrender: function(){//去掉fieldContainer默认的高度和滚动样式
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    		this.setValue(this.value);
    		this.setFieldStyle(this.fieldStyle);
    		this.setReadOnly(this.readOnly);
    	}
    }
});