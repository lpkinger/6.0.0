Ext.define('erp.view.pm.mps.GoodsChange',{ 
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
					saveUrl: 'pm/mps/saveGoodsChange.action',
					deleteUrl: 'pm/mps/deleteGoodsChange.action',
					updateUrl: 'pm/mps/updateGoodsChange.action',
					submitUrl: 'pm/mps/submitGoodsChange.action',
					resSubmitUrl: 'pm/mps/resSubmitGoodsChange.action',
					auditUrl: 'pm/mps/auditGoodsChange.action',
					resAuditUrl: 'pm/mps/resAuditGoodsChange.action',
					getIdUrl: 'common/getId.action?seq=GOODSCHANGE_SEQ',
					keyField: 'gc_id',
					codeField:'gc_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%',
					detno: 'gcd_detno',
					necessaryField: 'gd_code',
					keyField: 'gcd_id',
					mainField: 'gcd_gcid',
					allowExtraButtons : true
				  }]
			}]
		}); 
		me.callParent(arguments); 
	} 
});