Ext.define('erp.view.scm.purchase.VerifyApplyDetail.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',  
				xtype: 'erpVerifyApplyDetailFormPanel',
				anchor: '100% 35%',
				getIdUrl: 'common/getId.action?seq=VERIFYAPPLYDETAIL_SEQ',
				keyField: 'vad_id',
				codeField: 'vad_code'
			},{
				region: 'center', 
				xtype: 'erpVerifyApplyDetailGridPanel',
				anchor: '100% 65%', 
				detno: 'vadp_detno',
				necessaryField: 'vadp_qty',
				keyField: 'vadp_id',
				mainField: 'vadp_vadid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});