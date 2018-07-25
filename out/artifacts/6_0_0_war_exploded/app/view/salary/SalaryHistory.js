Ext.define('erp.view.salary.SalaryHistory',{
	extend:"Ext.Viewport",
	layout: 'border', 
	hideBorders: true, 
	initComponent:function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				region: 'center',
				layout: 'anchor',
				frameHeader:'false',
				border:false,
				items: [{
					anchor: "100% 100%",
					xtype: 'panel',
					autoScroll: true,
					layout: 'border',
					id:'btn-panel',
					border:false,
					frame:true,
					items: [{
						xtype:'panel',
						height:"5%",
						width:"100%",
						region: 'north',
						frame:true,
						border:false,
						bodyStyle: 'background: #ffffff;',
						items:[{
							xtype: 'button',
							text: '登录',
							id: 'btn-login',
							hidden:true
						},{
							xtype:'button',
							text:"工资月份",
							id:'btn-date',
							iconCls:"x-btn-date",
							//disabled:true
						},{
							xtype: 'button',
							text: '重新发送',
							id: 'btn-resend',
							iconCls:"x-btn-resend",
							//disabled:true
						},{
							xtype: 'button',
							text: '删除数据',
							id: 'btn-delete',
							iconCls:'x-btn-delete',
							//disabled:true
						},{
							xtype: 'button',
							text: '导出数据',
							id: 'btn-download',
							iconCls:'x-btn-loadall',
							//disabled:true
						}]
					},{
						xtype: 'salaryHisPanel',
						region: 'center',
						height: '96%',
						width: '100%',
						id: 'template',
					}]
				}]
			}] 
		});
		me.callParent(arguments); 	
	}
});