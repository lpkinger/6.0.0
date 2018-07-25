Ext.define('erp.view.scm.reserve.SawingSheet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'SawingSheetViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 26%',
					saveUrl: 'scm/reserve/saveSawingSheet.action',
					deleteUrl: 'scm/reserve/deleteSawingSheet.action',
					updateUrl: 'scm/reserve/updateSawingSheet.action',
					auditUrl: 'scm/reserve/auditSawingSheet.action',
					resAuditUrl: 'scm/reserve/resAuditSawingSheet.action',
					submitUrl: 'scm/reserve/submitSawingSheet.action',
					resSubmitUrl: 'scm/reserve/resSubmitSawingSheet.action',
					postUrl: 'scm/reserve/postSawingSheet.action',
					resPostUrl: 'scm/reserve/resPostSawingSheet.action',
					getIdUrl: 'common/getId.action?seq=SAWINGSHEET_SEQ',
					keyField: 'ss_id',
					codeField: 'ss_code',
					statusField: 'ss_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 37%',
					id: 'grid',
					caller: 'SawingSheet',
					detno: 'ssb_detno',
					keyField: 'ssb_id',
					mainField: 'ssb_ssid',
					allowExtraButtons: true,
					title:'开料前物料明细'
				},{
					xtype: 'erpGridPanel5',
					anchor: '100% 37%',
					bbar: new erp.view.core.toolbar.Toolbar,
					id: 'grid2',
					caller: 'SawingSheetAfter',
					title:'开料后物料明细',
				    keyField: 'ssa_id',
				    mainField: 'ssa_ssid',
					detno: 'ssa_detno',
					getCondition: function() {
						// gridCondition=ssb_pmidIS14790
						var cond = getUrlParam('gridCondition'), reg = /ssb_ssid(IS|=)(\d+)/;
						if(reg.test(cond)) {
							return 'ssa_ssid=' + cond.replace(reg, '$2');
						}
						return null;
					}
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});