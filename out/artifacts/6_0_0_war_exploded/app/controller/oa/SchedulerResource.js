Ext.QuickTips.init();
Ext.define('erp.controller.oa.SchedulerResource', {
    extend: 'Ext.app.Controller',
    views:[
     		'oa.SchedulerResource'
     	 ],
    requires: ['erp.util.BaseUtil'],
    init:function(){
    	this.control({ 
    		'schedulerpanel': { 
    			select: this.onGridItemClick 
    		} 
    	});
    }, 
    onGridItemClick: function(selModel, record){
    	var keyValues = record.data;
    	var fields=trigger.setFields;
    	var ff;
		Ext.Array.each(record.fields.keys,function(k){
			Ext.Array.each(fields,function(ds){				
				if(k == ds.mappingfield) {
					console.log(k);
					if(ds.field && parent.Ext.getCmp(ds.field)){
						if(trigger.name == ds.field || trigger.id == ds.field){
							triggerV = keyValues[k];
						}
						ff = parent.Ext.getCmp(ds.field);
						 ff.setValue(keyValues[k]);
					}
				}
			});
		});
		parent.Ext.getCmp('dbwin').close();
    }
});