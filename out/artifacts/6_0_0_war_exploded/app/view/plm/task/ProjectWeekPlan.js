Ext.define('erp.view.plm.task.ProjectWeekPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'erpProjectWeekPlanTree',
				region:'west'
			},{
				region:'center',
				layout:'border',				
				items: [{         
					xtype: "erpBatchDealFormPanel",  
					title:null/*'项目周报'*/,
					padding:'5 0 0 0',
					region:'north'
				},{        
					xtype: "erpBatchDealGridPanel",  
					region:'center',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					})],
					selModel:{},
					features:[]
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});