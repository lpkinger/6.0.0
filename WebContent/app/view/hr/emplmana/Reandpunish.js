Ext.define('erp.view.hr.emplmana.Reandpunish',{ 
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
					saveUrl: 'hr/emplmana/saveReandpunish.action',
					deleteUrl: 'hr/emplmana/deleteReandpunish.action',
					updateUrl: 'hr/emplmana/updateReandpunish.action',		
					getIdUrl: 'common/getId.action?seq=Reandpunish_SEQ',
					keyField: 'rp_id',
					auditUrl: 'hr/emplmana/auditReandpunish.action',
					resAuditUrl: 'hr/emplmana/resAuditReandpunish.action',
					submitUrl: 'hr/emplmana/submitReandpunish.action',
					resSubmitUrl: 'hr/emplmana/resSubmitReandpunish.action'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});