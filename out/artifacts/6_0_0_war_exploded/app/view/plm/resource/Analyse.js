Ext.define('erp.view.plm.resource.Analyse',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
			    defaults: {
                split: true
                },
				layout: 'anchor', 
				items: [{
				     anchor: '100% 50%',
                      split: true,              
					 xtype: 'barChart',
					 title:'north'
				},{
				   anchor: '100% 50%',
				   region:'south',
                   split: true,
                   layout:'border',
                   items: [{
                 title:'<font color=green>员工综合信息</font>',
                 region: 'center',
                 xtype:'AnalyseGrid',
                 split:true
                 }, {
                  title: '<font color=green>查询</font>',
                  region: 'west',
                  width:'25%',
                  xtype:'AnalyseForm',
                  split: true,
				}]
			}] 
			}]
		});
		me.callParent(arguments); 
	}
});