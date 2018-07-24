Ext.define('erp.view.fa.wg.Formula',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/wg/saveFormula.action',
					deleteUrl: 'fa/wg/deleteFormula.action',
					updateUrl: 'fa/wg/updateFormula.action',
					auditUrl: 'fa/wg/auditFormula.action',
					resAuditUrl: 'fa/wg/resAuditFormula.action',
					submitUrl: 'fa/wg/submitFormula.action',
					resSubmitUrl: 'fa/wg/resSubmitFormula.action',
					getIdUrl: 'common/getId.action?seq=Formula_SEQ',
					keyField: 'fo_id',
					statusField: 'fo_status',
					statuscodeField: 'fo_statuscode',
					codeField: 'fo_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});