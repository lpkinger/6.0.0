Ext.QuickTips.init();
Ext.define('erp.controller.ma.DocumentPower', {
    extend: 'Ext.app.Controller',
    views:[
    		'ma.DocumentPower','core.grid.DocumentPowerTreeGrid','core.button.Distribute'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpDocumentPowerTreeGrid': { 
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
    		},
    		'erpDistributeButton': {
    			click: function(){
    				var treegrid = Ext.getCmp('treegrid');
    				var record = treegrid.selModel.getSelection()[0];
    				var id = record.data['dcp_id'];    		
    				var title = record.data['dcp_powername'];
        			var win = new Ext.window.Window({
        			    	id : 'win',
        			    	title: "权限名称:" + title,
        					height: "100%",
        					width: "80%",
        					maximizable : true,
        					buttonAlign : 'center',
        					layout : 'anchor',
        					items: [{
        					    tag : 'iframe',
        					    frame : true,
        					    anchor : '100% 100%',
        					    layout : 'fit',
        					    html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/ma/documentPowerSet.jsp?dcp_id=' + id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
        					}]
        			});
        			win.show();
    			}
    		} 
    	});
    },
    loadNode: function(selModel, record){
    	var me = this;
    	if (record.get('isempower') == 'F') {
			Ext.getCmp('distribute').disable(true);
		} else {
			Ext.getCmp('distribute').setDisabled(false);
		}
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
			        	url : basePath + 'common/singleGridPanel.action',
			        	params: {
			        		caller: caller,
			        		condition: "dcp_parentid=" + record.data['dcp_id']
			        	},
			        	callback : function(options,success,response){
			        		activeTab.setLoading(false);
			        		console.log(response);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.data){
			        			var tree = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
			        			Ext.each(tree, function(t){
			        				t.powername = t.dcp_powername;
			        				t.isempower = t.dcp_isempower;
			        				t.id = t.dcp_id;
			        				t.parentId = t.dcp_parentid;
			        				t.dirty = false;
			        				t.cls = 'x-tree-cls-parent';
			        				t.leaf = t.dcp_isleaf == 'T';
			        				if(t.leaf){
			        					t.cls = 'x-tree-cls-node';
			        				}
			        			});
			        			record.appendChild(tree);
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