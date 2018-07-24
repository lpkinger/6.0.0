Ext.define('erp.view.oa.knowledge.KnowledgeTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpKnowledgeTreePanel', 
	 BaseUtil: Ext.create('erp.util.BaseUtil'),
	id: 'KnowledgeTree', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '30%', 
	region: 'east',
	title: "<font color=#a1a1a1; size=3;weight= bold;>知 识 模 块</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else{
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
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
			expanded: true
		}
	}),
	initComponent : function(){ 
		this.getTreeRootNode();
		this.callParent(arguments); 
	},
	getTreeRootNode: function(){
		Ext.Ajax.request({
        	url : basePath + 'oa/knowledge/getKnowledgeModule.action',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
                	Ext.getCmp('KnowledgeTree').store.setRootNode({
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