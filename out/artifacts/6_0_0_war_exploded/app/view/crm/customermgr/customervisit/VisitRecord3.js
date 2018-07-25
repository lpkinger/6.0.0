Ext.define('erp.view.crm.customermgr.customervisit.VisitRecord3',{ 
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
								saveUrl: 'crm/customermgr/saveVisitRecord3.action',
								deleteUrl: 'crm/customermgr/deleteVisitRecord3.action',
								updateUrl: 'crm/customermgr/updateVisitRecord3.action',
								auditUrl: 'crm/customermgr/auditVisitRecord3.action',
								resAuditUrl: 'crm/customermgr/resAuditVisitRecord3.action',
								submitUrl: 'crm/customermgr/submitVisitRecord3.action',
								resSubmitUrl:  'crm/customermgr/resSubmitVisitRecord3.action',
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
									_noc:1,
									title:'拜访人员',
									necessaryField: 'pl_name',
									keyField: 'pl_id',
									detno: 'pl_detno',
									mainField: 'pl_vrid' 							
								},{
									id: 'recordDetail',
									xtype: 'recordDetail3',
									_noc:1,
									title:'洽谈对象',
									mainField: 'cup_vrid',
									detno: 'cup_detno',
									necessaryField: 'cup_name',
									keyField: 'cup_id'
								},
								/*{
									id: 'Marketing',
									title:'市场推广策略',
									_noc:1,
									xtype: 'Marketing',
								    keyField: 'ma_id',
								    mainField: 'ma_vrid',
									detno: 'ma_detno'
								},*/{
									title:'推广策略',
									_noc:1,
									xtype: 'VenderMaketing',
									necessaryField:'vm_model'
								},/*{
									id: 'ChanceDetail',
									title:'商机项目进展',
									xtype: 'ChanceDetail',
								    keyField: 'cd_id',
								    mainField: 'cd_viid',									
									detno: 'cd_detno'
								},{
									id: 'Rival',
									title:'竞争对手状况',
									_noc:1,
									xtype: 'Rival',
								    keyField: 'ri_id',
								    mainField: 'ri_vrid',
									necessaryField: 'ri_model',
									detno: 'ri_detno'
								},*/{
									id: 'Price',
									title:'价格与付款条件',
									_noc:1,
									xtype: 'Price',
								    keyField: 'pr_id',
								    mainField: 'pr_vrid',
									detno: 'pr_detno'
								},/*{
									id: 'Expect',
									_noc:1,
									title:'短期预测',
									xtype: 'Expect',
								    keyField: 'ex_id',
								    mainField: 'ex_vrid',
									detno: 'ex_detno'
								},{
									id: 'ProductPlanning',
									_noc:1,
									title:'未来产品规划',
									xtype: 'ProductPlanning',
								    keyField: 'pp_id',
								    mainField: 'pp_vrid',
									detno: 'pp_detno'
								},*/{
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