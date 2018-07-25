Ext.define('erp.view.fa.ars.AssKindDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'fa/ars/saveAssKindDetail.action',
				deleteUrl: 'fa/ars/deleteAssKindDetail.action',
				updateUrl: 'fa/ars/updateAssKindDetail.action',
				getIdUrl: 'common/getId.action?seq=ASSKINDDETAIL_SEQ',
				keyField: 'ak_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});