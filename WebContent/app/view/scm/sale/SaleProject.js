Ext.define('erp.view.scm.sale.SaleProject',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/sale/saveSaleProject.action?caller='+caller,
				deleteUrl: 'scm/sale/deleteSaleProject.action?caller='+caller,
				updateUrl: 'scm/sale/updateSaleProject.action?caller='+caller,
				auditUrl: 'scm/sale/auditSaleProject.action?caller='+caller,
				resAuditUrl: 'scm/sale/resAuditSaleProject.action?caller='+caller,
				submitUrl: 'scm/sale/submitSaleProject.action?caller='+caller,
				resSubmitUrl: 'scm/sale/resSubmitSaleProject.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=SaleProject_SEQ',
				keyField: 'sp_id',
				codeField: 'sp_code',
				statuscodeField: 'sp_statuscode',
				statusField: 'sp_status'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});