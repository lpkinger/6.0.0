Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.Category', {
    extend: 'Ext.app.Controller',
    views:[
    		'fa.gla.Category','core.grid.SysTreeGrid'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSysTreeGrid': { 
    			itemmousedown: function(selModel, record){
    				var treegrid = Ext.getCmp('treegrid');
    				treegrid.selModel.select(record);
    				me.loadNode(selModel, record);
    			},
    			itemclick: function(selModel, record){
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
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
				me.flag = true;
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
					var activeTab = me.getActiveTab();
					activeTab.setLoading(true);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'common/lazyTree.action',
			        	params: {
			        		parentId: record.data['id']
			        	},
			        	callback : function(options,success,response){
			        		activeTab.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			var tree = res.tree;
			        			Ext.each(tree, function(t){
			        				t.sn_id = t.id;
			        				t.sn_parentid = t.parentId;
			        				t.sn_displayname = t.text;
			        				t.sn_isleaf = t.leaf ? 'T' : 'F';
			        				t.sn_tabtitle = t.text;
			        				t.sn_url = t.url;
			        				t.dirty = false;
			        				t.sn_deleteable = t.deleteable ? 'T' : 'F';
			        			});
			        			record.appendChild(res.tree);
			        			record.expand(false,true);//展开
			        			Ext.each(record.childNodes, function(){
			        				this.dirty = false;
			        			});
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);//展开
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