Ext.define('erp.view.hr.emplmana.Indinjury',{ 
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
					saveUrl: 'hr/emplmana/saveIndinjury.action',
					deleteUrl: 'hr/emplmana/deleteIndinjury.action',
					updateUrl: 'hr/emplmana/updateIndinjury.action',		
					getIdUrl: 'common/getId.action?seq=Indinjury_SEQ',
					keyField: 'ij_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});