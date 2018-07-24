Ext.define('erp.view.pm.mes.StencilUse',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'StencilUseViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveStencilUse.action',
					deleteUrl: 'pm/mes/deleteStencilUse.action',
					updateUrl: 'pm/mes/updateStencilUse.action',
					getIdUrl: 'common/getId.action?seq=Device_SEQ',
					submitUrl: 'pm/mes/submitStencilUse.action',
					auditUrl: 'pm/mes/auditStencilUse.action',
					resAuditUrl: 'pm/mes/resAuditStencilUse.action',			
					resSubmitUrl: 'pm/mes/resSubmitStencilUse.action',
					keyField: 'su_id',
					codeField: 'su_code', 
					statusField: 'su_status',
					statuscodeField: 'su_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});