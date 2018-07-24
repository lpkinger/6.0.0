Ext.QuickTips.init();
Ext.define('erp.controller.oa.flow.FlowList', {
	extend : 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	views : [ 'oa.flow.FlowList','oa.flow.FlowListTree','core.trigger.SearchField','core.button.Add','core.button.Save','core.button.Close','core.button.Update',
	          'core.button.Delete','core.button.DeleteDetail','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
	          'core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.grid.TfColumn','core.button.DbfindButton',
	          'core.button.ComboButton', 'core.form.YnField'],
	init:function(){
    	var me = this;
    	this.control({ 
    		'erpFlowListTree': { 
    			spcexpandclick:me.handleSpExpandClick
    		} 
    	});
    },
    handleSpExpandClick:function(record){//新定义的
		var me=this;
		var treegrid = Ext.getCmp('FlowListTree');
		var selModel=treegrid.getSelectionModel();
		me.loadNode(selModel,record);
		treegrid.selModel.select(record);
		return false;
	},
	loadNode: function(selModel,record){
			var me = this;
			if ( record.data['fd_id']) { 
				if(record.isExpanded() && record.childNodes.length > 0){
					me.flag = true;
					record.collapse(true,false);
				} else {	
					if(record.childNodes.length == 0){
						Ext.Ajax.request({//拿到tree数据
							url : basePath + 'oa/flow/getAllFlowTree.action',
				        	params: {
				        		parentId: record.data['id']
				        	},
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.tree && res.tree.length>0){
									var tree = res.tree;
									Ext.each(tree, function(t){
										t.fd_id = t.id;
						                t.fd_parentid = t.parentId;
						                t.fd_name = t.text;
						                t.fd_isleaf = t.leaf;
						                t.fd_detno = t.data.fd_detno;
						                t.fd_url = t.data.fd_url;
						                t.fd_caller = t.data.fd_caller;
						                t.fd_status = t.data.fd_status,
						                t.fd_remark = t.data.fd_remark,
						                t.fd_date = t.data.fd_date,
						                t.fd_shortname = t.data.fd_shortname,
						                t.fd_man = t.data.fd_man,
						                t.fd_fcid = t.data.fd_fcid,
						                t.fd_defaultduty = t.data.fd_defaultduty,
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