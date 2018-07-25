Ext.define('erp.view.ma.SysCheckTree',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpSysCheckTreePanel', 
	 BaseUtil: Ext.create('erp.util.BaseUtil'),
	id: 'tree-panel', 
	margins : '0 0 -1 1', 
	autoScroll : true,  
	split: true, 
	layout:'fit',
	region: 'west',
	title: "<font color=#a1a1a1; size=2>员工</font>",
	tpl:new Ext.XTemplate('<tpl for="."><div style="height:100' + 'px;"><div id="' + this.id+ '"></div></div></tpl>'),
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = "员工列表";
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	rootVisible: false, 
	singleExpand: true,
	containerScroll : true, 
	collapsible : true, 
	autoScroll: true,
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
        	url : basePath + 'ma/SysCheck/getAllHrTree.action',
        	params:{
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