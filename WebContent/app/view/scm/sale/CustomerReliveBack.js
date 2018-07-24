Ext.define('erp.view.scm.sale.CustomerReliveBack', {
	extend : 'Ext.Viewport',
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				width: 450,
				bodyStyle: 'background: #f1f1f1;',
				xtype: 'form',
				title: '解挂申请回款计算',
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