Ext.define('erp.view.crm.marketmgr.marketchange.ChangeBudget',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				//id:'purchaseViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'crm/marketmgr/saveChangeBudget.action',
					deleteUrl: 'crm/marketmgr/deleteChangeBudget.action',
					updateUrl: 'crm/marketmgr/updateChangeBudget.action',
					auditUrl: 'crm/marketmgr/auditChangeBudget.action',
					resAuditUrl: '/crm/marketmgr/resAuditChangeBudget.action',
					submitUrl: 'crm/marketmgr/submitChangeBudget.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitChangeBudget.action',
					getIdUrl: 'common/getId.action?seq=ChangeBudget_SEQ',
					keyField: 'cb_id',
					codeField: 'cb_code',
					statusField: 'cb_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'cbd_detno',
					necessaryField: 'cbd_projcode',
					keyField: 'cbd_id',
					mainField: 'cbd_cbid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});