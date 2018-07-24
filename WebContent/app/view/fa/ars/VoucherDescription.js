Ext.define('erp.view.fa.ars.VoucherDescription',{ 
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
					saveUrl: 'fa/ars/saveVoucherDescription.action',
					deleteUrl: 'fa/ars/deleteVoucherDescription.action',
					updateUrl: 'fa/ars/updateVoucherDescription.action',
					getIdUrl: 'common/getId.action?seq=VOUCHERDESCRIPTION_SEQ',
					keyField: 'vd_id'	
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});