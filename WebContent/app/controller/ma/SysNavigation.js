Ext.QuickTips.init();
Ext.define('erp.controller.ma.SysNavigation', {
    extend: 'Ext.app.Controller',
    views:[
    		'ma.SysNavigation','core.grid.SysTreeGrid'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSysTreeGrid': { 
    			spcexpandclick:me.handleSpExpandClick
    		} 
    	});
    },
    handleSpExpandClick:function(record){//新定义的
		var me=this;
		var treegrid = Ext.getCmp('treegrid');
		var selModel=treegrid.getSelectionModel();
		me.loadNode(selModel,record);
		treegrid.selModel.select(record);
		return false;
	},
	loadNode: function(selModel,record){
			var me = this;
			if ( record.data['sn_id']) { 
				if(record.isExpanded() && record.childNodes.length > 0){
					me.flag = true;
					record.collapse(true,false);
				} else {	
					if(record.childNodes.length == 0){
						Ext.Ajax.request({//拿到tree数据
							url : basePath + 'ma/lazyTree.action',
				        	params: {
				        		parentId: record.data['id']
				        	},
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.tree && res.tree.length>0){
									var tree = res.tree;
									Ext.each(tree, function(t){
										t.sn_id = t.id;
				        				t.sn_parentid = t.parentId;
				        				t.sn_displayname = t.text;
				        				t.sn_detno = t.detno;
				        				t.sn_isleaf = t.leaf;
				        				t.sn_using = t.using;
				        				t.sn_tabtitle = t.text;
				        				t.sn_url = t.url;
				        				t.dirty = false;
				        				t.sn_deleteable = t.deleteable;
				        				t.sn_showmode = t.showMode;
				        				t.sn_logic = t.data.sn_logic;
				        				t.sn_num=t.data.sn_num;
				        				t.sn_limit = t.data.sn_limit;
				        				t.sn_caller = t.data.sn_caller;	
				        				t.sn_addurl = t.data.sn_addurl;	
				        				t.sn_show=t.data.sn_show,
        								t.sn_standardDesc=t.data.sn_standardDesc;
				        				t.data = null;
									});
									me.flag=true;
									record.appendChild(tree);
									record.expand(false, true);//展开
									Ext.each(record.childNodes, function(){
										this.dirty = false;
									});
								} else if(res.exceptionInfo){
									showError(res.exceptionInfo);
								}
							}
						});
					} else {
						me.flag=true;			
					}
				}
			}
		},
    setParentNodes: function(tree, record, isExpand){
    	var grid = tree.ownerCt.down('grid'), data = new Array(), nodes = record.childNodes;
    	Ext.each(nodes, function(node){
    		data.push({
    			sn_id: node.get('id'),
    			sn_displayname: node.get('text'),
    			sn_detno: node.get('sn_detno')
    		});
    	});
    	grid.store.loadData(data);
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