Ext.QuickTips.init();
Ext.define('erp.controller.ma.PowerSet', {
    extend: 'Ext.app.Controller',
    views:[
    		'ma.PowerSet','core.grid.GroupPower'
    	],
    init:function(){
    	this.control({ 
    		'grouppower': {
    			
    		}
    	});
    }
});