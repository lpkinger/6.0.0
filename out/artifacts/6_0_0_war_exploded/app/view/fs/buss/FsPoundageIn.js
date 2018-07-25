Ext.define('erp.view.fs.buss.FsPoundageIn',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/buss/saveFsPoundageIn.action',
					updateUrl: 'fs/buss/updateFsPoundageIn.action',
					deleteUrl: 'fs/buss/deleteFsPoundageIn.action',
					submitUrl: 'fs/buss/submitFsPoundageIn.action',
					resSubmitUrl: 'fs/buss/resSubmitFsPoundageIn.action',
					auditUrl: 'fs/buss/auditFsPoundageIn.action',
					resAuditUrl: 'fs/buss/resAuditFsPoundageIn.action',
					getIdUrl: 'common/getId.action?seq=FSPOUNDAGEIN_SEQ',
					keyField: 'pi_id',
					codeField: 'pi_code'
				}]
			}); 
		this.callParent(arguments); 
	}
});