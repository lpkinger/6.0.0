Ext.define('erp.view.ma.update.UpdateScheme',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'border', 
				items: [{
					region:'west',
					width:'25%',
					xtype: 'upstreepanel',
					caller:'UpdateScheme'
				},{
					region:'center',
					layout:'anchor',
					items:[{
						xtype: 'erpFormPanel',
						anchor: '100% 40%',
						saveUrl:'ma/update/saveUpdateScheme.action',
						deleteUrl:'ma/update/deleteUpdateScheme.action',
						updateUrl:'ma/update/updateUpdateScheme.action',
						getIdUrl: 'common/getId.action?seq=UPDATESCHEME_SEQ',
						dumpable: true,
						singlePage: true
					},{
						xtype: 'erpGridPanel2',
						 viewConfig: {
	  						   plugins: {
	  							   ptype: 'gridviewdragdrop',
  						   }	    					
	  					   },
						anchor: '100% 60%',		
						bbar:[]
					}]
				}]
			}] 
		}); 
		me.callParent(arguments);
	} 
});