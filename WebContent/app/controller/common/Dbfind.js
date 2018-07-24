Ext.QuickTips.init();
Ext.define('erp.controller.common.Dbfind', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.dbfind.GridPanel',
     		'common.dbfind.Toolbar',
     		'common.dbfind.Viewport','core.grid.YnColumn'
     	],
    requires: ['erp.util.BaseUtil'],
    init:function(){
    	this.control({ 
    		'erpDbfindGridPanel': { 
    			itemclick: this.onGridItemClick 
    		} 
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	var keyValues = record.data;
    	var triggerV = null;
    	if(!trigger.hidden) {
        	if(!trigger.ownerCt || trigger.column){//如果是grid的dbfind
        		var grid = trigger.owner;
        		//grid前面有checkboxModel 的  时候   grid的一行得到焦点的时候会把这一行的record放入grid 中的lastSelected 
        		//为了解决selected.items[0]保存的是checkboxModel中选择列的情况
        		var select = grid.lastSelectedRecord || trigger.record || grid.getSelectionModel().selected.items[0] 
        				|| grid.selModel.lastSelected;//selected的数据
        		Ext.Array.each(record.fields.keys, function(k){
            		Ext.Array.each(grid.dbfinds, function(ds){
            			if(Ext.isEmpty(ds.trigger) || ds.trigger == trigger.name) {
            				if(Ext.Array.contains(ds.dbGridField.split(';'), k)) {//k == ds.dbGridField//支持多dbgrid的字段对应grid同一字段
                    			if(ds.field == key){
                    				triggerV = keyValues[k];//trigger所在位置赋值
                    			}
                    			select.set(ds.field, keyValues[k]);
                    		}
            			}
                	});
            	});
        	} else {
        		var ff;
    			Ext.Array.each(record.fields.keys,function(k){
    				Ext.Array.each(dbfinds,function(ds){
    					if(k == ds.dbGridField) {
    						if(ds.field && ds.field.indexOf(";")>0){
    							Ext.Array.each(ds.field.split(";"),function(s){
    								if(trigger.name == s || trigger.id == s){
    									triggerV = keyValues[k];
    								}
    								ff = parent.Ext.getCmp(s);
    								if(ff) ff.setValue(keyValues[k]);
    							});
    						}
    						else if(ds.field && parent.Ext.getCmp(ds.field)){
    							if(trigger.name == ds.field || trigger.id == ds.field){
    								triggerV = keyValues[k];
    							}
    							ff = parent.Ext.getCmp(ds.field);
    							//有时候会拿不到dbkind
    							var dbtype=getUrlParam('dbkind');
    							if(dbtype!=null&&dbtype=='add'&&ff.value!=null&&ff.value!="") {
    								//判断当前值是否存在于已选择里面
    								var arr=ff.value.split("#");
    								if(arr.indexOf(keyValues[k])<0) 
    									keyValues[k]=ff.value+"#"+keyValues[k];
    								else  
    										keyValues[k]=ff.value;
    								if(trigger.name == ds.field || trigger.id == ds.field){
        								triggerV = keyValues[k];
        							}
    							}
    							 ff.setValue(keyValues[k]);
    						}
    					}
    				});
    			});
        	}
    	}
    	trigger.setValue(triggerV);
    	trigger.lastTriggerValue=triggerV;
    	trigger.fireEvent('aftertrigger', trigger, record, dbfinds);
    	var win = parent.Ext.getCmp('dbwin');
    	win && win.close();
    }
});