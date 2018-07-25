Ext.define('erp.view.oa.info.Forms',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpPagingFormPanels',
	id: 'forms', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	tbar: [{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var grid = Ext.getCmp('grid');
			var condition = 'pr_releaserid=' + em_uu;
			if(Ext.getCmp('prd_recipientid').value != null && Ext.getCmp('prd_recipientid').value != ''){
				condition += " AND prd_recipientid=" + Ext.getCmp('prd_recipientid').value;
			}
			if(Ext.getCmp('pr_date').value != null && Ext.getCmp('pr_date').value != ''){
				if(condition == ''){
					condition += " (pr_date " + Ext.getCmp('pr_date').value + ")";
				} else {
					condition += " AND (pr_date " + Ext.getCmp('pr_date').value + ")";
				}
			}
			if(Ext.getCmp('pr_context').value != null && Ext.getCmp('pr_context').value != ''){
				if(condition == ''){
					condition += " pr_context like'%" + Ext.getCmp('pr_context').value + "%'";
				} else {
					condition += " AND pr_context like'%" + Ext.getCmp('pr_context').value + "%'";
				}
			}
			if(condition != ''){
				grid.getCount('PagingRelease', condition);
			} else {
				showError('请填写筛选条件');return;
			}
    	}
	}, '-', {
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}, '->', {
		id: 'vastdelete',
    	iconCls: 'group-delete',
    	cls: 'x-btn-gray',
		text: $I18N.common.button.erpDeleteButton,
		handler: function(){
			
		}
    },/*'-',{
    	iconCls: 'group-read',
    	cls: 'x-btn-gray',
		text: "重发",
		handler: function(){
			
		}
    },'-',{
    	iconCls: 'group-post',
    	cls: 'x-btn-gray',
		text: "转发",
		handler: function(){
			
		}
    },*/'-',{
    	id: 'all',
    	iconCls: 'group-all',
		text: "查看所有寻呼",
		cls: 'x-btn-gray',
    },'-',{
    	id: 'read',
    	iconCls: 'group-read',
		text: "查看已阅寻呼",
		cls: 'x-btn-gray',
    },'-',{
    	id: 'unread',
    	iconCls: 'group-unread',
		text: "查看未阅寻呼",
		cls: 'x-btn-gray',
        }],
	initComponent : function(){ 
		var param = {caller: caller, condition: ''};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
	},
	getGroupDa : function(condition, page, pageSize){
		var me = Ext.getCmp('querygrid');
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
        		pageSize: pageSize,
        		condition: condition
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
        			console.log(res.success);
        			dataCount = res.count;
        			me.store.loadData(res.success);
        		}
        	}
        });
	}
});