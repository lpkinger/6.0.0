Ext.define('erp.view.pm.mould.PriceMould',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'pm/mould/savePriceMould.action',
				deleteUrl: 'pm/mould/deletePriceMould.action',
				updateUrl: 'pm/mould/updatePriceMould.action',
				auditUrl: 'pm/mould/auditPriceMould.action',
				resAuditUrl: 'pm/mould/resAuditPriceMould.action',
				submitUrl: 'pm/mould/submitPriceMould.action',
				resSubmitUrl: 'pm/mould/resSubmitPriceMould.action',
				bannedUrl: 'pm/mould/bannedPriceMould.action',
				resBannedUrl: 'pm/mould/resBannedPriceMould.action',
				getIdUrl: 'common/getId.action?seq=PriceMould_SEQ',
				keyField: 'pd_id',
				codeField: 'pd_code',
				statusField: 'pd_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 25%',
				caller: 'PriceMould',
				detno: 'pdd_detno',
				keyField: 'pdd_id',
				mainField: 'pdd_pdid',
				allowExtraButtons: true,
				title:'模具明细'
			},{
				xtype: 'erpGridPanel5',
				anchor: '100% 25%',
				bbar: new erp.view.core.toolbar.Toolbar(),
				id: 'grid2',
				caller: 'PriceMouldDetail',
				title:'物料明细',
				detno: 'pmd_detno',
				keyField: 'pmd_id',
				mainField: 'pmd_pdid',
				getCondition: function() {
					var cond = getUrlParam('gridCondition'), reg = /pdd_pdid(IS|=)('|\d+)/;
					if(reg.test(cond)) {
						return 'pmd_pdid=' + cond.replace(reg, '$2');
					}
					return null;
				}
			}]
		}); 
		me.callParent(arguments); 
	} 
});