Ext.define('erp.view.hr.emplmana.Contractset',{ 
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
					saveUrl: 'hr/emplmana/saveContractset.action',
					deleteUrl: 'hr/emplmana/deleteContractset.action',
					updateUrl: 'hr/emplmana/updateContractset.action',		
					getIdUrl: 'common/getId.action?seq=Contractset_SEQ',
					keyField: 'cs_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});