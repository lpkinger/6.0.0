	Ext.define('erp.view.common.main.NavigationToolbar',{ 
		extend: 'Ext.Toolbar', 
		alias: 'widget.erpNavigationToolbar',
		items: [{
			width:215,
	        xtype: 'triggerfield',
	        triggerCls: 'x-form-search-trigger',
	        fieldStyle: 'color:#777;background: #f1f2f5;border-color: #d9d9d9; -moz-border-radius:3px 0 0 3px; -webkit-border-radius:3px 0 0 3px; border-radius:3px 0 0 3px;',
		    focusCls: 'x-form-field-cir',
		    emptyText: $I18N.common.main.quickSearch,
	        enableKeyEvents : true,
	        listeners : {
	        	specialkey : function(field, e){
	        		if(e.getKey() == Ext.EventObject.ENTER){
	        			this.onTriggerClick();
	        		}
	        	}
	        },
	        id:'navigationSearch',
	        autoShow:true,
	        onTriggerClick: function(){
	        	var f=this,tree=Ext.getCmp('navigation-panel');
	        	if(f.value == '' || f.value == null){
	        		tree.getTreeRootNode(0);
					return;
	        	}
	        	tree.setLoading(true, tree.body);
	        	Ext.Ajax.request({//拿到tree数据
		        	url : basePath +'common/searchTree.action',
		        	timeout:120000,
		        	params: {
		        		search: f.value
		        	},
		        	callback : function(options,success,response){
		        		tree.setLoading(false);
		        		var res = new Ext.decode(response.responseText);
		        		if(res.tree){
		        			var root = tree.getRootNode();
		        			root.removeAll();
		        			var fn = function(node, ch) {
		        				for(var i in ch) {
			        				var n = ch[i], chs = n.children;
			        				n.children = [];
			        				n.expanded = true;
			        				var d = node.appendChild(n);
			        				if(d && chs && chs.length > 0 && String(chs) != '[]') {
			        					fn(d, chs);
			        				}
			        			}
		        			};
		        			fn(root, res.tree);
		        		} else if(res.redirectUrl){
		        			window.location.href = res.redirectUrl;
		        		} else if(res.exceptionInfo){
		        			Ext.Msg.alert("ERROR:" + res.exceptionInfo);
		        		}
		        	}
		        });
	        }
		}, '->',{
	        iconCls: 'tree-back',
	        cls: 'x-btn-tb',
	        width: 16,
	        tooltip: $I18N.common.main.treeBack,
	        hidden: false,
	    	handler: function(){
	    		Ext.getCmp('navigation-panel').getTreeRootNode(0);
	    		Ext.getCmp('navigationSearch').setValue(null);
	    	}
	    },'->'],
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});