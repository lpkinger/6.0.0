Ext.define('erp.view.fs.cust.MoneyDemandPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 20%',
					saveUrl: 'fs/cust/saveMoneyDemandPlan.action',
					deleteUrl: 'fs/cust/deleteMoneyDemandPlan.action',
					updateUrl: 'fs/cust/updateMoneyDemandPlan.action',
					auditUrl: 'fs/cust/auditMoneyDemandPlan.action',
					resAuditUrl: 'fs/cust/resAuditMoneyDemandPlan.action',
					submitUrl: 'fs/cust/submitMoneyDemandPlan.action',
					resSubmitUrl: 'fs/cust/resSubmitMoneyDemandPlan.action',
					getIdUrl: 'common/getId.action?seq=MONEYDEMANDPLAN_SEQ',
					keyField: 'mp_id',
					codeField: 'mp_code',
					statusField: 'mp_status',
					statuscodeField: 'mp_statuscode'
				},{				
					xtype: 'erpGridPanel2',
					anchor : '100% 80%',
					detno: 'mpd_detno',
					keyField: 'mpd_id',
					mainField: 'mpd_mpid',
					allowExtraButtons:false					
				}]
		}); 
		
		this.callParent(arguments); 
	}
});