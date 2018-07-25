Ext.define('erp.view.fa.fp.GMYearPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 20%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/saveGMYearPlan.action',
				deleteUrl: 'fa/fp/deleteGMYearPlan.action',
				updateUrl: 'fa/fp/updateGMYearPlan.action',
				getIdUrl: 'common/getId.action?seq=GMYearPlan_SEQ',
				keyField: 'gmp_id',	
				statusField: 'gmp_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%',
				detno:'gmpd_detno',
				keyField:'gmpd_id',
				mainField:'gmpd_gmpid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
