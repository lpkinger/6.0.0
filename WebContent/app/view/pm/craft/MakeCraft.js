Ext.define('erp.view.pm.craft.MakeCraft',{ 
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
					saveUrl: 'pm/craft/saveMakeCraft.action',
					deleteUrl: 'pm/craft/deleteMakeCraft.action',
					updateUrl: 'pm/craft/updateMakeCraft.action',
					submitUrl: 'pm/craft/submitMakeCraft.action',
					auditUrl: 'pm/craft/auditMakeCraft.action',
					resAuditUrl: 'pm/craft/resAuditMakeCraft.action',					
					resSubmitUrl: 'pm/craft/resSubmitMakeCraft.action',
					getIdUrl: 'common/getId.action?seq=MAKECRAFT_SEQ',
					keyField: 'mc_id',
					statusField: 'mc_status',
					codeField: 'mc_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});