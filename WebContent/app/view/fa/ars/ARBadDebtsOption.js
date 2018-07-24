Ext.define('erp.view.fa.ars.ARBadDebtsOption',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
//				id:'ARBadDebtsOptionViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/ars/saveARBadDebtsOption.action',
					deleteUrl: 'fa/ars/deleteARBadDebtsOption.action',
					updateUrl: 'fa/ars/updateARBadDebtsOption.action',
					auditUrl: 'fa/ars/auditARBadDebtsOption.action',
					resAuditUrl: 'fa/ars/resAuditARBadDebtsOption.action',
					submitUrl: 'fa/ars/submitARBadDebtsOption.action',
					resSubmitUrl: 'fa/ars/resSubmitARBadDebtsOption.action',
					getIdUrl: 'common/getId.action?seq=ARBADDEBTSOPTION_SEQ',
					keyField: 'bdo_id',
					codeField: 'bdo_code',				
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});