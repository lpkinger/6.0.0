/**
* 
*/
Ext.define('erp.view.core.form.DetailTextField', {
	extend: 'Ext.form.FieldSet',
	id: 'dfield',
    alias: 'widget.detailtextfield',
    autoScroll:true,
    minHeight: 220,
    collapsible: true,
    title: '',
    style: 'background:#f1f1f1;',
    margin: '2 2 2 2',
    tfnumber: 0,
    initComponent: function() {
    	console.log(this);
    	this.columnWidth = 1;//强制占一行
    	this.cls = '';
    	this.callParent(arguments);
    	this.items.items[0].name = this.name;
    	this.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+this.fieldLabel);
    },
    layout:'column',
    items: [{
    	xtype: 'hidden',//该隐藏字段的值(附件在FilePath表的ID,用;隔开)将被保存到数据库
//    	id: 'file-hidden',
    	value: '',
    	fieldLabel: ''
    },{
    	xtype: 'form',
    	columnWidth: 1,
    	frame: false,
    	autoScroll:true,
    	minHeight: 220,
    	bodyStyle: 'background:#f1f1f1;',
    	bbar:[ {
 	    	iconCls: 'x-button-icon-add',
 	    	id: 'add',
 			text: '添加记录',
 			handler: function(btn){
 				btn.ownerCt.ownerCt.ownerCt.addItem(Ext.create('Ext.form.field.Text', {
					xtype: 'textfield',
					name: 'text' + ++btn.ownerCt.ownerCt.ownerCt.tfnumber,
					id: 'text' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					columnWidth: 0.95,
					labelWidth: 50,
					value: '',
					fieldLabel: '第&nbsp;' + btn.ownerCt.ownerCt.ownerCt.tfnumber +'&nbsp;条',
					fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
					listeners:{
						change: function(){
					    	var s = '';
							for(var i=1; i<=btn.ownerCt.ownerCt.ownerCt.tfnumber; i++){
								if(Ext.getCmp('text'+i).value != null && Ext.getCmp('text'+i).value.toString().trim() != ''){
									s += Ext.getCmp('text'+i).value + '==###==';
								}
							}
							btn.ownerCt.ownerCt.ownerCt.value = s;
						}
					}
				}));
 				btn.ownerCt.ownerCt.ownerCt.addItem(Ext.create('Ext.button.Button', {
 					text: '清&nbsp;空',
 					name: 'btn' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					id: 'btn' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					columnWidth: 0.05,
					index: btn.ownerCt.ownerCt.ownerCt.tfnumber,
					handler: function(btn){
				        Ext.getCmp('text'+btn.index).setValue('');
 			    	}
 				}));
 			}
 	    }]
    }],
    setValue: function(value){
    	this.value = value;
    },
    getValue: function(){
    	var value = '';
    	for(var i=1; i<=this.tfnumber; i++){
			if(Ext.getCmp('text'+i).value != null && Ext.getCmp('text'+i).value.toString().trim() != ''){
				value += Ext.getCmp('text'+i).value + '==###==';
			}
		}
    	return value;
    },
    clean: function(index){
    	Ext.getCmp('text'+index).setValue('');
    },
    listeners : {
    	afterrender: function(f){
			var me = this;
			if(f.value != null && f.value.toString().trim() != ''){
				var text = f.value.split('==###==');
				me.tfnumber = text.length-1;
				for(var i=1; i<=me.tfnumber; i++){
					me.addItem(Ext.create('Ext.form.field.Text', {
						xtype: 'textfield',
						name: 'text' + i,
						labelWidth: 50,
						id: 'text' + i,
						columnWidth: 0.95,
						value: text[i-1],
						fieldLabel: '第&nbsp;' + i +'&nbsp;条',
						fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
						listeners:{
							change: function(){
						    	var s = '';
								for(var i=1; i<=me.tfnumber; i++){
									if(Ext.getCmp('text'+i).value != null && Ext.getCmp('text'+i).value.toString().trim() != ''){
										s += Ext.getCmp('text'+i).value + '==###==';
									}
								}
								me.value = s;
							}
						}
					}));
					me.addItem(Ext.create('Ext.button.Button', {
	 					text: '清&nbsp;空',
	 					name: 'btn' + i,
						id: 'btn' + i,
						index: i,
						columnWidth: 0.05,
						handler: function(btn){
					        Ext.getCmp('text'+btn.index).setValue('');
	 			    	}
	 				}));
				}
			} else {
				me.tfnumber = 3;
				for(var i=1; i<=me.tfnumber; i++){
					me.addItem(Ext.create('Ext.form.field.Text', {
						xtype: 'textfield',
						name: 'text' + i,
						id: 'text' + i,
						labelWidth: 50,
						columnWidth: 0.95,
						value: '',
						fieldLabel: '第&nbsp;' + i +'&nbsp;条',
						fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
						listeners:{
							change: function(){
						    	var s = '';
								for(var i=1; i<=me.tfnumber; i++){
									if(Ext.getCmp('text'+i).value != null && Ext.getCmp('text'+i).value.toString().trim() != ''){
										s += Ext.getCmp('text'+i).value + '==###==';
									}
								}
								me.value = s;
							}
						}
					}));
					console.log(Ext.getCmp('text'+i));
					me.addItem(Ext.create('Ext.button.Button', {
	 					text: '清&nbsp;空',
	 					name: 'btn' + i,
						id: 'btn' + i,
						index: i,
						columnWidth: 0.05,
						handler: function(btn){
					        Ext.getCmp('text'+btn.index).setValue('');
	 			    	}
	 				}));
				}
			}
		}
    },
	addItem: function(item){
		this.add(item);
	}
});