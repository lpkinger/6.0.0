Ext.define('erp.view.co.cost.ManuFactFee',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					autoScroll: true,
					saveUrl: 'co/cost/saveManuFactFee.action',
					deleteUrl: 'co/cost/deleteManuFactFee.action',
					updateUrl: 'co/cost/updateManuFactFee.action',
					auditUrl: 'co/cost/auditManuFactFee.action',
					resAuditUrl: 'co/cost/resAuditManuFactFee.action',
					submitUrl: 'co/cost/submitManuFactFee.action',
					resSubmitUrl: 'co/cost/resSubmitManuFactFee.action',
					getIdUrl: 'common/getId.action?seq=MANUFACTFEE_SEQ',
					keyField: 'mf_id', 
					codeField: 'mf_code',
					statusField: 'mf_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});