Ext.define('erp.view.common.init.Template',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		var title = getUrlParam('title');//从url解析参数
		title = (title == null) ? "数据模板" : title.replace(/IS/g,"=");
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				id: 'tree',
				region: 'west',
				width: '20%',
				layout: 'accordion',
				title:'初始化导航'
			},{
				xtype: 'panel',
				region: 'center',
				layout: 'anchor',
				items: [{
					anchor: "100% 5%",
					xtype: 'panel',
					bodyStyle: 'background: #f1f2f5;',
					layout: {
						type: 'hbox'
					},
					defaults: {
						cls: 'custom-button',
						margin: '1 0 0 1'
					},
					items: [{
						xtype: 'button',
						text: '导入历史',
						//iconCls: 'history',
						id: 'history'
					},{
						xtype: 'button',
						text: '导入配置',
						//iconCls: 'rule',
						id: 'rule'
					},{
						xtype: 'button',
						text: '下载模板',
						//iconCls: 'export',
						id: 'export'
					},{
						xtype: 'upexcel',
						text: '导入数据',
						iconCls: 'upexcel',
						itemCls: 'up',
						id: 'upexcel',
						hidden: true,
						height: 23,
						width: 90
					},{
						xtype: 'button',
						text: '导出所有数据',
						//iconCls: 'upexcel',
						id: 'alldownload',
						hidden: true
					},{
						xtype: 'button',
						text: '导出错误数据',
						//iconCls: 'upexcel',
						id: 'errdownload',
						hidden: true
					},{
						xtype: 'button',
						text: '删除错误数据',
						//iconCls: 'delete',
						id: 'errdelete',
						hidden: true
					},{
						xtype: 'button',
						text: '保存修改',
						//iconCls: 'save',
						id: 'saveupdates',
						hidden: true
					},{
						xtype: 'button',
						text: '导出错误数据',
						//iconCls: 'upexcel',
						id: 'errdownload',
						hidden: true
					},{
						xtype: 'checkbox',
						boxLabel: '只显示错误行',
						id: 'onlyerror',
						hidden: true,
						height: 23,
						width: 120
					},{
						xtype: 'button',
						text: '校验数据',
						//iconCls: 'check',
						id: 'check',
						hidden: true
					},{
						xtype: 'button',
						text: '转入正式',
						//iconCls: 'save',
						id: 'toformal',
						hidden: true
					},{
						xtype: 'button',
						text: '保存到示例数据',
						//iconCls: 'save',
						id: 'todemo',
						hidden: true
					}]
				},{
					anchor: "100% 95%",
					xtype: 'panel',
					autoScroll: true,
					layout: 'border',
					items: [{
						title: '数据模板',
						xtype: 'panel',
						bodyStyle: 'background: #f1f2f5;',
						region: 'center',
						height: '100%',
						width: '100%',
						id: 'template',
						layout: 'anchor',
						autoScroll: true
					}]
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});