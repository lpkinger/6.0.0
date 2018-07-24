Ext.define('erp.view.plm.project.ProjectTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpProjectTreePanel', 
	 BaseUtil: Ext.create('erp.util.BaseUtil'),
	id: 'tree-panel', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	layout:'fit',
	height:'65%',
	region: 'west',
	title: "<font color=#a1a1a1; size=2>项目</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = "项目列表";
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	rootVisible: false, 
	singleExpand: true,
	containerScroll : true, 
	collapsible : true, 
	autoScroll: false, 
	useArrows: true,
	expanded: true,
	bodyStyle:'background-color:#f1f1f1;',
	store: Ext.create('Ext.data.TreeStore', {
		root : {
	    	text: 'root',
	    	id: 'root',
			expanded: true
		}
	}),
	tools: [{
		id: 'gear',
		type: 'gear',
		tooltip: '修改设置',
		handler: function(){
			
		}
	} , {
		id: 'refresh',
		type: 'refresh',
		tooltip: '刷新',
		handler: function(){
			
		}
	} , {
		id: 'search',
		type: 'search',
		tooltip: '查找',
		handler: function(){
			
		}
	}],
	initComponent : function(){ 
		this.getTreeRootNode();
		this.callParent(arguments); 
	},
	getTreeRootNode: function(parentId){
		Ext.Ajax.request({
        	url : basePath + 'plm/projectplan/GetProjectPlan.action',
        	params:{
        	  condition:condition
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
                	Ext.getCmp('tree-panel').store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	}
});