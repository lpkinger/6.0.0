Ext.define('erp.view.plm.task.ProjectWeekly',{ 
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
					anchor: '100% 20%',
					saveUrl: 'plm/task/savePrjWkReport.action',
					deleteUrl: 'plm/task/deletePrjWkReport.action',
					updateUrl: 'plm/task/updatePrjWkReport.action',
					submitUrl: 'plm/task/submitPrjWkReport.action',
					resSubmitUrl:'plm/task/resSubmitPrjWkReport.action',
					auditUrl: 'plm/task/auditPrjWkReport.action',
					resAuditUrl: 'plm/task/resAuditPrjWkReport.action',
					getIdUrl: 'common/getId.action?seq=PRJWKREPORT_SEQ',
					keyField: 'wr_id',
					codeField:'wr_code',
					statusField: 'wr_status',
					statuscodeField: 'wr_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 80%', 
					detno: 'wrd_detno',
					keyField: 'wrd_id',
					mainField: 'wrd_wrid',
					allowExtraButtons: false
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});