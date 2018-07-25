Ext.define('erp.view.hr.emplmana.Contract',{ 
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
					saveUrl: 'hr/emplmana/saveContract.action',
					deleteUrl: 'hr/emplmana/deleteContract.action',
					updateUrl: 'hr/emplmana/updateContract.action',	
					auditUrl: 'hr/emplmana/auditContract.action',
					resAuditUrl: 'hr/emplmana/resAuditContract.action',
					submitUrl: 'hr/emplmana/submitContract.action',
					resSubmitUrl: 'hr/emplmana/resSubmitContract.action',
					getIdUrl: 'common/getId.action?seq=Contract_SEQ',
					keyField: 'co_id',
					codeField: 'co_code',
					statusField: 'co_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});