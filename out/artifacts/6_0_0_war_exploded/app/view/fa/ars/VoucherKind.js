Ext.define('erp.view.fa.ars.VoucherKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: "window",
				autoShow: true,
				closable: false,
				maximizable : true,
		    	width: '65%',
		    	height: '65%',
		    	layout: 'anchor',
		    	items: [{
		    		xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/ars/saveVoucherKind.action',
					deleteUrl: 'fa/ars/deleteVoucherKind.action',
					updateUrl: 'fa/ars/updateVoucherKind.action',
					getIdUrl: 'common/getId.action?seq=VOUCHERKINDT_SEQ',
					keyField: 'vk_id'
		    	}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});