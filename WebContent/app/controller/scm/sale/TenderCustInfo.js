Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.TenderCustInfo', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:['scm.sale.TenderCustInfo','core.form.ReConDateField'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'#TenderCustInfo': { 
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
   		var url = 'jsps/scm/sale/tenderSubmission.jsp?formCondition=idEQ'+id;
   		var title = '投标单('+code+')';
   		var call = 'TenderSubmission'+id;
    	this.FormUtil.onAdd(call, title, url);
   	}
});