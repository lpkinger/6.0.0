Ext.define('erp.view.fa.gla.AmortProgram',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				deleteUrl: 'fa/gla/deleteAmortProgram.action',
				updateUrl: 'fa/gla/updateAmortProgram.action',
				auditUrl: 'fa/gla/auditAmortProgram.action',
				saveUrl: 'fa/gla/saveAmortProgram.action',
				resAuditUrl: 'fa/gla/resAuditAmortProgram.action',
				getIdUrl: 'common/getId.action?seq=AmortProgram_SEQ',
				keyField: 'ap_id',
				codeField: 'ap_code',
				statusField: 'ap_status',
				statuscodeField: 'ap_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'ad_detno',
				keyField: 'ad_id',
				mainField: 'ad_apid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});