Ext.define('erp.view.fa.fp.WorkReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					anchor: '100% 30%',
					xtype: 'erpFormPanel',
					saveUrl: 'fa/fp/saveWorkReport.action',
					deleteUrl: 'fa/fp/deleteWorkReport.action',
					updateUrl: 'fa/fp/updateWorkReport.action',
					auditUrl: 'fa/fp/auditWorkReport.action',
					resAuditUrl: 'fa/fp/resAuditWorkReport.action',
					submitUrl: 'fa/fp/submitWorkReport.action',
					resSubmitUrl: 'fa/fp/resSubmitWorkReport.action',
					getIdUrl: 'common/getId.action?seq=WorkReport_SEQ',
					keyField: 'wr_id',	
					codeField: 'wr_code',
					statusField: 'wr_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					keyField: 'wrd_id',
					mainField: 'wrd_wrid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});