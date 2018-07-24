Ext.define('erp.controller.as.port.NewBackList', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'as.port.NewBackList'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    init:function(){
    	var me = this;
    	this.control({
    		'#grid': {
    			beforeheaderfiltersapply: function(grid, filters) {
    				if(!grid.filterRendered) {
    					grid.filterRendered = true;
    					return false;
    				}
    				var validFilters = {}, keys = Ext.Object.getKeys(filters);
    				Ext.Array.each(keys, function(key){
    					if(!Ext.isEmpty(filters[key]))
    						validFilters[key] = filters[key];
    				});
    				grid.getStore().load({
    					params: {
    						filters: Ext.encode(validFilters)
    					}
    				});
    				return false;
    			}
    		}
    	});
    }
});