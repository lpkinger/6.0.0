Ext.define('erp.view.pm.bom.BOMSon',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BOMViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/bom/saveBOMSon.action',
					deleteUrl: 'pm/bom/deleteBOMSon.action',
					updateUrl: 'pm/bom/updateBOMSon.action',			
					getIdUrl: 'common/getId.action?seq=BOMSON_SEQ',
					keyField: 'bd_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});