Ext.define('erp.view.pm.mes.Source',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'SourceViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveSource.action',
					deleteUrl: 'pm/mes/deleteSource.action',
					updateUrl: 'pm/mes/updateSource.action',
					getIdUrl: 'common/getId.action?seq=Source_SEQ',
					submitUrl: 'pm/mes/submitSource.action',
					auditUrl: 'pm/mes/auditSource.action',
					resAuditUrl: 'pm/mes/resAuditSource.action',			
					resSubmitUrl: 'pm/mes/resSubmitSource.action',
					keyField: 'sc_id',
					codeField: 'sc_code', 
					statusField: 'sc_status',
					statuscodeField: 'sc_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});