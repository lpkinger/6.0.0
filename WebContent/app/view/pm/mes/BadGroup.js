Ext.define('erp.view.pm.mes.BadGroup',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BadGroupViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveBadGroup.action',
					deleteUrl: 'pm/mes/deleteBadGroup.action',
					updateUrl: 'pm/mes/updateBadGroup.action',
					getIdUrl: 'common/getId.action?seq=BadGroup_SEQ',
					submitUrl: 'pm/mes/submitBadGroup.action',
					auditUrl: 'pm/mes/auditBadGroup.action',
					resAuditUrl: 'pm/mes/resAuditBadGroup.action',			
					resSubmitUrl: 'pm/mes/resSubmitBadGroup.action',
					keyField: 'bg_id',
					codeField: 'bg_code', 
					statusField: 'bg_status',
					statuscodeField: 'bg_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});