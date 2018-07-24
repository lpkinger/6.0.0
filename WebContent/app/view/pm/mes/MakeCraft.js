Ext.define('erp.view.pm.mes.MakeCraft',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MakeCraftViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'pm/mes/saveMakeCraft.action',
					deleteUrl: 'pm/mes/deleteMakeCraft.action',
					updateUrl: 'pm/mes/updateMakeCraft.action',
					getIdUrl: 'common/getId.action?seq=MakeCraft_SEQ',
					submitUrl: 'pm/mes/submitMakeCraft.action',
					auditUrl: 'pm/mes/auditMakeCraft.action',
					resAuditUrl: 'pm/mes/resAuditMakeCraft.action',			
					resSubmitUrl: 'pm/mes/resSubmitMakeCraft.action',
					endUrl: 'pm/mes/endMakeCraft.action',
					resEndUrl: 'pm/mes/resEndMakeCraft.action',		
					keyField: 'mc_id',
					codeField: 'mc_code', 
					statusField: 'mc_status',
					statuscodeField: 'mc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'mcd_detno',
					keyField: 'mcd_id',
					mainField: 'mcd_mcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});