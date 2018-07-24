Ext.define('erp.view.plm.record.SpecialTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpSpecialTreePanel', 
	 BaseUtil: Ext.create('erp.util.BaseUtil'),
	id: 'tree-panel', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '20%', 
	region: 'east',
	title: "<font color=#a1a1a1; size=2>任务日报详情</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = "任务日报详情";
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
	var urlCondition = this.BaseUtil.getUrlParam('gridCondition');
		Ext.Ajax.request({
        	url : basePath + 'plm/record/GetRecordTree.action',
        	params:{
        	 condition:urlCondition,
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