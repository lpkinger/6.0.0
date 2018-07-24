Ext.QuickTips.init();
Ext.define('erp.controller.oa.info.PagingReceive', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.info.Viewportr','common.datalist.GridPanel','common.datalist.Toolbar','oa.info.Formr',
    		'core.trigger.DbfindTrigger','core.form.ConDateField'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    			afterrender: function(grid){
    				grid.onGridItemClick = function(){//改为点击button进入详细界面
    					me.onGridItemClick(grid.selModel.lastSelected);
    				};
    			}
    		},
    		'button[id=unread]': {
    			click: function(){
    				var grid = Ext.getCmp('grid');
    				var condition = 'prd_recipientid=' + em_uu + ' AND prd_status=-1';
    				grid.getCount('PagingRelease', condition);
    				grid.filterCondition = 'prd_recipientid=' + em_uu + ' AND prd_status=-1';
    			}
    		},
    		'button[id=read]': {
    			click: function(){
    				var grid = Ext.getCmp('grid');
    				var condition = 'prd_recipientid=' + em_uu + ' AND prd_status=1';
    				grid.getCount('PagingRelease', condition);
    				grid.filterCondition = 'prd_recipientid=' + em_uu + ' AND prd_status=1';
    			}
    		},
    		'button[id=all]': {
    			click: function(){
    				var grid = Ext.getCmp('grid');
    				var condition = 'prd_recipientid=' + em_uu;
    				grid.getCount('PagingRelease', condition);
    				grid.filterCondition = 'prd_recipientid=' + em_uu;
    			}
    		},
    		'button[id=relay]': {//转发
    			click: function(){
    				var grid = Ext.getCmp('grid'),
    					items = grid.selModel.getSelection();
    				if(items.length > 0)
    					me.FormUtil.onAdd('info', '转发寻呼', 'jsps/oa/info/pagingRelease.jsp?pr_id=' + 
    							items[0].get('pr_id'));
    			}
    		},
    		'button[id=draft]': {
    			click: function(btn){
    				if(Ext.getCmp('prd_status').value != 0){//修改状态为保留
    					me.updateStatus(Ext.getCmp('prd_id').value, 0);
    					grid.filterCondition = 'prd_recipientid=' + em_uu + ' AND prd_status=0';
    				}
    			}
    		},
    		'button[id=vastdelete]': {
    			click: function(){
    				me.vastDelete();
    			}
    		}
    	});
    },
    onGridItemClick: function(record){
    	var box = parent.Ext.create('erp.view.core.window.DialogBox', {
    		other: record.data['pr_releaser'],
    		otherId: record.data['pr_releaserid']
    	});
    	box.insertDialogItem(record.data['pr_releaser'], Ext.Date.format(record.data['pr_date'], 'Y-m-d H:i:s'), 
    			record.data['pr_context']);
    }, 
    getCurrentStore: function(value){
    	var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var array = new Array();
		var o = null;
		Ext.each(items, function(item, index){
			o = new Object();
			o.selected = false;
			if(index == 0){
				o.prev = null;
			} else {
				o.prev = items[index-1].data[keyField];
			}
			if(index == items.length - 1){
				o.next = null;
			} else {
				o.next = items[index+1].data[keyField];
			}
			var v = item.data[keyField];
			o.value = v;
			if(v == value)
				o.selected = true;
			array.push(o);
		});
		return array;
    },
    vastDelete: function(){
    	var grid = Ext.getCmp('grid');
		var records = grid.selModel.getSelection();
		if(records.length > 0){
			var id = new Array();
			Ext.each(records, function(record, index){
				id[index] = record.data[keyField];
			});
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'common/vastDelete.action',
		   		params: {
		   			caller: caller,
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "删除成功!", function(){
		   					window.location.href = window.location.href;
		   				});
		   			}
		   		}
			});
		}
    }
});