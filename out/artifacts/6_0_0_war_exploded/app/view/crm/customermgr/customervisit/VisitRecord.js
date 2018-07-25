Ext.define('erp.view.crm.customermgr.customervisit.VisitRecord',{ 
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
								saveUrl: 'crm/customermgr/saveVisitRecord.action',
								deleteUrl: 'crm/customermgr/deleteVisitRecord.action',
								updateUrl: 'crm/customermgr/updateVisitRecord.action',
								auditUrl: 'crm/customermgr/auditVisitRecord.action',
								resAuditUrl: 'crm/customermgr/resAuditVisitRecord.action',
								submitUrl: 'crm/customermgr/submitVisitRecord.action',
								resSubmitUrl:  'crm/customermgr/resSubmitVisitRecord.action',
								getIdUrl: 'common/getId.action?seq=VISITRECORD_SEQ',
								pingjiaUrl:'crm/customermgr/updateVisitRecordPingjia.action',
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
									title:'洽谈对象',
									_noc:1,
									mainField: 'cup_vrid',
									detno: 'cup_detno',
									necessaryField: 'cup_name',
									keyField: 'cup_id'
								},{
									id: 'InfoGrid',
									title:'推广项目信息',
									xtype: 'InfoGrid',
									_noc:1,
									relative : true,
								    keyField: 'pi_id',
								    mainField: 'pi_vrid',
									necessaryField: 'pi_brand',
									detno: 'pi_detno'
								},{
									title:'客户项目信息',
									_noc:1,
									xtype: 'VisitFeedBack',
									necessaryField:'fb_projname'
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