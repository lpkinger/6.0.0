Ext.define('erp.view.co.cost.MonthUnOver',{ 
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
				xtype: 'form',
				title: '成本期间反结转',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '40 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '期间',
			    	allowBlank: false,
			    	labelWidth: 60,
			    	id: 'date',
			    	name: 'date'
				}],
				buttonAlign: 'center',
				buttons: [{
					xtype: 'erpResCarryoverButton'
				},{
					xtype:'erpCloseButton'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});