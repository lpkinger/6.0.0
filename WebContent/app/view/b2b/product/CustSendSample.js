Ext.define('erp.view.b2b.product.CustSendSample',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 90%',				
				deleteUrl: 'b2b/product/deleteCustSendSample.action',
				updateUrl: 'b2b/product/updateCustSendSample.action',
				auditUrl: 'b2b/product/auditCustSendSample.action',
				resAuditUrl: 'b2b/product/resCustSendSample.action',				
				submitUrl: 'b2b/product/submitCustSendSample.action',
				resSubmitUrl: 'b2b/product/resSubmitCustSendSample.action',
				keyField: 'ss_id',
				codeField: 'ss_code',
				statusField: 'ss_status',
				statuscodeField: 'ss_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});