Ext.define('erp.view.plm.project.Loganalyse',{ 
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
                   width:'20%',
                   layout:'anchor',
                   items:[{
                   anchor:'100%  65%',
                   xtype:'erpProjectTreePanel',
                   },{
                    xtype: 'datepicker',
                   anchor:'100%  35%',
                   height:'35%',
                   id:'picker',
                     //minDate: new Date(),
                  handler: function(picker, date) {
                   }
                   }]
				},
				{
				     
				    layout:'anchor',
				    region:'center',
				    items:[
				    {
				       
				       style:'background:#CDCDB4',
				       bodyStyle: 'background:#CDCDB4;',
				       anchor:'100%  7%',
				       xtype:'AnalyseForm',
				       
				    },
				    {
				       anchor:'100% 93%',
                       xtype:'erpLogGridPanel',
				    },]
				}
				]
			}] 
		});
		me.callParent(arguments); 
	}
});