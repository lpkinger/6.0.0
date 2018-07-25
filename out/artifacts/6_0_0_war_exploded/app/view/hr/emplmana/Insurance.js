Ext.define('erp.view.hr.emplmana.Insurance',{ 
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
					saveUrl: 'hr/emplmana/saveInsurance.action',
					deleteUrl: 'hr/emplmana/deleteInsurance.action',
					updateUrl: 'hr/emplmana/updateInsurance.action',		
					getIdUrl: 'common/getId.action?seq=Insurance_SEQ',
					keyField: 'in_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});