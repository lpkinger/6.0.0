Ext.define('erp.view.hr.wage.calc.Wagecalculate',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	initComponent : function(){ 
		var me = this;
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 300,
				bodyStyle: 'background: #f1f1f1;',
				margin: '-20 0 0 0',
				xtype: 'form',
				title: '工资报表计算作业',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{
					xtype: 'container',
					contentEl: 'desc'
				},{		
					margin : '20 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '日期',
			    	allowBlank: false,
			    	labelWidth: 40,
			    	id: 'date',
			    	name: 'date'
				}],
				buttonAlign: 'center',
				buttons: [{
					xtype: 'erpConfirmButton'
				},{
					xtype:'erpDeleteButton'
				},{
					xtype:'erpCloseButton'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});