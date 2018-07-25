Ext.define('erp.view.hr.check.Overtimeset',{ 
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
					saveUrl: 'hr/check/saveOvertimeset.action',
					deleteUrl: 'hr/check/deleteOvertimeset.action',
					updateUrl: 'hr/check/updateOvertimeset.action',		
					getIdUrl: 'common/getId.action?seq=Overtimeset_SEQ',
					keyField: 'ot_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});