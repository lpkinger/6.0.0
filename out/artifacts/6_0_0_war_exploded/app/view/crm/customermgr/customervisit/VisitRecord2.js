Ext.define('erp.view.crm.customermgr.customervisit.VisitRecord2',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
							layout: 'anchor',
							items: [{
								xtype: 'erpFormPanel',
								title: '<font color=#00868B>拜访记录</font>',
								saveUrl: 'crm/customermgr/saveVisitRecord2.action',
								deleteUrl: 'crm/customermgr/deleteVisitRecord.action',
								updateUrl: 'crm/customermgr/updateVisitRecord2.action',
								auditUrl:  'common/auditCommon.action?caller='+caller,
								resAuditUrl:'common/resAuditCommon.action?caller='+caller,
								submitUrl: 'crm/customermgr/submitVisitRecord.action',
								resSubmitUrl:  'crm/customermgr/resSubmitVisitRecord.action',
								getIdUrl: 'common/getId.action?seq=VISITRECORD_SEQ',
								keyField: 'vr_id',
								codeField: 'vr_status',
								statusField: 'vr_statuscode',         
								anchor: '100% 70%'
							},{
								xtype:'tabpanel',
								anchor: '100% 30%',
								items:[{
									id: 'InfoGrid',
									title:'推荐产品信息',
									xtype: 'InfoGrid',
									_noc:1,
								    keyField: 'pi_id',
								    mainField: 'pi_vrid',
									necessaryField: 'pi_brand',
									detno: 'pi_detno'
								},{
									title:'客户项目信息',
									_noc:1,
									xtype: 'VisitFeedBack',
									necessaryField:'fb_projname'
								}]
							}]
		}); 
		me.callParent(arguments); 
	} 
});