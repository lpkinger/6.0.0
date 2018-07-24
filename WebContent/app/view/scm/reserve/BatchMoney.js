Ext.define('erp.view.scm.reserve.BatchMoney',{ 
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
					updateUrl: 'scm/reserve/updateBatchMoney.action',
					getIdUrl: 'common/getId.action?seq=BATCH_SEQ',
					keyField: 'ba_id',
					codeField: 'ba_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});