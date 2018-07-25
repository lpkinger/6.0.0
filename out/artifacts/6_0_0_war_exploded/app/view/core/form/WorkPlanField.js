/**
* 列出之前计划条目
*/
Ext.define('erp.view.core.form.WorkPlanField', {
	extend: 'Ext.form.FieldSet',
	id: 'wpfield',
    alias: 'widget.workplanfield',
    autoScroll:true,
    //height: 220,
    collapsible: true,
    collapsed: true,
    title: '',
    style: 'background:#f1f1f1;',
    margin: '2 2 2 2',
    tfnumber: 0,
    initComponent: function() {
    	console.log(this);
    	this.columnWidth = 1;//强制占一行
    	this.cls = '';
    	this.callParent(arguments);
    	this.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+this.title);
    },
    layout:'column',
    items: [],
    setValue: function(value){
    	this.value = value;
    },
    listeners : {
    	afterrender: function(f){
			var me = this;
			if(f.value != null && f.value.toString().trim() != ''){
				var text = f.value.split('==###==');
				me.tfnumber = text.length;
				me.setTitle(this.title+'('+me.tfnumber+')');
				for(var i=1; i<=me.tfnumber; i++){
					me.addItem(Ext.create('Ext.form.field.Text', {
						xtype: 'textfield',
						name: 'text' + i,
//						id: 'text' + i,
						columnWidth: 1,
						labelWidth: 30,
						readOnly: true,
						value: text[i-1],
						fieldLabel: i +'&nbsp;',
						fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
						listeners:{
							
						}
					}));
				}
			} else {
				me.addItem(Ext.create('Ext.form.field.TextArea', {
					xtype: 'textareafield',
					name: 'empty',
//					id: 'empty',
					columnWidth: 1,
					height: 30,
					frame: false,
					readOnly: true,
					value: '无数据',
					fieldLabel: '',
					listeners:{
						
					}
				}));
			}
//			me.expand(true);
		}
    },
	addItem: function(item){
		this.add(item);
	}
});