/**
 * ERP项目groupgrid样式
 */
Ext.define('erp.view.core.grid.DocListGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpDocListGrid',
	region: 'south',
	layout : 'fit',
	id: 'grid', 
	FormUtil: Ext.create('erp.util.FormUtil'),
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
    	fields: [
//    	         {
//        	name:'group',
//        	type:'string'
//        }, 
        {
        	name:'title',
        	type:'string'
        },{
        	name:'creator',
        	type:'string'
        },{
        	name:'creator_id',
        	type:'int'
        },{
        	name:'date',
        	type:'string'
        },{
        	name:'url',
        	type:'string'
        },{
        	name:'attach_number',
            type:'int'
        },{
        	name:'status',
        	type:'int'
        },{
        	name:'number',
        	type:'int'
        }],
        sorters: [{
            property : 'date',
            direction: 'DESC'
        }],
//        groupField: 'group'
    }),
    iconCls: 'icon-grid',
    frame: true,
    bodyStyle:'background-color:#f1f1f1;',
    features: [Ext.create('Ext.grid.feature.Grouping',{
        groupHeaderTpl: '{name} ({rows.length} 封)'
    })],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	
    }),
    dockedItems: [{
    	id : 'paging',
        xtype: 'erpMailPaging',
        dock: 'bottom',
        displayInfo: true
	}],
    columns: [
//              {
//        text: '',
//        width: 80,
//        dataIndex: 'group'
//    },
    {
        text: '文档名',
        width: 150,
        dataIndex: 'dcl_title'
    },{
        text: '所有者',
        width: 100,
        dataIndex: 'dcl_creator'
    },{
        text: '所有者ID',
        width: 100,
        dataIndex: 'dcl_creator_id'
    },{
        text: '创建日期',
        width: 210,
        dataIndex: 'dcl_date'
    },{
    	text: '目录',
        width: 210,
        dataIndex: 'dcl_url'
    },{
        text: '附件数',
        width: 80,
        dataIndex: 'dcl_attach_number'
    },{
        text: '状态',
        width: 80,
        dataIndex: 'dcl_status',
        renderer: function(val, meta, record){
        	if(val==0) return '正常';
        	else if(val==1) return '草稿';
        	else if(val==-1) return '失效';
        }
    },{
        text: '文档编号',
        width: 80,
        dataIndex: 'dcl_number'
    }],
    tbar: [{
    	iconCls: 'group-delete',
		text: $I18N.common.button.erpDeleteButton,
		handler: function(btn){
			var selectItem = Ext.getCmp('grid').selModel.selected.items;
			if (selectItem.length == 0) {
				showError("请先选中要删除的文档");return;
			} else {
				Ext.each(selectItem, function(item){
//					var o = Ext.getCmp('grid').getDocumentListPower(item.data.dcl_id);
					if(item.data.dcl_creator_id == em_uu ||  Ext.getCmp('grid').getDocumentListPower(item.data.dcl_id).del == 1){
						Ext.Ajax.request({//拿到grid的columns
							url : basePath + 'oa/documentlist/delete.action',
							params: {
								id : item.data.dcl_id
							},
							method : 'post',
							async: false,
							callback : function(options, success, response){
								parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);return;
								}
								if(res.success){
									alert(item.data.dcl_title + ' 删除成功！');
								}
							}
						});											
					} else{
						showError('亲！你没有权限删除文档<'+item.data.dcl_title+'>哦！');return;
					}
			    });
				url = "oa/documentlist/myDocument.action";
				btn.ownerCt.ownerCt.getGroupData();
			}
		}
    },{
    	iconCls: 'group-post',
		text: "查看",
		handler: function(){
			console.log(Ext.getCmp('grid').selModel.selected);
			var selectItem = Ext.getCmp('grid').selModel.selected.items;
			var me = Ext.getCmp('grid');
			if (selectItem.length == 0) {
				showError("请先选中要查看的文档");return;
			} else if(selectItem.length == 1){
				var id = selectItem[0].data.dcl_id;
				if(id != null && id != ''){
//					var o = Ext.getCmp('grid').getDocumentListPower(id);
					if(selectItem[0].data.dcl_creator_id == em_uu ||  Ext.getCmp('grid').getDocumentListPower(selectItem[0].data.dcl_id).see == 1){
						var panel = Ext.getCmp("documentlist" + id); 
			        	var main = parent.Ext.getCmp("content-panel");
			        	if(!panel){ 
			        		var title = selectItem[0].data.dcl_title;		        		
			    	    	panel = { 
			    	    			title : title,
			    	    			tag : 'iframe',
			    	    			tabConfig:{tooltip: selectItem[0].data.dcl_title},
			    	    			frame : true,
			    	    			border : false,
			    	    			layout : 'fit',
			    	    			iconCls : 'x-tree-icon-tab-tab1',
			    	    			html : '<iframe id="iframe_documentlist_' + id + '" src="' + basePath + "jsps/oa/document/documentDetail.jsp?id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
			    	    			closable : true,
			    	    			listeners : {
			    	    				close : function(){
			    	    			    	main.setActiveTab(main.getActiveTab().id); 
			    	    				}
			    	    			} 
			    	    	};
			    	    	me.FormUtil.openTab(panel, "documentlist" + id); 
			        	}else{ 
			    	    	main.setActiveTab(panel); 
			        	}
					} else {
						showError("亲！你没有相关操作权限哦！");return;
					}		        	 
		    	}
			} else{
				showError("亲！只能选中一个文档哦");return;
			}
		}
    },{
    	iconCls: 'group-post',
		text: "编辑",
		handler: function(){
			console.log(Ext.getCmp('grid').selModel.selected);
			var selectItem = Ext.getCmp('grid').selModel.selected.items;
			var me = Ext.getCmp('grid');
			if (selectItem.length == 0) {
				showError("请先选中要编辑的文档");return;
			} else if(selectItem.length == 1){
				var id = selectItem[0].data.dcl_id;
				if(id != null && id != ''){
//					var o = Ext.getCmp('grid').getDocumentListPower(id);
					if(selectItem[0].data.dcl_creator_id == em_uu ||  Ext.getCmp('grid').getDocumentListPower(selectItem[0].data.dcl_id).edit == 1){
						var panel = Ext.getCmp("documentlistEdit" + id); 
			        	var main = parent.Ext.getCmp("content-panel");
			        	if(!panel){ 
			        		var title = selectItem[0].data.dcl_title;		        		
			    	    	panel = { 
			    	    			title : title,
			    	    			tag : 'iframe',
			    	    			tabConfig:{tooltip: selectItem[0].data.dcl_title},
			    	    			frame : true,
			    	    			border : false,
			    	    			layout : 'fit',
			    	    			iconCls : 'x-tree-icon-tab-tab1',
			    	    			html : '<iframe id="iframe_documentlist_edit_' + id + '" src="' + basePath + "jsps/oa/document/documentDetail2.jsp?id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
			    	    			closable : true,
			    	    			listeners : {
			    	    				close : function(){
			    	    			    	main.setActiveTab(main.getActiveTab().id); 
			    	    				}
			    	    			} 
			    	    	};
			    	    	me.FormUtil.openTab(panel, "documentlist_edit" + id); 
			        	}else{ 
			    	    	main.setActiveTab(panel); 
			        	}
					} else {
						showError("亲！你没有相关操作权限哦！");return;
					}
		        	 
		    	}
			} else{
				showError("亲！只能选中一个文档哦");return;
			}
		}
    },{
    	iconCls: 'group-all',
		text: "共享",
		handler: function(btn){
			var selectItem = Ext.getCmp('grid').selModel.selected.items;
			if (selectItem.length == 0) {
				showError("请先选中要编辑的文档");return;
			} else if(selectItem.length == 1){
				var id = selectItem[0].data.dcl_id;
				if (selectItem[0].data.dcl_status == 0) {
					if(id != null && id != ''){
//						var o = Ext.getCmp('grid').getDocumentListPower(id);
						if (selectItem[0].data.dcl_creator_id == em_uu ||  Ext.getCmp('grid').getDocumentListPower(selectItem[0].data.dcl_id).share == 1) {
							var title = selectItem[0].data.dcl_title;
							var win = new Ext.window.Window({
								id : 'win',
								title: "权限名称:" + title,
								height: "100%",
								width: "80%",
								maximizable : true,
								buttonAlign : 'center',
								layout : 'anchor',
								items: [{
									tag : 'iframe',
									frame : true,
									anchor : '100% 100%',
									layout : 'fit',
									html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/document/documentListPowerSet.jsp?dcl_id=' + id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
								}]
							});
							win.show();						
						} else {
							showError("亲！你没有相关操作权限哦！");return;
						}
					}					
				} else {
					showError("亲！只能共享'正常'状态的文档哦！");return;
				}
			} else{
					showError("亲！只能选中一个文档哦");return;
			}			
		}
    },{
    	iconCls: 'group-read',
		text: "日志",
		handler: function(btn){
			var selectItem = Ext.getCmp('grid').selModel.selected.items;
			if (selectItem.length == 0) {
				showError("请先选中要查看日志的文档");return;
			} else if(selectItem.length == 1){
//				var o = Ext.getCmp('grid').getDocumentListPower(id);
				if(selectItem[0].data.dcl_creator_id == em_uu ||  Ext.getCmp('grid').getDocumentListPower(selectItem[0].data.dcl_id).journal == 1){
					var number = selectItem[0].data.dcl_number;
					if(number != null && number != ''){
						url = "oa/documentlist/getJournal.action?number=" + number;
						btn.ownerCt.ownerCt.getGroupData();
					}					
				} else {
					showError("亲！你没有权限查看该文档日志哦！");return;
				}
			} else {
				showError("亲！只能选中一个文档哦");return;
			}
//			Ext.getCmp("return").setVisible(true);
		}
    },{
    	iconCls: 'group-close',
		text: $I18N.common.button.erpCloseButton,
		handler: function(){
			parent.Ext.getCmp("content-panel").getActiveTab().close();
		}
    },{
    	iconCls: 'group-close',
		text: '返回',
		id: 'return',
		handler: function(btn){
			var whoami = getUrlParam("whoami");
			if (whoami == 'myDoc') {
				url = "oa/documentlist/myDocument.action";
				btn.ownerCt.ownerCt.getGroupData();		
			} else if (whoami == 'list') {
				url = "oa/documentlist/listDocument.action";
				btn.ownerCt.ownerCt.getGroupData();	
			} else if (whoami == 'search') {
				url = "oa/documentlist/search.action?title=@";
				btn.ownerCt.ownerCt.getGroupData();		
			}
		}
    },{
    	xtype: 'textfield',
    	id: 'titlelike'
    },{
    	iconCls: 'group-close',
		text: '搜索',
		id: 'search',
		handler: function(btn){
			var title = Ext.getCmp('titlelike').value;
			if(title != '' && title != null){
				url = "oa/documentlist/search.action?title=" + Ext.getCmp('titlelike').value;
				btn.ownerCt.ownerCt.getGroupData();				
			} else {
				showError("请先在输入框输入文档名");return;
			}
		}
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		var whoami = getUrlParam("whoami");
		if (whoami == 'myDoc') {
			url = "oa/documentlist/myDocument.action";
			this.getGroupData(page, pageSize);			
		} else if (whoami == 'list') {
			url = "oa/documentlist/listDocument.action";
			this.getGroupData(page, pageSize);	
		} else if (whoami == 'search') {
			
		}
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getGroupData: function(page, pageSize){
		var me = this;
		if(!page){
			page = 1;
		}
		if(!pageSize){
			pageSize = 15;
		}
		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: {
        		page: page,
        		pageSize: pageSize
        	},
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.success){
        			return;
        		} else {
        			dataCount = res.count;
        			me.store.loadData(res.success);
        		}
        	}
        });
	},
	getDocumentListPower: function(id){
		var o = new Object();
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'hr/employee/getJobDocumentListPower.action',
        	params: {
        		dcl_id: id,
        		em_id: em_uu
        	},
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		console.log(response);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.success){
        			return;
        		} else {
//        			alert(o.DELETE);
        			o.see =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_see;
        			o.edit =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_edit;
        			o.share =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_share;
        			o.del =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_delete;
        			o.download =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_download;
        			o.journal =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_journal;
        		}
        	}
        });
		return o;
	}
});