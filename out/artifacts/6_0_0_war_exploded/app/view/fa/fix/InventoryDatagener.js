Ext.define('erp.view.fa.fix.InventoryDatagener',{ 
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
				title: '盘点资料生成作业',
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				buttonAlign: 'center',
	    		buttons: [{
	    			xtype: 'erpConfirmButton',
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