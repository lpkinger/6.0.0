Ext.define('erp.view.ma.update.DataUpdate', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() { 
		var me = this; 
		Ext.apply(me, { 
			items: [{	anchor: "100% 7%",
						xtype: 'toolbar',
						bodyStyle: 'background: #f1f2f5;',
						layout: {
							type: 'hbox'
						},
						defaults: {						
							margin: '2 0 2 2',
							cls : 'x-btn-gray',
							width : 100
						},
						items: [{
							xtype: 'button',
							text: '重新选择',
							iconCls: 'upexcel',
							name: 'import'
						},{
							xtype: 'cycle',
							showText: true,
							iconCls: 'check',
							id: 'checkupdate',							
							menu:{
								  items: [{
								            text: '校验数据',											
								            checked: true},
								          {
								            text: '更新'}]
								    }
						},
						{
							xtype: 'button',
							text: '导出错误数据',
							width:120,
							iconCls: 'x-button-icon-download',
							name: 'downloadError',
							id:'downloaderror',
							hidden:true
						},
						
						'->',{
							xtype: 'button',
							text: '查看历史',
							iconCls: 'history',
							name: 'history',
							id:'history'
						},{
							xtype: 'button',
							text: '关闭',
							iconCls: 'icon-close',
							name: 'close'
						}]
					},{				
						anchor: "100% 93%",
						xtype: 'panel',
						autoScroll: false,
						border :false, 
						layout: 'border',
						items: [{
							xtype: 'panel',
							region: 'center',
							height: '100%',
							width: '100%',
							id: 'updategrid',
							bodyStyle: 'background: #f1f2f5;',
							layout: 'anchor',
							border :false, 
							autoScroll: false
						}]
					}]
		});
		me.callParent(arguments); 
	}
});