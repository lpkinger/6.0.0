Ext.define('erp.view.fa.ars.Currencys',{ 
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
					saveUrl: 'fa/ars/saveCurrencys.action',
					deleteUrl: 'fa/ars/deleteCurrencys.action',
					updateUrl: 'fa/ars/updateCurrencys.action',
					bannedUrl: 'fa/ars/bannedCurrencys.action',
					resBannedUrl: 'fa/ars/resBannedCurrencys.action',
					getIdUrl: 'common/getId.action?seq=CURRENCYS_SEQ',
					keyField: 'cr_id',
					codeField: 'cr_code'/*,
					statuscodeField: 'cr_statuscode'*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});