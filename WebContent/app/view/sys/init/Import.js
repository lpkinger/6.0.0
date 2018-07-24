Ext.define('erp.view.sys.init.Import',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{xtype:'panel',region: 'north',
					bodyStyle: 'background:white; style:"margin:1px 2px 0px 2px;"',cls:'import-panel',
					items:[{xtype:'label',width:60,
							html:'<div style="background:#F0F0F0; padding-left:5px; margin:4px 5px 2px 5px">第一步：下载模板;<br>' +
						 '第二步：仔细阅读模板，参考模板整理数据后导入数据;<br>' +
						 '第三步：检验数据，如果数据检验不通过将不能导入数据库，当检验不通过时请按系统提示修改错误数据，并重新检验;<br>' +
						 '第四步：检验通过后，转入正式.</div>'
						}]
			},{
				xtype: 'panel',
				region: 'center',
				layout: 'anchor',height:'28px',bodyStyle: 'background:white;',
				items: [{
					xtype: 'panel',
					layout: {
						type: 'hbox'
					},
					defaults: {
						cls: 'custom-button',
						margin: '1 0 0 1'
					},
					items: [{
						xtype: 'button',
						text: '下载模板',
						iconCls: 'export',
						id: 'export'
					},{
						xtype: 'upexcel',
						text: '导入数据',
						iconCls: 'upexcel',
						itemCls: 'up',
						id: 'upexcel',
						hidden: true,
						height: 24,
						width: 90
					},{
						xtype: 'button',
						text: '导出所有数据',
						iconCls: 'upexcel',
						id: 'alldownload',
						hidden: true
					},{
						xtype: 'button',
						text: '导出错误数据',
						iconCls: 'upexcel',
						id: 'errdownload',
						hidden: true
					},{
						xtype: 'button',
						text: '删除错误数据',
						iconCls: 'delete',
						id: 'errdelete',
						hidden: true
					},{
						xtype: 'button',
						text: '保存修改',
						iconCls: 'save',
						id: 'saveupdates',
						hidden: true
					},{
						xtype: 'button',
						text: '导出错误数据',
						iconCls: 'upexcel',
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
						iconCls: 'check',
						id: 'check',
						hidden: true
					},{
						xtype: 'button',
						text: '转入正式',
						iconCls: 'save',
						id: 'toformal',
						hidden: true
					},{
						xtype: 'button',
						text: '保存到示例数据',
						iconCls: 'save',
						id: 'todemo',
						hidden: true
					}]
				},{
					anchor:"100% 95%",
					xtype: 'panel',
					autoScroll: true,
					layout: 'border',
					items: [{
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