Ext.define('erp.view.crm.customermgr.customervisit.VisitRecord4',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
//	autoScroll: true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
							layout: 'anchor',
							items: [{
								anchor: '100% 70%',
								xtype: 'erpFormPanel',
								saveUrl: 'crm/customermgr/saveVisitRecord4.action',
								deleteUrl: 'crm/customermgr/deleteVisitRecord4.action',
								updateUrl: 'crm/customermgr/updateVisitRecord4.action',
								auditUrl: 'common/auditCommon.action?caller='+caller,
								resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
								submitUrl: 'common/submitCommon.action?caller='+caller,
								resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
								getIdUrl: 'common/getId.action?seq=VisitRecord_SEQ',
								keyField: 'vr_id',
								codeField: 'vr_status',
								statusField: 'vr_statuscode' 
							},{
								xtype:'tabpanel',
								anchor: '100% 30%',
								items:[{
									id: 'recordDetailDet',
									xtype: 'recordDetailDet', 
									title:'拜访人员',
									_noc:1,
									necessaryField: 'pl_name',
									keyField: 'pl_id',
									detno: 'pl_detno',
									mainField: 'pl_vrid' 							
								},{
									id: 'recordDetail',
									xtype: 'recordDetail',
									_noc:1,
									title:'洽谈对象',
									mainField: 'cup_vrid',
									detno: 'cup_detno',
									necessaryField: 'cup_name',
									keyField: 'cup_id'
								},/*{
									id: 'Marketing',
									title:'市场推广策略',
									xtype: 'Marketing',
								    keyField: 'ma_id',
								    mainField: 'ma_vrid',
									detno: 'ma_detno'
								},*/{
									title:'原厂推广策略',
									_noc:1,
									xtype: 'VenderMaketing',
									necessaryField:'vm_model',
									caller:'VenderMaketing!Resource'
								},/*{
									id: 'ChanceDetail',
									title:'商机项目进展',
									xtype: 'ChanceDetail',
								    keyField: 'cd_id',
								    mainField: 'cd_viid',									
									detno: 'cd_detno'
								},*/{
									id: 'Rival',
									title:'竞争对手状况',
									_noc:1,
									xtype: 'Rival',
								    keyField: 'ri_id',
								    mainField: 'ri_vrid',
									necessaryField: 'ri_model',
									detno: 'ri_detno',
									caller:'Rival!Resource'
								},{
									id: 'Price',
									title:'价格与付款条件',
									_noc:1,
									xtype: 'Price',
								    keyField: 'pr_id',
								    mainField: 'pr_vrid',
									detno: 'pr_detno',
									caller:'Price!Resource'
								},/*{
									id: 'Expect',
									title:'短期预测',
									xtype: 'Expect',
								    keyField: 'ex_id',
								    mainField: 'ex_vrid',
									detno: 'ex_detno'
								},*/{
									id: 'ProductPlanning',
									_noc:1,
									title:'产品规划',
									xtype: 'ProductPlanning',
								    keyField: 'pp_id',
								    mainField: 'pp_vrid',
									detno: 'pp_detno'
								},{
									xtype: 'erpGridPanel2',
									_noc:1,
									title:'费用报销',
									detno: 'vrd_detno',				
									keyField: 'vrd_id',
									mainField: 'vrd_vrid',
	    							necessaryField: 'vrd_costname'	  
								}]
							}]
		}); 
		me.callParent(arguments); 
	} 
});