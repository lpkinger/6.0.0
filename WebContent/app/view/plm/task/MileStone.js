Ext.define('erp.view.plm.task.MileStone',{ 
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
					anchor: '100% 40%',
					saveUrl: 'plm/task/saveMileStone.action',
					deleteUrl: 'plm/task/deleteMileStone.action',
					updateUrl: 'plm/task/updateMileStone.action',
					getIdUrl: 'common/getId.action?seq=MileStone_SEQ',
					auditUrl: 'plm/task/auditMileStone.action',
					resAuditUrl: 'plm/task/resAuditMileStone.action',
					submitUrl: 'plm/task/submitMileStone.action',
					resSubmitUrl: 'plm/task/resSubmitMileStone.action',
					keyField: 'ms_id',
					codeField: 'ms_code',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'msd_detno',
					keyField: 'msd_id',
					mainField: 'msd_msid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});