/**
 * 多个checkbox
 */
Ext.define('erp.view.core.form.RadioGroup', {
	extend: 'Ext.form.RadioGroup',
	alias: 'widget.erpradiogroup',
	layout: 'hbox',
	baseCls: 'x-field',//fieldContainer默认为x-component x-container x-container-default
	initComponent : function(){ 
		this.callParent(arguments);
		var me = this;
		var logic=this.logic;
		var arr=logic.split(";");
		for(var i=0;i<arr.length;i++){
			me.insert(i, {
				xtype: 'radio',
				boxLabel:arr[i],
				name:this.name,
				style: {
					marginLeft: '10px'
				},
				readOnly:me.readOnly,
				inputValue:arr[i],
				checked:me.value==arr[i]

			});
		}
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
	isDirty:function(){
		return true;
	},
	
	setReadOnly: function(bool){
		Ext.Array.each(this.items.items,function(item){
			console.log(arguments);
			item.setReadOnly(bool);
		});
	},
	setFieldStyle: function(style){
		console.log(arguments);
//		this.items.items[0].setFieldStyle(style);
	}
});