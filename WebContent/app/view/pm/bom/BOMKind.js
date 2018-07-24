Ext.define('erp.view.pm.bom.BOMKind',{ 
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
					saveUrl: 'pm/bom/saveBOMKind.action',
					deleteUrl: 'pm/bom/deleteBOMKind.action',
					updateUrl: 'pm/bom/updateBOMKind.action',			
					getIdUrl: 'common/getId.action?seq=BOMKIND_SEQ',
					keyField: 'bk_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});