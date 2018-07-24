Ext.define('erp.view.common.sysinit.SysPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.syspanel', 
	id:'syspanel',
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: true,
	layout: 'accordion',
	border: false,
	autoShow: true,
	collapsible : true, 
	title:'初始化导航',
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		this.getNodes(0,this);
		this.callParent(arguments);
	},
	getNodes: function(pid,p){
		var me = this;
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'system/initTree.action',
        	params: {
        		pid: pid
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			Ext.each(res.tree, function(t){
        				p.add({
        					title: t.in_desc,
        					autoScroll: true,
        					caller: t.in_caller,
        					id: t.in_id,
        					listeners: {
        						expand: function(n){
        							if(n.items.items.length == 0){
        								me.getTreeNode(n);
        							}
        						}
        					}
        				});
        			});
        			var n = p.items.items[0];
					if(n && n.items.items.length == 0) {
						me.getTreeNode(n);
					}
        		}else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	getTreeNode: function(node){
		var me = this;
		Ext.Ajax.request({// 拿到tree数据
        	url : basePath + 'system/initTree.action',
        	params: {
        		pid: node.id
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = me.parseTree(res.tree);
        			node.add({
        				xtype: 'treepanel',
        				height: '100%',
        				rootVisible: false, 
        				containerScroll : true, 
        				autoScroll: false, 
        				border:false,
        				store: Ext.create('Ext.data.TreeStore', {
        					root: {
        						text: 'root',
	                    	    id: 'root',
	                    		expanded: true,
	                    		children: tree
        					}
        				}),
        				listeners: {
            				itemmousedown: function(selModel, record){
            					//Ext.getCmp('template').setTitle(me.getPathString(selModel.ownerCt, record));
            					//me.getNode(record);
            					me.OpenUrl(record);
            					me.currentRecord=record;
            				}
            			}
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
	},
	parseTree: function(arr){
		var tree = new Array(),t;
		Ext.each(arr, function(r){
			t = new Object();
			t.id = r.in_id;
			t.text = r.in_desc;
			t.qtip = r.in_desc;
			t.caller = r.in_caller;
			t.img = r.in_img;
			t.parentId = r.in_pid;
			t.leaf = r.in_leaf == 1;
			t.url =r.in_url;
			t.description=r.in_description;
			tree.push(t);
		});
		return tree;
	},
	OpenUrl:function(record){
	var me = this,id=record.get('id');
	var panel = Ext.getCmp(id),main = Ext.getCmp("content-panel"); 
	if(!panel){ 
		var url = me.parseUrl(record.raw['url']);//解析url里的特殊描述
    	panel = { 
    			title : record.get('qtip').length>5?(record.get('qtip').substring(0,5)+'..'):record.get('qtip'),
    			tag : 'iframe',
    			tabConfig: {tooltip:record.get('qtip')},
    			border : false,
    			frame: false,
    			layout : 'fit',
    			iconCls : record.data.iconCls,
    			html : '<iframe id="iframe_' + id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    					var main = Ext.getCmp("content-panel"); 
    			    	main.setActiveTab(Ext.getCmp("HomePage")); 
    				}
    			} 
    	};
    	this.openTab(panel, record.get('id'), url); 
	} else{
    	main.setActiveTab(panel); 
	}
	},
    parseUrl: function(url){
    	var id = url.substring(url.lastIndexOf('?')+1);//将作为新tab的id
		if (id == null) {
			id = url.substring(0,url.lastIndexOf('.'));
		}
		if(contains(url, 'session:em_uu', true)){//对url中session值的处理
			url = url.replace(/session:em_uu/g,em_uu);
		}
		if(contains(url, 'session:em_code', true)){//对url中em_code值的处理
			url = url.replace(/session:em_code/g, "'" + em_code + "'");
		}
		if(contains(url, 'sysdate', true)){//对url中系统时间sysdate的处理
			url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
		}
		if(contains(url, 'session:em_name', true)){
			url = url.replace(/session:em_name/g,"'"+em_name+"'" );
		}
		return url;
    },
    openTab : function (panel, id, url){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = Ext.getCmp("content-panel"); 
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p);
    	} 
    }
});