Ext.define('erp.view.pm.bom.BOMDetailGroup',{ 
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
					saveUrl: 'pm/bom/saveBOMDetailGroup.action',
					deleteUrl: 'pm/bom/deleteBOMDetailGroup.action',
					updateUrl: 'pm/bom/updateBOMDetailGroup.action',
					getIdUrl: 'common/getId.action?seq=BOMDETAILGROUP_SEQ',
					keyField: 'bdg_id',
					//codeField: 'bo_code',
					//statusField: 'bo_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});