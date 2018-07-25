Ext.define('erp.view.scm.sale.Evaluation', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 44%',
				saveUrl : 'scm/sale/saveEvaluation.action',
				deleteUrl : 'scm/sale/deleteEvaluation.action',
				updateUrl : 'scm/sale/updateEvaluation.action',
				auditUrl : 'scm/sale/auditEvaluation.action',
				resAuditUrl : 'scm/sale/resAuditEvaluation.action',
				submitUrl : 'scm/sale/submitEvaluation.action',
				resSubmitUrl : 'scm/sale/resSubmitEvaluation.action',
				bannedUrl : 'scm/sale/bannedEvaluation.action',
				resBannedUrl : 'scm/sale/resBannedEvaluation.action',
				printUrl : 'scm/sale/printEvaluation.action',
				getIdUrl : 'common/getId.action?seq=EVALUATION_SEQ',
				keyField : 'ev_id',
				codeField : 'ev_code',
				statusField : 'ev_checkstatuscode'
			},
			{
				xtype:'tabpanel',
			 	anchor : '100% 56%',
				items:[{
					title:'报价材料明细',
					xtype : 'erpGridPanel2',
					necessaryField :'evd_prodcode',
					keyField : 'evd_id',
					mainField : 'evd_evid',
					cls : 'custom-grid',
					viewConfig : {
						getRowClass : function(record) {
							return record.get('evd_id') > 0 && record.get('evd_doubleprice') == 0 ? 'rep' : '';
						}
					}
				},{
					title:'产品开发及费用',
					xtype : 'EvaluationProduct',
					caller:'EvaluationProduct',
					condition:condition,
					id: 'EvaluationProduct',
					detno : 'evp_detno',
					keyField : 'evp_id',
					mainField : 'evp_evid'
				},{
					title:'过程费用',
					xtype : 'EvaluationProcess',
					caller:'EvaluationProcess',
					condition:condition,
					id: 'EvaluationProcess',
					detno : 'evp_detno',
					keyField : 'evp_id',
					mainField : 'evp_evid'
				},{
					title:'参考材料明细',
					xtype : 'erpGridPanel5',
					caller:'EvaluationRefer',
					id:'bom',
					necessaryField : 'evd_prodcode',
					keyField : 'evd_id',
					mainField : 'evd_evid',
					cls : 'custom-grid',
					condition: getUrlParam('gridCondition')!=null?getUrlParam('gridCondition').replace(/IS/g, "="):''
				}]
			}]
		});
		me.callParent(arguments);
	}
});