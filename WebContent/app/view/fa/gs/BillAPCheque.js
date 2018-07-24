Ext.define('erp.view.fa.gs.BillAPCheque',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fa/gs/saveBillAPCheque.action',
				deleteUrl: 'fa/gs/deleteBillAPCheque.action',
				updateUrl: 'fa/gs/updateBillAPCheque.action',
				auditUrl: 'fa/gs/auditBillAPCheque.action',
				resAuditUrl: 'fa/gs/resAuditBillAPCheque.action',
				submitUrl: 'fa/gs/submitBillAPCheque.action',
				resSubmitUrl: 'fa/gs/resSubmitBillAPCheque.action',
				nullifyUrl: 'fa/gs/nullifyBillAPCheque.action',
				getIdUrl: 'common/getId.action?seq=BillAPCheque_SEQ',
				keyField: 'bar_id',
				statusField: 'bar_status',
				statuscodeField: 'bar_statuscode',
				codeField: 'bar_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});