Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.TaskTemplate', {
    extend: 'Ext.app.Controller',
    views:[
    		'plm.task.TaskTemplate','core.grid.TaskTreeGrid','core.grid.WbsColumn'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpTaskTreeGrid': { 
    			itemmousedown: function(selModel, record){
    				var treegrid = Ext.getCmp('treegrid');
    				treegrid.selModel.select(record);
    				me.loadNode(selModel, record);
    			},
    			itemdbclick: function(selModel, record){
    				var treegrid = Ext.getCmp('treegrid');
    				treegrid.selModel.select(record);
    				me.loadNode(selModel, record);
    			}
    		} 
    	});
    },
    loadNode: function(selModel, record){
    	var me = this;
    	if (!record.get('leaf')) { 
    		if(record.isExpanded() && record.childNodes.length > 0){
				record.collapse(true,true);
				me.flag = true;
			} else {
				if(record.childNodes.length == 0){
					var activeTab = me.getActiveTab();
					var condition=(formCondition=="")?"":formCondition.replace('pt_id','tt_ptid');
					activeTab.setLoading(true);
					Ext.Ajax.request({
			        	url : basePath + 'common/TaskTree.action',
			        	params: {
			        		parentId: record.data['tt_id'],
			        		condition:condition
			        	},
			        	callback : function(options,success,response){
			        		activeTab.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        		var tree=res.tree;
			                if(tree[0]){
			        			record.appendChild(tree);
			        			record.expand(false,true);
			        			Ext.each(record.childNodes, function(){
			        				this.dirty = false;
			        			});
			        			}
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);
				}
			}
    	}
    },
    getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
    	return tab;
	}
});