Ext.define('erp.view.fs.credit.AssessScheme',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 20%',
					saveUrl: 'fs/credit/saveAssessScheme.action',
					deleteUrl: 'fs/credit/deleteAssessScheme.action',
					updateUrl: 'fs/credit/updateAssessScheme.action',
					auditUrl: 'fs/credit/auditAssessScheme.action',
					resAuditUrl: 'fs/credit/resAuditAssessScheme.action',
					submitUrl: 'fs/credit/submitAssessScheme.action',
					resSubmitUrl: 'fs/credit/resSubmitAssessScheme.action',
					getIdUrl: 'common/getId.action?seq=ASSESSSCHEME_SEQ',
					keyField: 'as_id',
					codeField: 'as_code',
					statusField: 'as_status',
					statuscodeField: 'as_statuscode'
				},{				
					xtype: 'erpGridPanel2',
					anchor : '100% 80%',
					detno: 'asd_detno',
					keyField: 'asd_id',
					mainField: 'asd_asid',
					necessaryField: '',
					allowExtraButtons:false					
				}]
		}); 
		
		this.callParent(arguments); 
	}
});