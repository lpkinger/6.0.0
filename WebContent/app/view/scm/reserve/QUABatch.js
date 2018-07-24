Ext.define('erp.view.scm.reserve.QUABatch',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/reserve/saveQUABatch.action',
				deleteUrl: 'scm/reserve/deleteQUABatch.action',
				updateUrl: 'scm/reserve/updateQUABatch.action',
				submitUrl: 'scm/reserve/submitQUABatch.action',
				resSubmitUrl: 'scm/reserve/resSubmitQUABatch.action',
				auditUrl: 'scm/reserve/auditQUABatch.action',
				resAuditUrl: 'scm/reserve/resAuditQUABatch.action',
				getIdUrl: 'common/getId.action?seq=QUABATCH_SEQ',
				keyField: 'qba_id',
				codeField: 'qba_code',
				statuscodeField: 'qba_statuscode',
				statusField: 'qba_status'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'pbd_detno',
				necessaryField: 'pbd_batchcode',
				keyField: 'pbd_id',
				mainField: 'pbd_qbaid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});