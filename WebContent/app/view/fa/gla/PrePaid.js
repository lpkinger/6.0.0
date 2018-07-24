Ext.define('erp.view.fa.gla.PrePaid',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				deleteUrl: 'fa/gla/deletePrePaid.action',
				updateUrl: 'fa/gla/updatePrePaid.action',
				auditUrl: 'fa/gla/auditPrePaid.action',
				saveUrl: 'fa/gla/savePrePaid.action',
				resAuditUrl: 'fa/gla/resAuditPrePaid.action',
				postUrl: 'fa/gla/postPrePaid.action',
				resPostUrl: 'fa/gla/resPostPrePaid.action',
				getIdUrl: 'common/getId.action?seq=PrePaid_SEQ',
				keyField: 'pp_id',
				codeField: 'pp_code',
				statusField: 'pp_status',
				statuscodeField: 'pp_statuscode',
				voucherConfig: {
					voucherField: 'pp_vouchercode',
					vs_code: 'PrePaid',
					yearmonth: 'pp_date',
					datas: 'pp_code',
					status: 'pp_statuscode',
					mode: 'single',
					kind: '摊销单',
					vomode: 'GL'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pd_detno',
				necessaryField: 'pd_amortcode',
				keyField: 'pd_id',
				mainField: 'pd_ppid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});