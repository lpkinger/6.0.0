Ext.define('erp.view.pm.bom.BOMDetailGroupReplace',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BOMTestViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 25%',
					saveUrl: 'pm/bom/saveBOMDetailGroupReplace.action',
					deleteUrl: 'pm/bom/deleteBOMDetailGroupReplace.action',
					updateUrl: 'pm/bom/updateBOMDetailGroupReplace.action',
					getIdUrl: 'common/getId.action?seq=BOMDETAILGROUP_SEQ',
					keyField: 'bdg_id',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					detno: 'bgr_detno',
					keyField: 'bgr_id',
					mainField: 'bgr_groupid',
					necessaryField: 'bgr_repgroupid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});