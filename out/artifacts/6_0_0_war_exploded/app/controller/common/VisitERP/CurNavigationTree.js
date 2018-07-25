Ext.QuickTips.init();
Ext.define('erp.controller.common.VisitERP.CurNavigationTree', {
	extend : 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	views : [ 'common.VisitERP.CurNavigationTree','common.VisitERP.CNTreePanel','core.trigger.SearchField','core.button.Add','core.button.Save','core.button.Close','core.button.Update',
	          'core.button.Delete','core.button.DeleteDetail','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
	          'core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.grid.TfColumn','core.button.DbfindButton',
	          'core.button.ComboButton', 'core.form.YnField'],
	init:function(){
    	var me = this;
    	this.control({ 
    		'CNTreePanel': { 
    			spcexpandclick:me.handleSpExpandClick
    		} 
    	});
    },
    handleSpExpandClick:function(record){//新定义的
		var me=this;
		var treegrid = Ext.getCmp('CNTreePanel');
		var selModel=treegrid.getSelectionModel();
		me.loadNode(selModel,record);
		treegrid.selModel.select(record);
		return false;
	},
	loadNode: function(selModel,record){
			var me = this;
			if ( record.data['cn_id']) { 
				if(record.isExpanded() && record.childNodes.length > 0){
					me.flag = true;
					record.collapse(true,false);
				} else {	
					if(record.childNodes.length == 0){
						Ext.Ajax.request({//拿到tree数据
							url : basePath + 'common/VisitERP/getCNTree.action',
				        	params: {
				        		parentId: record.data['id']
				        	},
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.tree && res.tree.length>0){
									var tree = res.tree;
									Ext.each(tree, function(t){
										 t.cn_id = t.id;
				                         t.cn_subof = t.parentId;
				                         t.cn_isleaf = t.leaf;
				                         t.cn_detno = t.detno;
				                         t.cn_title = t.data.cn_title,
				                         t.cn_icon = t.data.cn_icon,
				                         t.cn_url = t.data.cn_url,
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