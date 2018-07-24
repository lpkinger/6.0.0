Ext.define('erp.view.plm.test.Buganalyse',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [
				{
				   region:'west',
                   split: true,
                   width:'30%',
                   layout:'anchor',
                   items:[{
                   anchor:'100%  100%',
                   height:'100%',
                   xtype:'erpProjectTreePanel',
                   },/*{
                    xtype: 'datepicker',
                   anchor:'100%  35%',
                   height:'35%',
                   id:'picker',
                     //minDate: new Date(),
                  handler: function(picker, date) {
                   }
                   }*/]
				},
				{
				   layout:'anchor',
				    region:'center',
				    items:[{
				       style:'background:#CDCDB4',
				       bodyStyle: 'background:#CDCDB4;',
				       anchor:'100%  6%',
				       xtype:'AnalyseForm',
				    },				  
				    {
				    	title:'处理统计',	
				    	anchor:'100% 47%',
				        xtype:'erpAnalyseHandGridPanel',
				        id:'hand',
				        url:'plm/test/handgrid.action',
				        region:'south',
				    },{ 
					      title:'提出统计',	
					       anchor:'100% 47%',
	                       xtype:'erpAnalyseTestGridPanel',
	                       id:'test',
	                       url:'plm/test/testgrid.action',
	                       region:'center',                       
					    },]
				}
				]
			}] 
		});
		me.callParent(arguments); 
	}
});