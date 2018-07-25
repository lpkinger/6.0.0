Ext.define('erp.view.core.form.ArgsField', {
	extend: 'Ext.form.FieldSet',
    alias: 'widget.argsfield',
    autoScroll:true,
    collapsible: true,
    collapsed: false,
    title: '',
    height: 22,
    style: 'background:#f1f1f1;',
    margin: '2 2 2 2',
    initComponent: function() {
    	this.columnWidth = 1;//强制占一行
    	this.cls = '';
    	this.callParent(arguments);
    	this.setTitle('<span><img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;参数设置</span>');
    },
    layout:'column',
    setValue: function(value){
    	this.value = value;
    },
    getValue:function(){
    	
    	return "niubi";
    },
    items:[],
    listeners : {
    	afterrender: function(f){
			var me = this;
			if(f.value != null && f.value.toString().trim() != ''){
				var text = f.value.split(',');
				for(var i=1; i<=text.length; i++){
					me.addItem(Ext.create('erp.view.core.form.ArgTypeField', {
						xtype: 'argtypefield',
						name: '参数' + i,
						columnWidth: 0.33,
						labelWidth: 50,
						readOnly: true,
						value: text[i-1],
						fieldStyle:"background:#fffac0;color:#515151;",
						fieldLabel:'参数' + i,						
						listeners:{
							
						}
					}));
				}
			} else {
				me.addItem(Ext.create('Ext.form.field.TextArea', {
					xtype: 'textareafield',
					name: 'empty',
					columnWidth: 1,
					height: 30,
					frame: false,
					readOnly: true,
					value: '无参数',
					fieldLabel: '',
					listeners:{
						
					}
				}));
			}
		}
    },
	addItem: function(item){
		this.add(item);
	}
});