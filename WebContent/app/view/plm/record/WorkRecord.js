Ext.define('erp.view.plm.record.WorkRecord',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		var urlCondition = getUrlParam('gridCondition');
		urlCondition = urlCondition == null || urlCondition == "null" ? "" : urlCondition;
		gridCondition = gridCondition + urlCondition;
		gridCondition = gridCondition.replace(/IS/g, "=");
		var condition = gridCondition + " order by wr_recorddate desc";
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpRecordPanel',
					title:'研发任务',
					anchor: '100% 65%',
					saveUrl: 'plm/record/saveWorkRecord.action',
					updateUrl:'plm/record/updateWorkRecord.action',
					getIdUrl: 'common/getId.action?seq=WORKRECORD_SEQ',
					keyField: 'wr_id'
				  }, { 
					xtype:'tabpanel',
					anchor: '100% 35%',
                    items:[{
					   title:'提交记录',	
					   xtype: 'erpGridPanel2',
					   bbar: null,
					   frame:true,
					   anchor: '100% 35%',					
					   keyField: 'wr_id',
					   mainField: 'wr_taskid',
					   condition:condition
					},{
					   title:'关联业务单据',	
					   xtype: 'relationPanel',
					   bbar: null,
					   frame:true,
					   anchor: '100% 35%',					
					   keyField: 'wr_id',
					   mainField: 'wr_taskid'
					},{
                       title:'任务要求',
					   xtype: 'htmleditor',
					   height:300,
					   anchor: '100% 35%',
					   id:'taskdescription',
					   readOnly:true	   
					},{
					   title:'查看附件',
                       anchor: '100% 35%',
                       id:'attachs'
					}]
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});