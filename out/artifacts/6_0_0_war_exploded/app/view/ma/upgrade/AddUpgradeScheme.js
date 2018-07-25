Ext.define('erp.view.ma.upgrade.AddUpgradeScheme', {
	extend : 'Ext.Viewport',
	hideBorders : true,
	layout : 'border',
	id:'upgradescheme',
	initComponent : function() {
		var me = this;
		var formpanelConfig={xtype: 'panel',columnWidth: 1,border:0,layout:'table',margin: '10 0 10 0',
							 items:[{xtype:'textfield',emptyText:'输入caller',margin: '1 0 0 0'},
										{xtype:'button',text:'确定',
										handler:function(btn){
										  	var callerField=btn.ownerCt.down('textfield');
											var caller=callerField.value;
										   	var fieldset_c=btn.ownerCt.ownerCt;
									    	fieldset_c.insert(fieldset_c.items.length-1,{
												xtype: 'checkbox',
												name: caller,
												boxLabel:caller,
												checked:true
											});
											callerField.setValue('');
										    }
	   }]};	
	   var resultFormItems=[{
						xtype:'fieldset',
				        title: 'SQL语句',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 1},
						layout:'column',
				        type:'sql_',
				        items :[{xtype: 'panel',columnWidth: 1,border:0,layout:'column',margin: '10 0 10 0',
							 items:[{xtype:'textarea',columnWidth:1,grow:true}]}]
				   },{xtype:'fieldset',
				        title: '涉及的Form配置(Form、FormDetail、DbfindSetUI、RelativeSearch、RelativeSearchForm、RelativeSearchGrid)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 1/3},
						layout:'column',
				        type:'form_',
				        items :[formpanelConfig]
				   },{
						 xtype:'fieldset',
				        title: '涉及的Grid配置(DetailGrid、DbfindSet、DbfindSetDetail、DbfindSetGrid、gridbutton)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'detailgrid_',
				        items :[formpanelConfig]
				   },{
						 xtype:'fieldset',
				        title: '涉及的列表配置(DataList、DataListDetail)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'datalist_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的下拉配置(datalistcombo)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'combo_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的同步配置(PostStyle、PostStyleStep、PostStyleDetail)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'poststyle_',
				        items :[formpanelConfig]
				   },{
                        xtype:'fieldset',
				        title: '涉及的转单配置(Transfers、TransferDetail)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'transfers_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的出入库配置(DocumentSetup)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'documentsetup_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的初始化配置(Initialize、InitDetail)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'initialize_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的系统参数配置(Configs、ConfigProp)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'configs_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的业务逻辑配置(Interceptors)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'interceptors_',
				        items :[formpanelConfig]
				   },{
						 xtype:'fieldset',
				        title: '涉及的特殊权限(SysSpecialPower)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'sysspecialpower_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的查询方案配置(SearchTemplate、SearchTemplateGrid、SearchTemplateProp)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'searchtemplate_',
				        items :[formpanelConfig]
				   },{
						xtype:'fieldset',
				        title: '涉及的表、视图(DataCascade、DataRelation、DataLink)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'table_',
				        items :[{xtype: 'panel',columnWidth: 1,border:0,layout:'table',margin: '10 0 10 0',
							 items:[{xtype:'textfield',emptyText:'输入表名',margin: '1 0 0 0'},
										{xtype:'button',text:'确定',
										handler:function(btn){
										  	var callerField=btn.ownerCt.down('textfield');
											var caller=callerField.value;
										   	var fieldset_c=btn.ownerCt.ownerCt;
									    	fieldset_c.insert(fieldset_c.items.length-1,{
												xtype: 'checkbox',
												name: caller,
												boxLabel:caller,
												checked:true
											});
											callerField.setValue('');
										    }
	   					}]}]
				   },{
						xtype:'fieldset',
				        title: '涉及的其他对象(函数、过程、程序包)',
				        collapsible: true,
				        defaultType: 'textfield',
				        defaults: { columnWidth: 0.25},
						layout:'column',
				        type:'object_',
				        items :[{xtype: 'panel',columnWidth: 1,border:0,layout:'table',margin: '10 0 10 0',
							 items:[{xtype:'textfield',emptyText:'输入对象名',margin: '1 0 0 0'},
										{xtype:'button',text:'确定',
										handler:function(btn){
										  	var callerField=btn.ownerCt.down('textfield');
											var caller=callerField.value;
										   	var fieldset_c=btn.ownerCt.ownerCt;
									    	fieldset_c.insert(fieldset_c.items.length-1,{
												xtype: 'checkbox',
												name: caller,
												boxLabel:caller,
												checked:true
											});
											callerField.setValue('');
										    }
	   					}]}]
				   }];
		Ext.apply(me, {
			items : [{
				region : 'west',
				width : '25%',
				height : '100%',
				xtype : 'sysnavigationCheckTree'
			},{
				region: 'center',
				width: '75%',
				height : '100%',
				xtype:'form',
				id:'resultForm',
				autoScroll:true,
				defaults: {margin: '10 0 0 0'},
				tbar: [
				    {xtype: 'button', text: '检索',id:'check' ,style:'border-color: #bbbbbb;background:#fafafa;'},
				    {xtype: 'button', text: '刷新',id:'refresh' ,style:'border-color: #bbbbbb;background:#fafafa;',
				    handler:function(){
				    	//var me=this;
				    	var panel=Ext.getCmp('resultForm');
				    	panel.removeAll();
				    	panel.add(resultFormItems);
				    }
				    },'->',
				  	{xtype:'numberfield',hideTrigger:true,emptyText:'输入版本号',id:'version'},
				  	{xtype:'textfield',emptyText:'输入方案描述',id:'desc',width:300},
				  		{ xtype: 'button', text: '生成方案',id:'saveScheme',style:'border-color: #bbbbbb;background:#fafafa;' }
				],
				items: resultFormItems
			}]
		});
		me.callParent(arguments);
	}
});