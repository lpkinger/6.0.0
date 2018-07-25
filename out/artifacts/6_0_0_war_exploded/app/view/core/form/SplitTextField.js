/**
 * RadioGroup
 */
Ext.define('erp.view.core.form.SplitTextField', {
	extend: 'Ext.form.FieldContainer',
	alias: 'widget.splittextfield',
	layout: 'column',
	baseCls: 'x-field',//fieldContainer默认为x-component x-container x-container-default
	initComponent : function(){ 
		this.callParent(arguments);
		var me = this;
		var cw = me.columnWidth;
//		var logic = this.logic;
		me.insert(0,{
			xtype : 'textfield',
			name : me.name,
			readOnly : me.readOnly,
			columnWidth : 0.7,
//			cls:'form-field-gray',
			cls:me.cls,
			fieldStyle : me.fieldStyle
//			fieldStyle:'background:#FFFAFA;color:#515151;'
//			fieldStyle:'background:#f1f1f1;border: none;border-right: 2px solid #c6c6c6;border-left: 2px solid #c6c6c6;'
				
		});
		me.insert(1,{
			xtype : 'label',
			columnWidth : 0.3,
			text : me.logic
			

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
		var me = this;
		console.log(me);
		console.log(bool);
//		item.setReadOnly(bool);
		me.items.items[0].setReadOnly(bool);
		
	},
	
	setFieldStyle: function(style){
		this.items.items[0].setFieldStyle(style);
	}
});