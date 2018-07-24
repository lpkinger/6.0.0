Ext.define('erp.view.salary.SalaryPsw',{
	extend:"Ext.Viewport",
	layout: 'border', 
	hideBorders: true, 
	initComponent:function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				region: 'center',
				id:'changePwd',
				layout: 'anchor',
				frameHeader:'false',
				border:false,
				items: [/*{
					xtype: 'button',
					text: '登录',
					id: 'btn-login',
					hidden:true
				},*/{
					xtype:"button",
					id:"psw",
					hidden:true,
				}]
			}] 
		});
		me.callParent(arguments); 	
	}
});