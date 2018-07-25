Ext.define('erp.view.common.JProcess.JprocessCommunicate',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	hideBorders: true,
	autoScroll: false,
	style: {
		background: '#D3D3D3',
	},
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	FormUitl:Ext.create('erp.util.FormUtil'),
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				id: 'Viewport',
				layout: 'auto',
				autoScroll: true,
				xtype: 'panel',
				style: {
					background: '#FFFFFF'
				},
				items: [{
					id: 'app-header',
					xtype: 'box',
					height: 5,
					style: 'background: #f0f0f0;color: #596F8F;font-size: 16px;font-weight: 200;padding: 5px 5px;text-shadow: 0 1px 0 #fff'
				},{
					xtype: 'toolbar',
					id: 'currentNodeToolbar',
					layout: {
						type: 'hbox',
						align: 'right'
					},
					bodyStyle: {
						background: '#f0f0f0',
						border: 'none'
					},
					style: {
						background: '#f0f0f0',
						border: 'none'
					},
					items: [{
						xtype: 'tbtext',
						id: 'processname'			
					},
					'->', {
						xtype: 'tbtext',
						id: 'label1',
						text: '<span style="font-weight: bold !important;font-size:18px">审批沟通</span>'
					},
					'->', {
						xtype: 'tbtext',
						id: 'label2'
					}]

				},{xtype: 'toolbar',
					layout: {
						type: 'hbox',
						align: 'right'
					},
					bodyStyle: {
						background: '#f0f0f0',
						border: 'none'
					},
					style: {
						background: '#f0f0f0',
						border: 'none',
						padding:'0 10 0 0'
					},
					items: ['->', {
						xtype: 'tbtext',
						id: 'currentnode'
					},
					'-', {
						xtype: 'tbtext',
						id: 'launchername'
					},
					'-', {
						xtype: 'tbtext',
						id: 'launchtime'
					}]
				}]

			}]
		});
		me.callParent(arguments); 
	},
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	listeners: {
		afterrender: function(){
			var me=this;
			var view=Ext.getCmp('Viewport');
			var taskId=getUrlParam('formCondition').split("=")[1];
			Ext.Ajax.request({ //获取当前节点对应的JProcess对象
				url: basePath + 'plm/task/getTaskInfo.action',
				params: {
					taskId: taskId,
					_noc: 1
				},
				success: function(response) {
					var res = new Ext.decode(response.responseText);
					var url = basePath + res.info.SOURCELINK;
					url += '&_noc=1&datalistId=NaN&NoButton=1'; // 不限制权限
					Ext.getCmp('currentnode').setText('当前节点:<font size=2 color="red">' + res.info.NAME + '</font>');
					Ext.getCmp('launchername').setText('当前节点:<font size=2 color="red">' + res.info.RECORDER + '</font>');              
					Ext.getCmp('launchtime').setText('发起时间:<font size=2 color="red">'+Ext.Date.format(new Date(res.info.RECORDDATE), "Y-m-d H:i:s")+'</font>');
					var panel=new Ext.panel.Panel({
						tag: 'iframe',
						id: 'mm',
						style: {
							background: '#f0f0f0',
							border: 'none'
						},
						frame: true,
						border: false,
						layout: 'fit',
						height: window.innerHeight,
						iconCls: 'x-tree-icon-tab-tab',
						html: '<iframe id="iframe_maindetail" name = "iframe_maindetail" src="' + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
					});
					view.insert(4,{id:'dbform',
						layout: 'column',
						autoScroll:true,
						buttonAlign:'center',
						bodyStyle: 'background:#f1f1f1;',
						frame:true,
						fieldDefaults: {
							labelWidth: 70,
							fieldStyle:'background:#FFFAFA;color:#515151;'
						},
						xtype:'form',
						items: [{
							xtype: 'textareafield',
							fieldLabel: '任务描述',
							id:'description',
							name: 'description',
							value:res.info.DESCRIPTION,
							readOnly:true,
							columnWidth: 0.5
						},{
							fieldLabel: '回复信息',
							xtype: 'textareafield',
							name: 'reply',
							id: 'reply',
							allowBlank:false,
							fieldStyle:'background:#fffac0;color:#515151;',
							columnWidth: 0.5
						}],
						buttons:[{
							xtype:'button',
							text:'回复',
							iconCls: 'x-button-icon-save',
							cls: 'x-btn-gray',
							formBind: true,
							width: 60,
							style: {
								marginLeft: '10px'
							},
							handler:function(){
								me.replyCommmunicate(taskId);
							}
						},{
							xtype:'button',
							text: $I18N.common.button.erpCloseButton,
							iconCls: 'x-button-icon-close',
							cls: 'x-btn-gray',
							width: 60,
							style: {
								marginLeft: '10px'
							},
							handler:function(btn){
								me.BaseUtil.getActiveTab().close();
							} 
						}]
					});
					view.insert(5,panel);

				}
			});
		}
	},
	replyCommmunicate:function(taskId){
		var me=this;
		var FormUtil=Ext.create('erp.util.FormUtil');
		FormUtil.setLoading(true);
		var reply=Ext.getCmp('reply').value;
		Ext.Ajax.request({
			url : basePath + 'common/replyCommunicateTask.action',
			params: {
				taskId:taskId,
				reply:reply
			},
			method : 'post',
			callback : function(opt, s, res){
				FormUtil.setLoading(false);
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);return;
				} else if(r.success){
					if (parent && parent.Ext.getCmp('content-panel')) {
						var firstGrid = parent.Ext.getCmp('content-panel').items.items[0].firstGrid;
						if (firstGrid && firstGrid != null) {
							firstGrid.loadNewStore();
						}
					}
					showMessage('提示','回复成功',1000);
					FormUtil.onClose();	
				}
			}
		});
	},
	endCommunicate: function(){

	}
});