Ext.define('erp.view.fa.ars.Payments',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'paymentsViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'scm/purchase/savePayments.action',
					deleteUrl: 'scm/purchase/deletePayments.action',
					updateUrl: 'scm/purchase/updatePayments.action',
					getIdUrl: 'common/getId.action?seq=Payments_SEQ',
					keyField: 'pa_id',
					codeField: 'pa_code'
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});