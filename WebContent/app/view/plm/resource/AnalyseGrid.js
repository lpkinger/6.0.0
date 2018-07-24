Ext.define('erp.view.plm.resource.AnalyseGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.AnalyseGrid',
        id: 'analysegrid',
        RenderUtil:Ext.create('erp.util.RenderUtil'),
        flex: 0.60,

        store:store,
        columnLines:true,
        title:'resource Data',
        columns: columns,
        listeners: {
           'beforerender': function(grid,opt) {
            var me=this;
                   Ext.Array.each(columns, function(column) {   
                    if(column.renderer != null && column.renderer != ""){
                    	column.renderer = me.RenderUtil[column.renderer];
                    	}
                    })
              }
           }, 
      initComponent : function(){
		this.callParent(arguments);
	 },
});