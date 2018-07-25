Ext.define('erp.view.pm.mps.SYSJob',{ 
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
					saveUrl: 'pm/mps/saveSYSJob.action',
					deleteUrl: 'pm/mps/deleteSYSJob.action',
					updateUrl: 'pm/mps/updateSYSJob.action',
					submitUrl:'pm/mps/submitSYSJob.action',
					resSubmitUrl:'pm/mps/resSubmitSYSJob.action',
					auditUrl:'pm/mps/auditSYSJob.action',
					resAuditUrl:'pm/mps/resAuditSYSJob.action',
					getIdUrl: 'common/getId.action?seq=SYSJOB_SEQ',
					keyField: 'sj_id',
					codeField:'sj_code'
				}] 
			}]
		}); 
		me.callParent(arguments); 
	} 
});