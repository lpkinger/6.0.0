Ext.define('erp.view.pm.mes.BadCode',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BadCodeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveBadCode.action',
					deleteUrl: 'pm/mes/deleteBadCode.action',
					updateUrl: 'pm/mes/updateBadCode.action',
					getIdUrl: 'common/getId.action?seq=BadCode_SEQ',
					submitUrl: 'pm/mes/submitBadCode.action',
					auditUrl: 'pm/mes/auditBadCode.action',
					resAuditUrl: 'pm/mes/resAuditBadCode.action',			
					resSubmitUrl: 'pm/mes/resSubmitBadCode.action',
					keyField: 'bc_id',
					codeField: 'bc_code', 
					statusField: 'bc_status',
					statuscodeField: 'bc_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});