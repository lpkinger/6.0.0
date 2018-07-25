/**
 * 企业联系人email-tree
 */
Ext.define('erp.view.oa.mail.TreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpMailTreePanel', 
	id: 'tree-panel', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '20%', 
	region: 'east',
	title: "<font color=#a1a1a1; size=2>联系人</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = "联系人";
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
		tooltip: '修改联系人设置',
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
	selModel: Ext.create('Ext.selection.CheckboxModel', {
		
	}),
	getTreeRootNode: function(parentId){
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'oa/mail/getEmployeeMail.action',
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
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	}
});