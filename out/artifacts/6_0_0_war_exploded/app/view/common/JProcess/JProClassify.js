Ext.define('erp.view.common.JProcess.JProClassify',{ 
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
					saveUrl: 'common/saveJProClassify.action',
					deleteUrl: 'common/deleteJProClassify.action',
					updateUrl: 'common/updateJProClassify.action',
					//auditUrl: 'common/auditJProClassify.action',
					//resAuditUrl: 'common/resAuditJProClassify.action',
					//submitUrl: 'common/submitJProClassify.action',
					//resSubmitUrl: 'common/resSubmitJProClassify.action',
					getIdUrl: 'common/getId.action?seq=JProClassify_SEQ',
					keyField: 'jc_id',
					codeField: 'jc_code',
					statusField: 'jc_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});