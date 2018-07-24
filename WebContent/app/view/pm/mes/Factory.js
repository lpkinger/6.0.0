Ext.define('erp.view.pm.mes.Factory',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'FactoryViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveFactory.action',
					deleteUrl: 'pm/mes/deleteFactory.action',
					updateUrl: 'pm/mes/updateFactory.action',
					getIdUrl: 'common/getId.action?seq=Factory_SEQ',
					submitUrl: 'pm/mes/submitFactory.action',
					auditUrl: 'pm/mes/auditFactory.action',
					resAuditUrl: 'pm/mes/resAuditFactory.action',			
					resSubmitUrl: 'pm/mes/resSubmitFactory.action',
					keyField: 'fa_id',
					codeField: 'fa_code', 
					statusField: 'fa_status',
					statuscodeField: 'fa_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});