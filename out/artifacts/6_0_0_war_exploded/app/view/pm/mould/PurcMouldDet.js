Ext.define('erp.view.pm.mould.PurcMouldDet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'PurcMouldViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 44%',
					saveUrl: 'pm/mould/savePurcMouldDet.action',
					deleteUrl: 'pm/mould/deletePurcMould.action',
					updateUrl: 'pm/mould/updatePurcMouldDet.action',
					auditUrl: 'pm/mould/auditPurcMould.action',
					printUrl: 'pm/mould/printModAlter.action',
					resAuditUrl: 'pm/mould/resAuditPurcMould.action',
					submitUrl: 'pm/mould/submitPurcMould.action',
					resSubmitUrl: 'pm/mould/resSubmitPurcMould.action',
					getIdUrl: 'common/getId.action?seq=PurcMould_SEQ',
					keyField: 'pm_id',
					codeField: 'pm_code',
					statusField: 'pm_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 28%',
					id: 'grid',
					caller: 'Purc!Mould',
					detno: 'pmd_detno',
					keyField: 'pmd_id',
					mainField: 'pmd_pmid',
					allowExtraButtons: true,
					title:'模具采购明细'
				},{
					xtype: 'erpGridPanel5',
					anchor: '100% 28%',
					bbar: new erp.view.core.toolbar.Toolbar,
					id: 'grid2',
					caller: 'PurcMouldDet',
					title:'分期付款明细',
				    keyField: 'pd_id',
				    mainField: 'pd_pmid',
					detno: 'pd_detno',
					getCondition: function() {
						// gridCondition=pmd_pmidIS14790
						var cond = getUrlParam('gridCondition'), reg = /pmd_pmid(IS|=)(\d+)/;
						if(reg.test(cond)) {
							return 'pd_pmid=' + cond.replace(reg, '$2');
						}
						return null;
					}
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});