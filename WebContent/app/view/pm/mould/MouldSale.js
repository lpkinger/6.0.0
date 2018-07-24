Ext.define('erp.view.pm.mould.MouldSale',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MouldSaleViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mould/saveMouldSale.action',
					deleteUrl: 'pm/mould/deleteMouldSale.action',
					updateUrl: 'pm/mould/updateMouldSale.action',
					auditUrl: 'pm/mould/auditMouldSale.action',
					resAuditUrl: 'pm/mould/resAuditMouldSale.action',
					submitUrl: 'pm/mould/submitMouldSale.action',
					resSubmitUrl: 'pm/mould/resSubmitMouldSale.action',
					getIdUrl: 'common/getId.action?seq=MOD_SALE_SEQ',
					keyField: 'msa_id',
					codeField: 'msa_code',
					statusField: 'msa_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'msd_detno',
					necessaryField: 'msd_pscode',
					keyField: 'msd_id',
					mainField: 'msd_msaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});