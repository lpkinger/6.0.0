Ext.define('erp.view.scm.reserve.UnPeriodsdetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hiextend: 'Ext.Viewport', 
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
				title: '库存反结账作业',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '40 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '期间',
			    	allowBlank: false,
			    	readOnly: true,
			    	labelWidth: 60,
			    	id: 'date',
			    	name: 'date'
				}],
				buttonAlign: 'center',
				buttons: [{
					xtype: 'erpUnCheckPostButton',
					height: 26
				},{
					xtype:'erpCloseButton',
					height: 26
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});