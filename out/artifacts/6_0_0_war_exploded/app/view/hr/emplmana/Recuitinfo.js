Ext.define('erp.view.hr.emplmana.Recuitinfo',{ 
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
					saveUrl: 'hr/emplmana/saveRecuitinfo.action',
					deleteUrl: 'hr/emplmana/deleteRecuitinfo.action',
					updateUrl: 'hr/emplmana/updateRecuitinfo.action',
					submitUrl: 'hr/emplmana/submitRecuitinfo.action',
					resSubmitUrl: 'hr/emplmana/resSubmitRecuitinfo.action',
					auditUrl: 'hr/emplmana/auditRecuitinfo.action',
					resAuditUrl: 'hr/emplmana/resAuditRecuitinfo.action',
					getIdUrl: 'common/getId.action?seq=Recuitinfo_SEQ',
					keyField: 're_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});