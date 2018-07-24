Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.TenderInfo', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:['scm.sale.TenderInfo','core.form.ReConDateField'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'#tenderInfo': { 
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
	   	var url = 'jsps/scm/sale/tenderPublic.jsp?id='+id;
	   	var title = '公开招标单('+code+')';
	   	var call = 'TenderPublic'+id;
    	this.FormUtil.onAdd(call, title, url);
   	}
});