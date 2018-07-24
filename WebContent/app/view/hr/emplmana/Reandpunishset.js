Ext.define('erp.view.hr.emplmana.Reandpunishset',{ 
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
					saveUrl: 'hr/emplmana/saveReandpunishset.action',
					deleteUrl: 'hr/emplmana/deleteReandpunishset.action',
					updateUrl: 'hr/emplmana/updateReandpunishset.action',		
					getIdUrl: 'common/getId.action?seq=Reandpunishset_SEQ',
					keyField: 'rs_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});