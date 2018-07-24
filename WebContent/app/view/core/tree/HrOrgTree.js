Ext.define('erp.view.core.tree.HrOrgTree',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.orgtreepanel', 
	id:'orgtreepanel',
	region:'west',
	width:'20%',
	frame:false,
	animCollapse: false,
	constrainHeader: true,
	border: false,
	autoShow: true,
	collapsible : true, 
	useArrows: true,
	title:'人事架构',
	rootVisible: false, 
	layout:'fit',
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		this.getNodes(0,this);
		this.callParent(arguments);
	},
	getNodes: function(pid,p){
		var me = this;
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'hr/getTreeNode.action',
			params: {
				parentId: pid
			},
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.result){
					var tree = res.result;
					Ext.getCmp('orgtreepanel').store.setRootNode({
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
	loadChild:function(record){
		var tree=this;
		tree.setLoading(true, tree.body);
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'hr/getTreeNode.action',
			params: {
				parentId: record.get('id')
			},
			callback : function(options,success,response){
				tree.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.result){
					record.appendChild(res.result);
					record.expand(false,true);
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