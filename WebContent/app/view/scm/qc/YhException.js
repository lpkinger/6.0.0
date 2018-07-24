Ext.define('erp.view.scm.qc.YhException',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				deleteUrl: 'scm/qc/deleteYhException.action',
				updateUrl: 'scm/qc/updateYhException.action',
				auditUrl: 'scm/qc/auditYhException.action',
				resAuditUrl: 'scm/qc/resAuditYhException.action',
				saveUrl: 'scm/qc/saveYhException.action',
				submitUrl: 'scm/qc/submitYhException.action',
				resSubmitUrl: 'scm/qc/resSubmitYhException.action',
				getIdUrl: 'common/getId.action?seq=YhException_SEQ',
				codeField: 'ye_code',
				keyField: 'ye_id',
				statusField: 'ye_status',
				statuscodeField: 'ye_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});