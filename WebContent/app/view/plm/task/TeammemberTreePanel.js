Ext.define('erp.view.plm.task.TeammemberTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpTeammemberTreePanel', 
	 BaseUtil: Ext.create('erp.util.BaseUtil'),
	id: 'tree-panel', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '20%', 
	region: 'east',
	title: "<font color=#a1a1a1; size=2>项目成员</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = "项目成员";
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	layout : 'fit',
	id: 'teammembertree', 
 	emptyText : $I18N.common.grid.emptyText,
	rootVisible: false, 
	singleExpand: false,
	containerScroll : true, 
	collapsible : true, 
	autoScroll: false, 
	useArrows: true,
	select:null,
	bodyStyle:'background-color:#f1f1f1;',
	store: Ext.create('Ext.data.TreeStore', {
		root : {
	    	text: 'root',
	    	id: 'root',
			expanded: true,	
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
		this.getTreeRootNode(this);
		this.callParent(arguments); 
	},
	getTreeRootNode: function(panel){
		var id=condition.split('=')[1];
		/*Ext.Ajax.request({
        	url : basePath + 'plm/task/GetTeammember.action',
        	params:{
        	  id:id,
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
        			panel.store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });*/
		
		var me=panel;
		var id=condition.split('=')[1];
		Ext.Ajax.request({
        	url : basePath + 'plm/task/GetTeammember.action',
        	async: false,
        	params:{
          	  id:id,
          	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
        			 Ext.Array.each(tree, function(tr) {
                          tr.cls="x-tree-cls-node";
                      });                      
                	me.store.setRootNode({
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