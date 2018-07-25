Ext.define('erp.view.oa.emFoodCard.EmFoodCard',{ 
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
					anchor: '100% 40%',
					saveUrl: 'oa/emFoodCard/saveEmFoodCard.action',
					deleteUrl: 'oa/emFoodCard/deleteEmFoodCard.action',
					updateUrl: 'oa/emFoodCard/updateEmFoodCard.action',
					auditUrl: 'oa/emFoodCard/auditEmFoodCard.action',
					resAuditUrl: 'oa/emFoodCard/resAuditEmFoodCard.action',
					submitUrl: 'oa/emFoodCard/submitEmFoodCard.action',
					resSubmitUrl: 'oa/emFoodCard/resSubmitEmFoodCard.action',
					getIdUrl: 'common/getId.action?seq=EmFoodCard_SEQ',
					keyField: 'ef_id',
					codeField: 'ef_code',
					statusField: 'ef_status',
					statuscodeField: 'ef_statuscode'
						
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'efd_detno',					
					keyField: 'efd_id',
					mainField: 'efd_efid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});