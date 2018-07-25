Ext.define('erp.view.pm.bom.NeedFeature',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				updateUrl: 'pm/bom/setNeedSpec.action',
				deleteUrl: 'pm/bom/deleteNeedSpec.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTREVIEWDETAIL_SEQ',
				keyField: 'pvd_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'nf_detno',
				keyField: 'nf_id',
				mainField: 'nf_pvdid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});