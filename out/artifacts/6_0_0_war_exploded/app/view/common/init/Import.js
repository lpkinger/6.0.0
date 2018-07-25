Ext.define('erp.view.common.init.Import',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				region: 'center',
				layout: 'anchor',
				items: [{
					anchor: "100% 6%",
					xtype: 'panel',
					bodyStyle: 'background: #f7f7f7;',
					layout: {
						type: 'hbox'
					},
					defaults: {
						cls: 'x-btn-gray',
						margin: '1 10 0 0'
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
						height: 26,
						width: 90
					},{
						xtype: 'button',
						text: '匹配料号',
						id: 'matchingcode',
						hidden: true
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
						baseBodyCls: 'style="padding-top: 0px;"',
						boxLabel: '只显示错误行',
						boxLabelCls:'style="font-size:11px;' +
								'font-family:tahoma, arial, verdana, sans-serif;' +
								'font-weight: normal;' +
								'padding: 0 4px;"',
						id: 'onlyerror',
						hidden: true,
						height: 23,
						width: 100
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
					anchor: "100% 94%",
					xtype: 'panel',
					autoScroll: true,
					layout: 'border',
					items: [{
						title: title||'数据模板',
						xtype: 'panel',
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