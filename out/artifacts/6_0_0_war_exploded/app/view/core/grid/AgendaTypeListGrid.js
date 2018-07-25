/**
 * ERP项目groupgrid样式
 */
Ext.define('erp.view.core.grid.AgendaTypeListGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpAgendaTypeListGrid',
	region: 'south',
	layout : 'fit',
	id: 'grid', 
	FormUtil: Ext.create('erp.util.FormUtil'),
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name:'name',
        	type:'string'
        },{
        	name:'color',
        	type:'string'
        }],
//        sorters: [{
//            property : 'date',
//            direction: 'DESC'
//        }],
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
    columns: [{
        text: '类型名称',
        width: 150,
        dataIndex: 'at_name'
    },{
        text: '类型颜色',
        width: 150,
        dataIndex: 'at_color',
        renderer: function(val, meta, record){
        	return '<div style="background:#' + val + '">&nbsp;</div>';
        }
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
					Ext.Ajax.request({//拿到grid的columns
						url : basePath + 'oa/persontask/myAgenda/delete.action',
						params: {
							id : item.data.at_id
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
								alert(item.data.at_name + ' 删除成功！');
							}
						}
					});											
				});
				url = "oa/persontask/myAgenda/typelist.action";
				btn.ownerCt.ownerCt.getGroupData();
			}
		}
    },'-',{
    	iconCls: 'x-button-icon-add',
		text: '添加',
		handler: function(){
			var win = new Ext.window.Window({
				id : 'win',
				title: "添加日程类型",
				height: "180px",
				width: "60%",
				maximizable : false,
				buttonAlign : 'center',
				layout : 'anchor',
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/persontask/myAgenda/addType.jsp" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
				}]
			});
			win.show();		
		}
    },'-',{
    	iconCls: 'group-close',
		text: $I18N.common.button.erpCloseButton,
		handler: function(){
			parent.Ext.getCmp("content-panel").getActiveTab().close();
		}
    },'->',{
    	xtype: 'textfield',
    	fieldLabel: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b><font size="3">类型名称</font></b>',
    	id: 'titlelike'
    },{
    	iconCls: 'x-button-icon-scan',
    	text: '查询',
		id: 'search',
		handler: function(btn){
			var name = Ext.getCmp('titlelike').value;
			if(name != '' && name != null){
				url = "oa/persontask/myAgenda/search.action?name=" + Ext.getCmp('titlelike').value;
				btn.ownerCt.ownerCt.getGroupData();				
			} else {
				showError("请先在输入框输入类型名称");return;
			}
		}
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		url = "oa/persontask/myAgenda/typelist.action";
		this.getGroupData(page, pageSize);
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
//        		console.log(response);
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
	}
});