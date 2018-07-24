Ext.define('erp.view.drp.distribution.SalePriceApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'saleViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'drp/distribution/saveSalePriceApply.action',
					deleteUrl: 'drp/distribution/deleteSalePriceApply.action',
					updateUrl: 'drp/distribution/updateSalePriceApply.action',
					auditUrl: 'drp/distribution/auditSalePriceApply.action',
					resAuditUrl: 'drp/distribution/resAuditSalePriceApply.action',
					submitUrl: 'drp/distribution/submitSalePriceApply.action',
					resSubmitUrl: 'drp/distribution/resSubmitSalePriceApply.action',
					getIdUrl: 'common/getId.action?seq=SALEPRICEAPPLY_SEQ',
					keyField: 'sp_id',
					codeField: 'sp_code',
					statusField: 'sp_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'spd_detno',
					necessaryField: 'spd_prodcode',
					keyField: 'spd_id',
					mainField: 'spd_spid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});