Ext.define('erp.view.fs.cust.FSMFCustInfo',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 65%',
					saveUrl: 'fs/cust/saveFSMFCustInfo.action',
					updateUrl: 'fs/cust/updateFSMFCustInfo.action',
					deleteUrl: 'fs/cust/deleteFSMFCustInfo.action',
					submitUrl: 'fs/cust/submitFSMFCustInfo.action',
					resSubmitUrl: 'fs/cust/resSubmitFSMFCustInfo.action',
					auditUrl: 'fs/cust/auditFSMFCustInfo.action',
					resAuditUrl: 'fs/cust/resAuditFSMFCustInfo.action',
					getIdUrl: 'common/getId.action?seq=FSMFCustInfo_SEQ',
					keyField: 'mf_id',
					statusField: 'mf_status',
					statuscodeField: 'mf_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 35%', 
					keyField: 'mfd_id',
					mainField: 'mfd_mfid'
				}]
			}); 
		this.callParent(arguments); 
	}
});