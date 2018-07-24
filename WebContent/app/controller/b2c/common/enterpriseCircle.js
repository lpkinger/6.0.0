Ext.QuickTips.init();
Ext.define('erp.controller.b2c.common.enterpriseCircle', {
	extend : 'Ext.app.Controller',
    init:function(){
		var me = this;
		this.control({
			'#enterpriseListGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#myPartnerGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#myPartnerGrid1': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#vendorGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#customerGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#invitationGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#serviceGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#noPlatformGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'#nocustomerGrid': {
    			afterrender:function(grid){
    				grid.store.load();
    			}
    		},
    		'textfield':{
				specialkey : function(field, e){
	        		if(e.getKey() == Ext.EventObject.ENTER){
	        			var btn = field.nextNode('button');
	        			btn.fireEvent('click',btn);
	        			//me.searchEnterprise();
	        		}
	        	}
    		},
		});	
		me.callParent(arguments);		
    }
});