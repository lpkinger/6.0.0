Ext.define('erp.view.crm.marketmgr.resourcemgr.TrainReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					id:'taskForm',
					_noc:1,
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					//getIdUrl: 'common/getId.action?seq=TrainReport_SEQ',
					formCondition:cond,
					enableTools: false,
					caller:'ShowTrainOrder'
				},{	
					enableTools: false,
					id:'form',
					xtype: 'erpFormPanel',
					_noc:1,
					anchor: '100% 70%', 
					updateUrl: 'crm/customermgr/updateTrainReport.action?caller='+caller+'&_noc=1',
					auditUrl: 'crm/customermgr/auditTrainReport.action?caller='+caller+'&_noc=1',
					resAuditUrl: 'crm/customermgr/resAuditTrainReport.action?caller='+caller+'&_noc=1',
					submitUrl: 'crm/customermgr/submitTrainReport.action?caller='+caller+'&_noc=1',
					resSubmitUrl: 'crm/customermgr/resSubmitTrainReport.action?caller='+caller+'&_noc=1',
					getIdUrl: 'common/getId.action?seq=TrainReport_SEQ',
					keyField: 'tr_id',
					codeField: 'tr_code',
					statusField: 'tr_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});