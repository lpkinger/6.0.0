Ext.define('erp.view.common.init.Master',{ 
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
				height: 200,
				width: 400,
				layout: {
					type: 'vbox',
					align: 'center',
					pack: 'center'
				},
				title: '登录到需要初始化的账套',
				bodyStyle: 'background: #f1f1f1;',
				defaults: {
					margin: '5 10 5 10',
					labelWidth: 40
				},
				items: [{
					xtype: 'combobox',
					allowBlank: false,
					editable: false,
					fieldLabel: '账套',
					name: 'ma_name',
					id: 'ma_name',
					store: Ext.create('Ext.data.Store', {
						fields: ['display', 'value']
					}),
					displayField: 'display',
					valueField: 'value',
					queryMode: 'local'
				},{
					xtype: 'textfield',
					fieldLabel: '账号',
					name: 'em_code',
					allowBlank: false,
					id: 'em_code'
				},{
					xtype: 'textfield',
					fieldLabel: '密码',
					allowBlank: false,
					inputType: 'password',
					name: 'em_password',
					allowBlank: false,
					id: 'em_password'
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '上一步',
					cls: 'custom-button',
					id: 'prev'
				},{
					text: '新建账套',
					cls: 'custom-button',
					id: 'newmaster'
				},{
					text: '确定',
					cls: 'custom-button',
					formBind: true,
					id: 'confirm'
				},{
					text: '下一步',
					cls: 'custom-button',
					id: 'next',
					disabled: true
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});