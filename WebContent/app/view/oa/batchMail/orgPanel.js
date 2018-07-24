Ext.define('erp.view.oa.batchMail.orgPanel', {
	extend: 'Ext.tree.Panel', 
	alias: 'widget.orgSelectPanel',
	id: 'tree-panel', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '100%',
	height: '100%',
	expandedNodes: [],
	collapsible : true,
	rootVisible: false, 
	singleExpand: true,
	containerScroll : true,
	autoScroll: true,
    bodyStyle:'background-color:#f1f1f1;',
	initComponent: function(){
		var me = this;
		me.getTreeRootNode(0);
		me.callParent(arguments); 
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
	},
	getTreeRootNode: function(parentId){
		Ext.Ajax.request({
			url : basePath + 'hr/employee/getAllHrOrgsTree.action',
        	params: {
        		parentId: parentId
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			Ext.Array.each(res.tree, function(item){
        				if(item.leaf)
        					item.checked = false;
        			});
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
        	},
        	
        	
		});
	},

});