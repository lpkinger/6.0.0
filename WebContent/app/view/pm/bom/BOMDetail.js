Ext.define('erp.view.pm.bom.BOMDetail',{ 
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
					anchor: '100% 60%',
					saveUrl: 'pm/bom/saveBOMDetail.action',
					deleteUrl: 'pm/bom/deleteBOMDetail.action',
					updateUrl: 'pm/bom/updateBOMDetail.action',			
					getIdUrl: 'common/getId.action?seq=BOMDETAIL_SEQ',
					keyField: 'bd_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});