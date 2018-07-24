Ext.define('erp.view.hr.emplmana.Accidinsur',{ 
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
					saveUrl: 'hr/emplmana/saveAccidinsur.action',
					deleteUrl: 'hr/emplmana/deleteAccidinsur.action',
					updateUrl: 'hr/emplmana/updateAccidinsur.action',		
					getIdUrl: 'common/getId.action?seq=Accidinsur_SEQ',
					keyField: 'as_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});