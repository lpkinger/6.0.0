Ext.define('erp.view.ma.update.UpdateSchemeTree',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.upstreepanel', 
	id:'upstreepanel',
	region:'west',
	//width:'20%',
	frame:false,
	animCollapse: false,
	constrainHeader: true,
	border: false,
	autoShow: true,
	collapsible : true, 
	useArrows: true,
	title:'更新方案',
	rootVisible: false, 
	layout:'fit',
	tbar:[{xtype:'toolbar',
		   width:'100%',
		   items:[{
	           xtype: 'trigger', 
	           width:180,
	           triggerCls: 'x-form-search-trigger',
	           enableKeyEvents : true,
		       listeners : {
		        	specialkey : function(field, e){
		        		if(e.getKey() == Ext.EventObject.ENTER){
		        			this.onTriggerClick();
		        		}
		        	}
		       },
		       onTriggerClick: function(){
		           var f = this;
		           var tree=Ext.getCmp('upstreepanel');
					if(f.value == '' || f.value == null){
						tree.getNodes();
						return;
					}
					tree.getNodes("title_ like ('%"+f.value+"%')");
		       }
	       }, '->','->', {
	        iconCls: 'tree-back',
	        cls: 'x-btn-tb',
	        width: 16,
	        tooltip: $I18N.common.main.treeBack,
	        hidden: false,
	    	handler: function(){
	    		var tree=Ext.getCmp('upstreepanel');
	    		tree.getNodes();
	    	}
	    },'->']
     }],
	bodyStyle:'background-color#f1f1f1;',
	initComponent : function(){ 
		this.getNodes();
		this.callParent(arguments);
	},
	getNodes: function(condition){
		var me = this;
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'ma/getTreeNode.action',
			params: {
				condition:condition
			},
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.result){
					var tree = res.result;
					Ext.getCmp('upstreepanel').store.setRootNode({
						text: 'root',
						id: 'root',
						expanded: true,
						children: tree
					});
				}else if(res.exceptionInfo){
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