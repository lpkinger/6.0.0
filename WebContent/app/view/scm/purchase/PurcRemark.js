Ext.define('erp.view.scm.purchase.PurcRemark',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',		
				saveUrl: 'scm/purchase/savePurcRemark.action',
				deleteUrl: 'scm/purchase/deletePurcRemark.action',
				updateUrl: 'scm/purchase/updatePurcRemark.action',
				getIdUrl: 'common/getId.action?seq=PURCREMARK_SEQ',
				bannedUrl: 'scm/purchase/bannedPurcRemark.action',
				resBannedUrl: 'scm/purchase/resBannedPurcRemark.action',
				keyField: 'pr_id',
				codeField: 'pr_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'prd_detno',
				keyField: 'prd_id',
				mainField: 'prd_prid',
				necessaryField:'prd_remark'
			}]
		}); 
		me.callParent(arguments); 
	} 
});