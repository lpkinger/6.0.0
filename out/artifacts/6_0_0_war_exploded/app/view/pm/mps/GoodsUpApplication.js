Ext.define('erp.view.pm.mps.GoodsUpApplication',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [
				{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'pm/mps/saveGoodsUpApplication.action',
					deleteUrl: 'pm/mps/deleteGoodsUpApplication.action',
					updateUrl: 'pm/mps/updateGoodsUpApplication.action',
					submitUrl: 'pm/mps/submitGoodsUpApplication.action',
					resSubmitUrl: 'pm/mps/resSubmitGoodsUpApplication.action',
					auditUrl: 'pm/mps/auditGoodsUpApplication.action',
					resAuditUrl: 'pm/mps/resAuditGoodsUpApplication.action',
					getIdUrl: 'common/getId.action?seq=GOODSUP_SEQ',
					keyField: 'gu_id',
					codeField:'gu_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%',
					detno: 'gd_detno',
					necessaryField: 'gd_code',
					keyField: 'gd_id',
					mainField: 'gd_guid',
					allowExtraButtons : true
				  }]
			}]
		}); 
		me.callParent(arguments); 
	} 
});