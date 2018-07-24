Ext.define('erp.view.plm.record.SpecialRecord',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	autoScroll:true,
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'border', 
				items: [{region: 'center',
					id: 'workrecord',
					layout: 'column',
					autoScroll: true,
					bodyStyle: 'background: #f0f0f0;',
					defaults: {frame:true},
					items: [{
					id:'grid',
					xtype: 'erpGridPanel2',
					layout:'column',
					defaults:{
					 columnWidth:1,
					},
					bbar: {},
					},{
					  xtype:'erpFormPanel',
					  title:'',
					  hidden:true,
					  params:{
					   caller:'WorkRecord!Task',
					   condition:''
					  }
					}] 
					},
					{
					 region:'west',
					 width:'25%',
					 xtype:'erpSpecialTreePanel',
					  layout:'anchor',
					}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});