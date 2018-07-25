Ext.define('erp.view.ma.logic.Config', {
	extend : 'Ext.Viewport',
	layout : 'border',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpTreePanel',
				dockedItems : null,
				region : 'east',
				width : '28%',
				height : '100%',
				useArrows: false,
				bodyStyle: null
			}, {
				xtype : 'container',
				region : 'center',
				layout : 'border',
				items : [ {
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
					},{
						text: '查看日志',
						height: 30,
						id:'btn-logs',
					}],
					items: [{
						html: '没有参数配置',
						cls: 'x-form-empty'
					}],
				}, {
					xtype: 'tabpanel',
					title: '逻辑配置',
					id: 'tabpanel',
					region : 'south',
					height: window.innerHeight*0.62,
					bodyStyle : 'background:#f9f9f9',
					border: false,
					collapsible: true,
					collapseDirection: 'bottom',
					collapsed: window.whoami ? false : true
				} ]
			} ],
		});
		me.callParent(arguments);
	}
});