Ext.define('erp.view.common.init.UU',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'form',
				region: 'east',
				width: '70%',
				bodyStyle: 'background-color: #fefefe',
				id: 'uu',
				layout: {
					type: 'vbox',
					align: 'center',
					pack: 'center'
				},
				items: [{
					xtype: 'numberfield',
					hideTrigger: true,
					allowBlank: false,
					fieldLabel: '企业UU号',
					name: 'en_uu',
					id: 'en_uu'
				},{
					xtype: 'numberfield',
					hideTrigger: true,
					allowBlank: false,
					fieldLabel: '个人UU号',
					name: 'em_uu',
					id: 'em_uu'
				},{
					xtype: 'textfield',
					fieldLabel: '密码',
					allowBlank: false,
					inputType: 'password',
					name: 'em_password',
					id: 'em_password'
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '上一步',
					cls: 'x-btn-blue',
					id: 'prev'
				},{
					text: '确定',
					cls: 'x-btn-blue',
					id: 'confirm'
				},{
					text: '下一步',
					cls: 'x-btn-blue',
					id: 'next'
				}]
			},{
				region: 'center',
				xtype: 'panel',
				contentEl: 'uas'
			}] 
		});
		me.callParent(arguments); 
	}
});