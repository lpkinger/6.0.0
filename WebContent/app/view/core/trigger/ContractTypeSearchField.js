Ext.define('erp.view.core.trigger.ContractTypeSearchField', {
			extend : 'Ext.form.field.Trigger',
			alias : 'widget.contracttypesearchfield',
			triggerCls: 'x-form-search-trigger',
			hasSearch : false,			
			paramName : 'query',
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
	        autoShow:true,
			initComponent : function() {
				this.callParent(arguments);
			},
			onTriggerClick: function(){
				var f = this, tree = Ext.getCmp('tree-panel')!=null?Ext.getCmp('tree-panel'):Ext.getCmp('powertree');
				if(f.value == '' || f.value == null){
					tree.getTreeRootNode(0);
					return;
				}
	            tree.setLoading(true, tree.body);
				Ext.Ajax.request({//拿到tree数据
		        	url :basePath +'oa/fee/searchContractTypeTree.action',
		        	timeout:120000,
		        	params: {
		        		search: f.value,
		        		isPower:Ext.getCmp('tree-panel')!=null?0:1
		        	},
		        	callback : function(options,success,response){
		        		tree.setLoading(false);
		        		var res = new Ext.decode(response.responseText);
		        		if(res.tree){
		        			var searchKeys = Ext.util.Cookies.get('searchKeys');
		        			if(searchKeys == null || searchKeys.length == 0){
		        				//searchKeys = new Array();//cookie存放数组很麻烦，就用字符串，中间用||隔开
		        				searchKeys = f.value;
		        			} else {
		        				var bool = true;
		        				Ext.Array.each(searchKeys.split('||'), function(k){
		        					if(k == f.value){//已存在
			        					bool = false;
		        					}
		        				});
		        				if(bool){
		        					searchKeys = searchKeys + "||" + f.value;
		        				}
		        			}
		        			//Ext.util.Cookies.set('searchKeys', searchKeys);//把用户的搜索条件作为关键字存入cookie
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
		});
