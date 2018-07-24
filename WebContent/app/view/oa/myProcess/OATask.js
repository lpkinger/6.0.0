Ext.define('erp.view.oa.myProcess.OATask',{ 
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
					anchor: '100% 60%',
					saveUrl: 'oa/myProcess/saveOATask.action',
					deleteUrl: 'oa/myProcess/deleteOATask.action',
					updateUrl: 'oa/myProcess/updateOATask.action',
					getIdUrl: 'common/getId.action?seq=ProjectTask_SEQ',
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'ma_id'
				},{
					xtype:'tabpanel',
					anchor: '100% 40%',
					items:[{
						title:'待办事宜',
						xtype: 'erpGridPanel2',
						anchor: '100% 30%', 
						//detno: 'mad_detno',
						keyField: 'id',
						mainField: 'parentid'
					},{
						//id: 'recordDetailDet',
						xtype: 'OATaskRecord', 
						title:'任务报告',
						necessaryField: 'wr_redcord'
						//keyField: 'pl_id',
						//detno: 'pl_detno',
						//mainField: 'pl_vrid' 							
					},{
						xtype: 'OATaskChange', 
						title:'任务变更',
						necessaryField: 'wr_remark'
					}]
				}/*{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'mad_detno',
					//necessaryField: 'md_participants',
					keyField: 'mad_id',
					mainField: 'mad_maid'
				}*/]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});