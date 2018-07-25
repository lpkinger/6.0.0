Ext.define('erp.view.fa.ars.PreRec',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'fa/PreRecController/savePreRec.action',
				deleteUrl: 'fa/PreRecController/deletePreRec.action',
				updateUrl: 'fa/PreRecController/updatePreRec.action',
				auditUrl: 'fa/PreRecController/auditPreRec.action',
				resAuditUrl: 'fa/PreRecController/resAuditPreRec.action',
				submitUrl: 'fa/PreRecController/submitPreRec.action',
				resSubmitUrl: 'fa/PreRecController/resSubmitPreRec.action',
				postUrl: 'fa/PreRecController/postPreRec.action',
				resPostUrl: 'fa/PreRecController/resPostPreRec.action',
				printUrl:'common/printCommon.action',
				getIdUrl: 'common/getId.action?seq=PreRec_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code',
				auditStatusCode:'pr_auditstatuscode',
				statusCode:'pr_statuscode',
				printStatusCode:'pr_printstatuscode',
				assCaller:'PreRecAss',
				voucherConfig: {
					voucherField: 'pr_vouchercode',
					vs_code: 'PreRec',
					yearmonth: 'pr_date',
					datas: 'pr_code',
					status: 'pr_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#pr_kind');
						return f ? f.getValue() : null;
					},
					vomode: 'AR'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'prd_detno',  
				keyField: 'prd_id',
				mainField: 'prd_prid',
				detailAssCaller:'PreRecDetailAss'
			}]
		}); 
		me.callParent(arguments); 
	} 
});