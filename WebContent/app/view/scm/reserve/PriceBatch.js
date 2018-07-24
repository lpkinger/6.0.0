Ext.define('erp.view.scm.reserve.PriceBatch',{ 
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
					anchor: '100% 20%',
					saveUrl: 'scm/reserve/savePriceBatch.action',
					updateUrl: 'scm/reserve/updatePriceBatch.action',
					deleteUrl: 'scm/reserve/deletePriceBatch.action',
					auditUrl: 'scm/reserve/auditPriceBatch.action',
					resAuditUrl: 'scm/reserve/resAuditPriceBatch.action',
					submitUrl: 'scm/reserve/submitPriceBatch.action',
					resSubmitUrl: 'scm/reserve/resSubmitPriceBatch.action',
					getIdUrl: 'common/getId.action?seq=UPDATEMAINFORM_SEQ',
					keyField: 'em_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 80%', 
					detno: 'pbu_detno',
					keyField: 'pbu_id',
					mainField: 'pbu_emid'/*,
					bbar: {
						xtype: 'erpToolbar',
						enableDelete:false
					}*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});