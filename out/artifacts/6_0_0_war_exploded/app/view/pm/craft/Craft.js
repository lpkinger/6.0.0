Ext.define('erp.view.pm.craft.Craft',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'CraftViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mes/saveCraft.action',
					deleteUrl: 'pm/mes/deleteCraft.action',
					updateUrl: 'pm/mes/updateCraft.action',
					auditUrl: 'pm/mes/auditCraft.action',
					resAuditUrl: 'pm/mes/resAuditCraft.action',
					submitUrl: 'pm/mes/submitCraft.action',
					resSubmitUrl: 'pm/mes/resSubmitCraft.action',
					getIdUrl: 'common/getId.action?seq=CRAFT_SEQ',
					keyField: 'cr_id',
					codeField: 'cr_code',
					statusField: 'cr_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'cd_detno',
					keyField: 'cd_id',
					mainField: 'cd_crid',
					necessaryField: 'cd_stepcode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});