Ext.define('erp.view.pm.craft.BOMChange',{ 
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
					anchor: '100% 50%',
					saveUrl: 'pm/craft/saveBOM.action',
					deleteUrl: 'pm/craft/deleteBOM.action',
					updateUrl: 'pm/craft/updateBOM.action',
					submitUrl: 'pm/craft/submitBOM.action',
					auditUrl: 'pm/craft/auditBOM.action',
					resAuditUrl: 'pm/craft/resAuditBOM.action',					
					resSubmitUrl: 'pm/craft/resSubmitBOM.action',
					getIdUrl: 'common/getId.action?seq=BOM_SEQ',
					keyField: 'bo_id',
					statusField: 'bo_status',
					codeField: 'bo_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});