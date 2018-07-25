Ext.QuickTips.init();

Ext.define('erp.view.ma.ObjectExplainFormPanel', {
	extend:'erp.view.core.form.Panel',	
	alias:'widget.erpObjectExplainFormPanel',
	id:'form',
	layout:'column',
	title:'存储过程说明 ',
	keyField:'name_',
	tablename:'object$explain',
	defaults:{
		xtype: "textfield", 
		columnWidth:0.25,
		allowBlank: true, 
      	cls: "form-field-allowBlank", 
      	fieldStyle: "background:#FFFAFA;color:#515151;", 
      	editable: true, 
      	labelAlign: "left",
      	readOnly: false    	
	},
	enableKeyEvents:false,
	getItemsAndButtons:function(){
		var me = this;
		Ext.apply(me,{
			items:[{
				fieldLabel: "对象名称", 
				dataIndex:'name_',
				id:'name_',
				name:'name_', 
				labelStyle:"color:#FF0000",
				readOnly: true,
				allowBlank: false 
			},{
				fieldLabel: "存储过程标题", 
				dataIndex:'title_',
				id:'title_',
				name:'title_',  
				labelStyle:"color:#FF0000",
				columnWidth:0.5,
				allowBlank: false 
			},{
				xtype:'combo',
				fieldLabel: "是否标准", 
				labelStyle: "color:#FF0000",
				allowBlank: false, 
				dataIndex:'standard_',
				id:'standard_',
				name:'standard_',
				displayField:'display',
				valueField:'value',
				editable: false, 
				store: Ext.create('Ext.data.Store', {
					fields: ['display', 'value'],
					data:[{
						display:'是',
						value:-1
					},{
						display:'否',
						value:0
					}]
				})
			},{
				fieldLabel: "最近更新人", 
				dataIndex:'man_',
				id:'man_',
				name:'man_',
				readOnly: true
			},{
				xtype:'datefield',
				fieldLabel: "最近更新时间", 
				dataIndex:'date_',
				id:'date_',
				name:'date_',
				readOnly: true
			},{
				xtype:'textareatrigger',
				fieldLabel: "接口位置说明", 
				dataIndex:'interface_',
				id:'interface_',
				name:'interface_',
				columnWidth:0.5
			},{
				xtype:'textareafield',
				fieldLabel: "描述", 
				dataIndex:'desc_',
				id:'desc_',
				name:'desc_', 
				columnWidth:1,
				cls: "" 
			},{
				xtype:'mfilefield',
				fieldLabel: "说明文档", 
				dataIndex:'attach_',
				id:'attach_',
				name:'attach_'
			}],
			buttons:[{
				xtype:'erpSaveButton',
				height:27
			},{
				xtype:'erpCloseButton',
				height:27
			}]
		});
		me.setValue();
	},
	setValue:function(){
		var me = this;
		Ext.Ajax.request({
			url:basePath + 'ma/objectexplain/getData.action',
			params:{
				condition:"name_='" + objectName + "'"
			},
			method:'post',
			callback:function(options,success,response){
				var res = Ext.decode(response.responseText);
				if(res.success){
					if(res.name_){
						me.getForm().setValues(res);
						if(res.attach_){
							var attachField = Ext.getCmp('attach_');						
							if(attachField){
								attachField.setValue(res.attach_);
								attachField.download(attachField.value,attachField.name);
							}
						}
					}else{
						Ext.getCmp('name_').setValue(objectName);
					}
				}
			}
		});
	}
});
