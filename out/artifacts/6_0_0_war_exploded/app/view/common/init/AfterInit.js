Ext.define('erp.view.common.init.AfterInit', { 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	},
	style: 'background: #f1f2f5;',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				xtype: 'form',
				title: '初始化完成',
				height: 300,
				width: 300,
				layout: 'vbox',
				bodyStyle: 'background: #f1f1f1;',
				defaults: {
					margin: '10 0 0 30',
					checked: true
				},
				items: [{
					xtype: 'checkbox',
					action: 'ma/update_seq.action',
					boxLabel: '更新序列值'
				},{
					xtype: 'checkbox',
					action: 'ma/update_maxnum.action',
					boxLabel: '更新编号值'
				},{
					xtype: 'checkbox',
					action: 'system/init/clear.action',
					boxLabel: '清除导入历史'
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '上一步',
					cls: 'custom-button',
					name: 'prev'
				},{
					text: '确认',
					cls: 'custom-button',
					name: 'confirm'
				},{
					text: '退出',
					cls: 'custom-button',
					handler: function() {
						parent.window.opener = null;
						parent.window.open('', '_self');
						parent.window.close();
					}
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});