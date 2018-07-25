Ext.define('erp.view.co.cost.ProdIOCateSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'Viewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'co/cost/saveProdIOCateSet.action',
					deleteUrl: 'co/cost/deleteProdIOCateSet.action',
					updateUrl: 'co/cost/updateProdIOCateSet.action',
					auditUrl: 'co/cost/auditProdIOCateSet.action',
					resAuditUrl: 'co/cost/resAuditProdIOCateSet.action',
					submitUrl: 'co/cost/submitProdIOCateSet.action',
					resSubmitUrl: 'co/cost/resSubmitProdIOCateSet.action',
					bannedUrl: 'co/cost/bannedProdIOCateSet.action',
					resBannedUrl: 'co/cost/resBannedProdIOCateSet.action',
					getIdUrl: 'common/getId.action?seq=PRODIOCATESET_SEQ',
					keyField: 'pc_id',
					codeField: 'pc_code',
					statusField: 'pc_status',
					statuscodeField: 'pc_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});