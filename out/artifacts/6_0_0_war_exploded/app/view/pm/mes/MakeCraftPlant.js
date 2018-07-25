Ext.define('erp.view.pm.mes.MakeCraftPlant',{ 
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
					anchor: '100% 45%',
					saveUrl: 'pm/mes/saveMakeCraft.action',
					deleteUrl: 'pm/mes/deleteMakeCraft.action',
					updateUrl: 'pm/mes/updateMakeCraft.action',
					getIdUrl: 'common/getId.action?seq=MakeCraft_SEQ',
					submitUrl: 'pm/mes/submitMakeCraft.action',
					auditUrl: 'pm/mes/auditMakeCraft.action',
					resAuditUrl: 'pm/mes/resAuditMakeCraft.action',			
					resSubmitUrl: 'pm/mes/resSubmitMakeCraft.action',
					resEndUrl: 'pm/mes/resEndMakeCraft.action',
					keyField: 'mc_id',
					codeField: 'mc_code', 
					statusField: 'mc_status',
					statuscodeField: 'mc_statuscode'
				},{
					xtype: 'erpDisplayGridPanel',
					anchor: '100% 55%',
					querycondition : "(mm_code,nvl(mm_mdcode,' '))=(select mc_makecode,mc_code from makecraft where @formCondition )"
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});