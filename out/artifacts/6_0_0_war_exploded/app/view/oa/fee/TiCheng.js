Ext.define('erp.view.oa.fee.TiCheng',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'oa/fee/saveTiCheng.action',
					deleteUrl: 'oa/fee/deleteTiCheng.action',
					updateUrl: 'oa/fee/updateTiCheng.action',
					getIdUrl: 'common/getId.action?seq=TiCheng_SEQ',
					auditUrl: 'oa/fee/auditTiCheng.action',
					resAuditUrl: 'oa/fee/resAuditTiCheng.action',
					submitUrl: 'oa/fee/submitTiCheng.action',
					resSubmitUrl: 'oa/fee/resSubmitTiCheng.action',
					keyField: 'tc_id',
					codeField: 'tc_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					keyField: 'tcd_id',
					detno: 'tcd_detno',
					mainField: 'tcd_tcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});