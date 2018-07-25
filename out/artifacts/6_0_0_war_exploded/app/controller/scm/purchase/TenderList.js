Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.TenderList', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['scm.purchase.TenderList','core.form.ReConDateField'],
	init:function(){
	    	this.control({ 
    		'#tenderlist': { 
    			afterrender:function(grid){
    				var main = parent.Ext.getCmp("content-panel");
					if(main){
						var panel = main.getActiveTab(); 
						if(panel){
							panel.on('activate',function(){
								grid.store.load();
							});
						}
					}
    			},
			   	itemclick: this.onGridItemClick
		   	},
    		'#search':{
    			specialkey : function(trigger, e) { 
                    if (e.getKey() == Ext.EventObject.ENTER) { 
                        var grid = trigger.ownerCt.ownerCt;
						grid.store.load();
                    } 
                } 
    		},
    		'#_status': {
    			change: function(field,newValue,oldValue){
    				var grid = field.ownerCt.ownerCt;
					grid.store.load();
    			}
    		},
    		'recondatefield': {
    			select:function(field,value){
    				var grid = field.ownerCt.ownerCt;
					grid.store.load();
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	var id = record.data['id'];
    	var code = record.data['code'];
    	var isPublish = record.data['isPublish'];
    	if(isPublish){
		   	var url = 'jsps/scm/purchase/tenderEstimate.jsp?formCondition=idIS'+id;
		   	var title = '评标单('+code+')';
		   	var call = 'TenderEstimate'+id;
	   	}else{
	   		var url = 'jsps/scm/purchase/tender.jsp?formCondition=idIS'+id;
	   		var title = '招标单('+code+')';
	   		var call = 'Tender'+id;
	   	}
    	this.FormUtil.onAdd(call, title, url);
   	}
});