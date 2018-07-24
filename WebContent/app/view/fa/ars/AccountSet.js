Ext.define('erp.view.fa.ars.AccountSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'categoryViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 60%',
					saveUrl: 'fa/ars/saveAccountSet.action',
					deleteUrl: 'fa/ars/deleteAccountSet.action',
					updateUrl: 'fa/ars/updateAccountSet.action',
					auditUrl: 'fa/ars/auditAccountSet.action',
					resAuditUrl: 'fa/ars/resAuditAccountSet.action',
					submitUrl: 'fa/ars/submitAccountSet.action',
					resSubmitUrl: 'fa/ars/resSubmitAccountSet.action',
					getIdUrl: 'common/getId.action?seq=ACCOUNTSET_SEQ',
					keyField: 'as_id',	
					codefield:'as_setcode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});