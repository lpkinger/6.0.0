Ext.define('erp.view.scm.sale.PreSale',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: v_width==100?'100% 100%':'120% 100%',
				saveUrl: 'scm/sale/savePreSale.action?caller='+caller,
				deleteUrl: 'scm/sale/deletePreSale.action?caller='+caller,
				updateUrl: 'scm/sale/updatePreSale.action?caller='+caller,
				auditUrl: 'scm/sale/auditPreSale.action?caller='+caller,
				resAuditUrl: 'scm/sale/resAuditPreSale.action?caller='+caller,
				submitUrl: 'scm/sale/submitPreSale.action?caller='+caller,
				resSubmitUrl: 'scm/sale/resSubmitPreSale.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=PreSale_SEQ',
				keyField: 'ps_id',
				codeField: 'ps_code',
				statuscodeField: 'ps_statuscode',
				statusField: 'ps_status'
			}]
		}); 
		me.callParent(arguments); 
	} 
});