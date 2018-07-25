Ext.define('erp.view.pm.mes.Line',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'LineViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveLine.action',
					deleteUrl: 'pm/mes/deleteLine.action',
					updateUrl: 'pm/mes/updateLine.action',
					getIdUrl: 'common/getId.action?seq=Line_SEQ',
					submitUrl: 'pm/mes/submitLine.action',
					auditUrl: 'pm/mes/auditLine.action',
					resAuditUrl: 'pm/mes/resAuditLine.action',			
					resSubmitUrl: 'pm/mes/resSubmitLine.action',
					keyField: 'li_id',
					codeField: 'li_code', 
					statusField: 'li_status',
					statuscodeField: 'li_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});