Ext.define('erp.view.fa.ars.SecondCategory',{ 
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
					saveUrl: 'fa/ars/saveSecondCategoryBase.action',
					deleteUrl: 'fa/ars/deleteSecondCategoryBase.action',
					updateUrl: 'fa/ars/updateSecondCategoryBase.action',
					auditUrl: 'fa/ars/auditSecondCategoryBase.action',
					submitUrl: 'fa/ars/submitSecondCategoryBase.action',
					resSubmitUrl: 'fa/ars/resSubmitSecondCategoryBase.action',
					getIdUrl: 'common/getId.action?seq=SecondCategory_SEQ',
					keyField: 'ca_id',	
					codeField:'ca_code',
					statuscodeField: 'ca_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});