Ext.define('erp.view.common.init.Tree',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.inittree', 
	id: 'inittree', 
	width : '20%',
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	title: '初始化项目',
	rootVisible: false, 
	containerScroll : true, 
	collapsible : true, 
	autoScroll: false, 
	useArrows: true,
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		this.getTreeRootNode(0);
		this.callParent(arguments);
	},
	getTreeRootNode: function(pid){
		var me = this;
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'system/initTree.action',
        	params: {
        		pid: pid
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = me.parseTree(res.tree);
        			if(pid == 0){
        				Ext.getCmp('inittree').store.setRootNode({
                    		text: 'root',
                    	    id: 'root',
                    		expanded: true,
                    		children: tree
                    	});
        			} else {
        				var record = me.selModel.lastSelected;
        				record.appendChild(tree);
        				record.expand(false, true);
        			}
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
	},
	parseTree: function(arr){
		var tree = new Array(),t;
		Ext.each(arr, function(r){
			t = new Object();
			t.id = r.in_id;
			t.text = r.in_desc;
			t.caller = r.in_caller;
			t.img = r.in_img;
			t.parentId = r.in_pid;
			t.leaf = r.in_leaf == 1;
			tree.push(t);
		});
		return tree;
	}
});