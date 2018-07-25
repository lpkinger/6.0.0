Ext.define('erp.view.oa.flow.flowEditor.FlowJudgeNodeEditor',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		var shortName = getUrlParam('shortName');
		var remark = me.getRemark(shortName,name);
		Ext.apply(me, { 
			items: [{ 
				id:'judgeeditor',
				xtype:'panel',
				layout: 'fit', 
				cls:'judgeeditor',
				bbar: ['->',{
					xtype:'button',
					cls:'x-btn-gray',
					text:'保存',
					margin:'0 0 0 2',
					handler:function(){
						Ext.Ajax.request({
							url : basePath + 'oa/flow/saveJudgeNode.action',
							async: false,
							params:{
								shortName:shortName,
								operationName:name,
								remark:Ext.getCmp('remark').value
							},
							callback : function(options,success,response){
								var rs = new Ext.decode(response.responseText);
								if(rs.exceptionInfo){
									showError(rs.exceptionInfo);return;
								}
								Ext.Msg.alert('提示', '保存成功', function(){
									window.location.reload();
								});
							}
						});
						
					}
				},{xtype:'splitter',width:10},{
					xtype:'button',
					cls:'x-btn-gray',
					text:'关闭',
					margin:'0 5 0 0',
					handler:function(){
						parent.Ext.getCmp('nodeEdit').close()
					}
				},'->'],
				items: [{
					xtype: 'panel',
					id:'judgePanel',
					layout: 'column', 
					margin:'10 5 0 0',
					items:[{
						fieldLabel: '操作名称',
						name: '操作名称',
						columnWidth:1,
						xtype:'textfield',
						labelAlign: 'left',
						readOnly:true,
						editable:false,
						value:name,
						cls: "form-field-allowBlank"
					},{
						fieldLabel: '描述',
						id: 'remark',
						readOnly:false,
						value:remark,
						xtype: "textareafield",
					    maxLength: 300,
				        maxLengthText: "字段长度不能超过300字符!",
				        hideTrigger: false,
				        editable: true,
				        columnWidth: 1,
				        allowBlank: true,
				        cls: "form-field-allowBlank",
				        labelAlign: "left",
				        allowDecimals: true
					}]
				}]
			}]
		});
		me.callParent(arguments);
	},
	getRemark:function(shortName,name){
		var Data;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fn_remark',
				caller : 'flow_node',
				condition : 'fn_fdshortname = \''+ shortName +'\' and fn_nodename = \''+ name +'\''
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					Data = item.FN_REMARK
				});
			}
		});
		return Data;
	}
});