Ext.define('erp.view.ma.encryptConfig', {
	extend: 'Ext.Viewport',
	alias: 'widget.encryptConfig',
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	id: 'enctyptConfig',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this;
		Ext.apply(me, {
			items: [{
				title: '密码加密设置',
				xtype: 'form',
				width: 605,
				height: 260,
				frame:true,	
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				buttonAlign:'center',
				fieldDefaults : {
					margin : '2 2 2 2',
					fieldStyle : "background:#FFFAFA;color:#515151;",
					labelAlign : "right",
					blankText : $I18N.common.form.blankText
				},
				items: [{
					xtype: 'fieldcontainer',
					fieldLabel : '是否启用',
					defaultType: 'radiofield',
		            labelCls: 'mylabel',
		            labelWidth: '200px',
		            id: 'radioGroup',
		            items: [{
		            	boxLabel: '是',
		            	name: 'enable',
		            	inputValue: '1',
		            	cls: 'myradio',
		            	id: 'encrypt'
		            },{
		            	xtype: 'label',
		            	text: '--将所有账套下用户的密码加密。',
		            	cls: 'labeltext'
		            },{
		            	boxLabel: '否',
		            	name: 'enable',
		            	inputValue: '0',
		            	cls: 'myradio',
		            	id: 'decrypt'
		            },{
		            	xtype: 'label',
		            	text: '--将所有账套下用户的密码解密。',
		            	cls: 'labeltext'
		            }]
				}],
				buttons: [{
					text: '确认',
					cls: 'x-btn-gray',
					width: 60,
					id: 'confirm'
				},{
					text: '关闭',
					cls: 'x-btn-gray',
					width: 60,
					handler: function(btn){
						var p = parent.Ext.getCmp('content-panel');
						p.getActiveTab().close();
					}
				}]
			}]
		});
		me.callParent(arguments); 
	}
	
});