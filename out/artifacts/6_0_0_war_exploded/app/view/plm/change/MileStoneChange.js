Ext.define('erp.view.plm.change.MileStoneChange',{ 
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
					saveUrl: 'plm/change/saveMileStoneChange.action',
					deleteUrl: 'plm/change/deleteMileStoneChange.action',
					updateUrl: 'plm/change/updateMileStoneChange.action',
					auditUrl: 'plm/change/auditMileStoneChange.action',
					resAuditUrl: 'plm/change/resAuditMileStoneChange.action',
					submitUrl: 'plm/change/submitMileStoneChange.action',
					resSubmitUrl: 'plm/change/resSubmitMileStoneChange.action',
					getIdUrl: 'common/getId.action?seq=MileStoneChange_SEQ',
					keyField: 'mc_id',
					statusField: 'mc_statuscode'
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});