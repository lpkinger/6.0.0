Ext.define('erp.view.hr.check.Evectionset',{ 
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
					saveUrl: 'hr/check/saveEvectionset.action',
					deleteUrl: 'hr/check/deleteEvectionset.action',
					updateUrl: 'hr/check/updateEvectionset.action',		
					getIdUrl: 'common/getId.action?seq=Evectionset_SEQ',
					keyField: 'es_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});