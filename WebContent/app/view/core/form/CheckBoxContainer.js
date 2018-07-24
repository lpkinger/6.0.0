Ext.define('erp.view.core.form.CheckBoxContainer', {
	extend: 'Ext.form.Panel',
	alias: 'widget.erpcheckboxcontainer',
	layout:'column',
	labelAlign:'top',
	bodyStyle: {
	    background: '#f0f0f0',
	    padding: '10px'
	},
	//baseCls: 'x-field',//fieldContainer默认为x-component x-container x-container-default
	initComponent : function(){ 
		this.callParent(arguments);
		var me = this;
		var logic=this.logic;
		this.title='<div style="color:black;fony-size:14px;">'+this.fieldLabel+'</div>';
		var arr=logic.split(";");
		var value=this.value;
		var checked=false;
			for(var i=1;i<arr.length+1;i++){
				if(value&&value.split(";")[i-1]==1){
					checked=true;
				}else checked=false;
				me.insert(i, {
					xtype: 'checkboxfield',
					fieldStyle: null,	
					fieldLabel :null,
                    cls : null,
					columnWidth:0.25,
					labelAlign:'right',
					boxLabel:arr[i-1],
					checked:checked,
					listeners:{
						change:function(field,newValue,oldValue){
							if(newValue){
								field.value=1;
								me.setFiledValue();
							}else field.value=-1;
						}
					}

				});
			}
			me.insert(0,{
				xtype:'hidden',
				name:this.name,
				id:this.name+"_checks",
				value:this.value
			});
	},
	listeners: {
		afterrender: function(){//去掉fieldContainer默认的高度和滚动样式
			this.getEl().dom.childNodes[1].style.height = 22;
			this.getEl().dom.childNodes[1].style.overflow = 'hidden';
		}
	},
	isValid: function(){
		return this.items.items[0].isValid();
	},
	setValue: function(value){
		this.value=value;
	},
	isDirty:function(){
		return true;
	},
	setFiledValue:function(field){
		var value="";
		var items=this.items.items;
		for(var i=1;i<items.length;i++){
			if(items[i].value){
				value+="1;";
			}else {
				value+="-1;";
			}
		}
		value=value.substring(0,value.length-1);
		this.items.items[0].setValue(value);
	},
	getValue: function(){
		return this.value;
	},
	setReadOnly: function(bool){
		this.items.items[0].setReadOnly(bool);
	},
	setFieldStyle: function(style){
		this.items.items[0].setFieldStyle(style);
	}
});