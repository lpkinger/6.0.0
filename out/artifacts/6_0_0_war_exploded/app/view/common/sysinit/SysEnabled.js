Ext.define('erp.view.common.sysinit.SysEnabled', {
	extend : 'Ext.Viewport',
	layout : 'border',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				//xtype : 'container',
				region : 'center',
				layout : 'border',
				items : [{
					xtype : 'form',
					region : 'center',
					autoScroll: true,
					id: 'configPanel',
					title : '参数配置',
					layout: 'column',
					bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
					defaults: {
						columnWidth: .5,
						margin: '4 8 4 8'
					},
					buttonAlign: 'center',
					buttons: [{
						text: '保存',
						id: 'btn-save',
						height: 30
					},{
						text: '关闭',
						id: 'btn-close',
						height: 30
					}],
					items: [{
						xtype:'displayfield',
						text:'没有参数配置'
					}]
				},{
					region:'south',
					title: '基础刷新',
					xtype:'sysrefreshgrid',
					id: 'tabpanel',
					region : 'south',
					height: window.innerHeight*0.62,
					bodyStyle : 'background:#f9f9f9',
					border: false,
					collapsible: true,
					collapseDirection: 'bottom',
					collapsed: window.whoami ? false : true
				}]},{region: "east",
					id: "east-region",
					title: "模块描述",
					stateful: true,
					stateId: "mainnav.east",
					split: true,collapsible: true,
					layout: {type: "vbox",align: "stretch"},
					width:'30%',
					tools: [{type: "gear",regionTool: true}],
					items: [{ bodyStyle: 'padding-bottom:15px;background:#eee;',
						id: 'infos-panel',
				        autoScroll: true,
				        height:300,
				        html: '<p class="details-info">When you select a layout from the tree, additional details will display here.</p>'}, 
						{xtype: "splitter",collapsible: true,collapseTarget: "prev"}, 
						{
							xtype: "sysdatagrid",title:'数据明细',flex: 1
							
						}]
				}]
		});
		me.callParent(arguments);
	}
});