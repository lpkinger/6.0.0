Ext.define('erp.view.plm.test.CheckList',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	id:'checklist', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'plm/check/saveCheckList.action',
					deleteUrl: 'plm/check/deleteCheckList.action',
					updateUrl: 'plm/check/updateCheckList.action',
					auditUrl: 'plm/check/auditCheckList.action',
					resAuditUrl: 'plm/check/resAuditCheckList.action',
					submitUrl: 'plm/check/submitCheckList.action',
					resSubmitUrl: 'plm/check/resSubmitCheckList.action',
					getIdUrl: 'common/getId.action?seq=CHECKLIST_SEQ',
					keyField: 'cl_id',
					codeField:'cl_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					detno: 'cld_detno',
					necessaryField: 'cld_itemcode',
					keyField:'cld_id',
					mainField: 'cld_clid'
				 }
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});