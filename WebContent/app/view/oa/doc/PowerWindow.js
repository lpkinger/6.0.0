Ext.define('erp.view.oa.doc.PowerWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.powerwindow',
	id:'powerwindow',
	requires:['erp.view.oa.doc.ItemSelector'],
	height: 600,
	width: 700,
	closeAction: 'destroy',
	title:'<div align="center" class="WindowTitle">设置权限</div>',
	layout:'border',
	items:[{
		region:'center',
		layout:'border',
		items:[{
			region:'north',
			xtype:'form',
			bodyPadding: 10,
			layout:'column',
			bodyStyle:'background:#fafafa;',
			items:[{
				columnWidth:1,
				xtype: 'checkboxgroup',
				id:'checkboxgroup',
				padding:'0 0 0 20',
				fieldStyle : "background:#FFFAFA;color:#515151;",
				defaults: {
					margin: '0 5 0 0'
				},
				layout:'column',
				items: [{ boxLabel: '管理', name: 'DP_CONTROL', inputValue: '1'},
				        { boxLabel: '浏览', name: 'DP_SEE', inputValue: '1'   },
				        { boxLabel: '创建(编辑、上传)', name: 'DP_SAVE', inputValue: '1' },
				        { boxLabel: '阅读', name: 'DP_READ', inputValue: '1'   },
				        { boxLabel: '删除', name: 'DP_DELETE', inputValue: '1' },
				        //{ boxLabel: '打印', name: 'DP_PRINT', inputValue: '1'  },
				        { boxLabel: '下载', name: 'DP_DOWNLOAD', inputValue: '1'}]
			},{
				xtype:'textfield',
				margin:'0 0 0 20',
				fieldLabel:'快速搜索',
				id:'searchcontent',
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
					var condition = Ext.getCmp('searchcontent');
					var likestring=btn.ownerCt.items.items[0].value;
					likestring = condition.getValue();
					if(!likestring) {
						showMessage('提示','请输入需要搜索的信息!',1000);
						btn.setDisabled(false);
						return;
					}
					Ext.Ajax.request({//查询数据
						url : basePath +('common/ProcessQueryPersons.action'),
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
			},{
				xtype: 'radiogroup',
				fieldLabel: '应用到子目录',
				labelStyle:'font-weight:bold;',
				id: 'applySubs',
				columnWidth: 1,
				defaults: {
					flex: 1
				},
				layout: 'hbox',
				items: [{
					boxLabel: '是',
					name: 'sub',
					inputValue: '1',
					margin: '0 20 0 20',
					id: 'radioyes',
					checked: true
				},{
					boxLabel: '否',
					name: 'sub',
					inputValue: '0',
					margin: '0 0 0 10 ',
					id: 'radiono'
				}]
			}]
		},{
			region:'center',
			xtype: 'itemselector',
			anchor: '100%',	
			id: 'itemselector-field',
			displayField: 'text',
			valueField: 'value',
			allowBlank: false,
			msgTarget: 'side'
		}],
		buttonAlign:'center',
		buttons:['->',{
			cls:'button1 pill',
			style:'margin-left:5px;',
			text:'保存',
			scope:this,
			handler:function(btn){
				btn.ownerCt.ownerCt.ownerCt.savePower(btn);
			}
		},{
			cls:'button1 pill',
			style:'margin-left:5px;',
			text:'关闭',
			handler:function(btn){
				btn.getEl().dom.disabled = false;
				Ext.getBody().unmask();
				btn.ownerCt.ownerCt.ownerCt.close(btn);
			}
		},'->']
	},{
		region:'west',
		width:'40%',
		xtype:'erpOrgTreePanel',
		bodyStyle:'background:#fafafa;'
	}],
	initComponent: function() {
		this.callParent(arguments);
	},
	savePower:function(btn){
		var checkboxgroup=Ext.getCmp('checkboxgroup');
		var selectorvalue=Ext.getCmp('itemselector-field').getModelData();
		var values=checkboxgroup.getValue();
		var keys=Ext.Object.getKeys(values);
		if(keys.length>0 && selectorvalue.length>0){
			Ext.Ajax.request({//拿到form的items
				url : basePath + 'doc/docmentPowerSet.action',
				params: {
					folderId:CurrentFolderId,
					powers: unescape(escape(Ext.JSON.encode(values))),
					objects:unescape(selectorvalue.toString()),
					sub: Ext.getCmp('applySubs').getValue().sub,				//是否应用到子目录
				},
				method : 'post',
				callback : function(options, success, response){
					//btn.ownerCt.ownerCt.ownerCt.close(btn);
					var res = Ext.decode(response.responseText);
					if(res.success){
						showResult('提示','设置成功!',btn);
						var activetab=Ext.getCmp('2');
						activetab.items.items[0].loadNewStore();
					}
					/*var activetab=Ext.getCmp('doctab').getActiveTab();
					activetab.items.items[0].loadNewStore();*/
				}
			});
		}else  {
			showResult('提示','未选择设置角色或权限!',btn);
		}
	}
});

