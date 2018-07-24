Ext.define('erp.view.common.VisitERP.Login', {
	extend : 'Ext.Viewport',
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	}, 
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				title:'访问xxxERP系统',
				xtype: 'form',
				height: 260,
				width: 440,
				bodyStyle:'background:#f2f2f2',
				layout: {
					type: 'vbox',
					align: 'center',
					pack: 'center'
				}, 
				items:[{
					xtype: 'combo',
					fieldLabel: '账套',
					id: 'master',
					store:[]
				},{
					xtype: 'textfield',
					fieldLabel: '帐号',
					id: 'username'
				},{
					xtype: 'textfield',
					fieldLabel: '密码',
					id: 'password'
				}],
				bbar:['->',{
					cls:'x-btn-gray',
					xtype:'button',
					text:'登录'
				},{
					cls:'x-btn-gray',
					margin:'0 0 0 10',
					xtype:'button',
					text:'关闭'
				},'->']
			}]
		});
		me.callParent(arguments);
	}
});