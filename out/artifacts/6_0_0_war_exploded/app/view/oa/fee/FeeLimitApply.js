Ext.define('erp.view.oa.fee.FeeLimitApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'oa/fee/saveFeeLimitApply.action',
					deleteUrl: 'oa/fee/deleteFeeLimitApply.action',
					updateUrl: 'oa/fee/updateFeeLimitApply.action',
					getIdUrl: 'common/getId.action?seq=FeeLimitApply_SEQ',
					auditUrl: 'oa/fee/auditFeeLimitApply.action',
					resAuditUrl: 'oa/fee/resAuditFeeLimitApply.action',
					submitUrl: 'oa/fee/submitFeeLimitApply.action',
					resSubmitUrl: 'oa/fee/resSubmitFeeLimitApply.action',
					keyField: 'fa_id',
					codeField: 'fa_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					keyField: 'fad_id',
					detno: 'fad_detno',
					mainField: 'fad_faid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});