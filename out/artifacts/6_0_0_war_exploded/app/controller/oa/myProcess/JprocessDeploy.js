Ext.QuickTips.init();
Ext.define('erp.controller.oa.myProcess.JprocessDeploy',{
    extend: 'Ext.app.Controller',
    views:[
    		'oa.myProcess.jprocessDeploy.JprocessDeploy','oa.myProcess.jprocessDeploy.JCTreeGrid'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpJCTreeGrid': { 
    			itemmousedown: function(selModel, record){
    				var treegrid = Ext.getCmp('treegrid');
//    				alert(11);
    				treegrid.selModel.select(record);
    				me.loadNode(selModel, record);
//    				console.log(record);
//    				alert(record.data.jd_formUrl);
    				if(record.data.jd_formUrl != null && record.data.jd_formUrl != ''){
    					this.onGridItemClick(selModel, record);    					
    				}
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
			        	url : basePath + 'common/getLazyJProcessDeploy.action',
			        	params: {
			        		parentId: record.data['id']
			        	},
			        	callback : function(options,success,response){
			        		activeTab.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			var tree = res.tree;
			        			Ext.each(tree, function(t){
			        				t.jd_selfId = t.id;
			        				t.jd_parentId = t.parentId;
			        				t.jd_classifiedName = t.text;
			        			//	t.sn_isleaf = t.leaf;
			        				t.jd_caller = t.creator;
			        				t.jd_formUrl = t.url;
			        				t.jd_processDefinitionId = t.qtitle;
			        				t.jd_enabled = t.using;
			        				t.jd_processDefinitionName = t.version;
			        				t.dirty = false;
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
    onGridItemClick: function(selModel, record){//grid行选择
//    	console.log(record);
    	var win = new Ext.window.Window({
			id : 'win',
			title: '来自' + em_name + '的' + record.data.jd_processDefinitionName,
			height: "90%",
			width: "85%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + record.data.jd_id + '" src="' + basePath + record.data.jd_formUrl+'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
		win.show();
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