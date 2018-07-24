Ext.define('erp.view.oa.attention.AttentionMain',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'border', 
				items: [{region: 'center',
					id: 'workrecord',
					layout: 'fit',
					title:'',
					bodyStyle: 'background: #f0f0f0;',
					defaults: {frame:true},
				   items: [{
					id:'grid',
					xtype:'erpAttentionMainGridPanel', 
                     animCollapse: false,
					 title: '所有下属',
					defaults:{
					 columnWidth:1,
					}
					},{
					  id:'detail',
					  layout:'anchor',
					  items:[{
					   anchor:'100% 20%',
					   layout:'column',
					   contentEl:'employeedata'
					  },{
					   anchor:'100% 50%',
					   layout:'column',
					   contentEl:'details'
					  }] 
					}] 
					},
					{
					 region:'west',
					 width:'25%',
					 xtype:'AttentionMainTreePanel',
					 layout:'fit',
					}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});