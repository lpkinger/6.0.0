Ext.define('erp.view.crm.marketmgr.marketresearch.Task',{ 
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
					saveUrl: 'plm/task/saveTask.action',
					deleteUrl: 'plm/task/deleteTask.action',
					updateUrl: 'plm/task/updateTask.action',
					submitUrl: 'plm/task/submitTask.action',
					auditUrl: 'plm/task/auditTask.action',
					getIdUrl: 'common/getId.action?seq=PROJECTTASK_SEQ',
					keyField: 'id',
					codeField:'taskcode'
				}/*,
				{	anchor: '100% 1000%',
					layout:'column',
				    items:[{
				    	autoScroll : true,
				    	columnWidth:1,
				    	height:210,
				    	title:'任务分配',
						xtype: 'erpGridPanel2',
						detno: 'ra_detno',
						necessaryField: 'ra_resourcecode',
						keyField: 'ra_id',
						mainField: 'ra_taskid'
				    },{
				    	id:'showreport',
				    	columnWidth:.5,
				    	height:210
				    }]

				}*/
				
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});