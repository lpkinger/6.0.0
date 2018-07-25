/**
 * 原HrOrgSelectField使用的人员选择window抽离
 */
Ext.define('erp.view.core.window.HrOrgSelectWindow', {
	extend: 'Ext.window.Window',
	ailas: 'widget.HrOrgSelectWin',
	title: '<div align="center" class="WindowTitle">选择对象</div>',
	modal: true,
	width:'80%',
	height:'90%',
	layout:'border',
	requires:['erp.view.oa.doc.ItemSelector'],
	isSpecial:false,//贝腾代理商使用
	initComponent: function() {
		var me = this;
		Ext.apply(me, {
			items: [{
				region:'center',
				layout:'border',
				items:[{
					region:'north',
					xtype:'form',
					bodyPadding: 10,
					layout:'column',
					bodyStyle:'background:#fafafa;',
					items:[{
						xtype:'textfield',
						margin:'0 0 0 20',
						fieldLabel:'快速搜索',
						labelStyle:'font-weight:bold;',
						columnWidth:0.8
					},{
						xtype:'button',
						id:'search',
						text:'搜索',
						cls:'button1 pill',
						style:'margin-left:5px;',
						width:60,
						handler:function(btn){
							btn.setDisabled(true);
							var likestring=btn.ownerCt.items.items[0].value;
							if(!likestring) {
								showMessage('提示','请输入需要搜索的信息!',1000);
								btn.setDisabled(false);
								return;
							}
							Ext.Ajax.request({//查询数据
								url : basePath +(me.isSpecial?'common/ProcessQueryAgentPersons.action':'common/ProcessQueryPersons.action'),
								params:{
									likestring:likestring
								},
								callback : function(options,success,response){
									var res = new Ext.decode(response.responseText);													
									if(res.data){
										Ext.getCmp('itemselector-field').fromField.store.loadData(res.data);
										btn.setDisabled(false);
									} else if(res.exceptionInfo){
										showError(res.exceptionInfo);
									}
								}
							});
						}
					}]
				},{
					region:'center',
					xtype: 'itemselector',
					data:me.data||[],
					anchor: '100%',	
					id: 'itemselector-field',
					displayField: 'text',
					valueField: 'value',
					allowBlank: true,
					msgTarget: 'side',
					listeners:{
						afterrender:function(field){
							console.log(field.data);
							//field.toField.store.loadData(field.data);
						}
					}
				}],
				buttonAlign:'center',
				buttons:['->',{
					cls:'button1 pill',
					style:'margin-left:5px;',
					text:'确认',
					scope:this,
					handler:function(btn){
						var itemselector=Ext.getCmp('itemselector-field');
						var value=itemselector.getRawValue();
						
						var toField=itemselector.toField,displayValue;
						if (toField) {
							displayValue = Ext.Array.map(toField.boundList.getStore().getRange(), function(model) {
								return model.get(itemselector.displayField);
							});
						}	
						me.getValue(displayValue,value);
						if(me.setFieldValue) {
							me.setFieldValue(me.value, me.secondvalue);
						}
						btn.ownerCt.ownerCt.ownerCt.close();												
						
					}
				},{
					cls:'button1 pill',
					style:'margin-left:5px;',
					text:'关闭',
					handler:function(btn){
						btn.ownerCt.ownerCt.ownerCt.close();
					}
				},'->']
			},{
				region:'west',
				width:'40%',
				margins: '0 0 0 0',
				border:'0 0 0 0',
				layout: 'accordion',
				items: [{					    
					title: '公司组织架构',
					xtype:'erpOrgTreePanel',
					bodyStyle:'background:#fafafa;',
					isSpecial:me.isSpecial
				},{
					title: '个人通讯组',
					itemId:'personal'
		
				},{
					title: '最近联系人',
					itemId:'recent'
				}]
		
			}]
		});
		me.callParent(arguments);
	},
	getValue:function(displayValue,rawValue){
		var me=this;
		me.value=displayValue.join(";");
		me.secondvalue=rawValue.join(";");
		return {
			value: me.value,
			secondvalue: me.secondvalue
		}
	}
});