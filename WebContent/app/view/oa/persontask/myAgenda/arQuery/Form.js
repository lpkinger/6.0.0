Ext.define('erp.view.oa.persontask.myAgenda.arQuery.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpArrangeQueryFormPanel',
	id: 'form', 
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
			var form = Ext.getCmp('form');
			var condition = '(ag_arrange_id=' + em_uu + ")";
			var con = '';
			if(Ext.getCmp('ag_start').value != null && Ext.getCmp('ag_start').value != ''){
				con += " AND (ag_date " + Ext.getCmp('ag_date').value + ")";
			}
			if(Ext.getCmp('ag_end').value != null && Ext.getCmp('ag_end').value != ''){
				con += " AND (ag_end " + Ext.getCmp('ag_end').value + ")";
			}
			if(Ext.getCmp('ag_title').value != null && Ext.getCmp('ag_title').value != ''){
				con += " AND (ag_title like '%" + Ext.getCmp('ag_title').value + "%')";
			}
			if(Ext.getCmp('ag_content').value != null && Ext.getCmp('ag_content').value != ''){
				con += " AND (ag_content like '%" + Ext.getCmp('ag_content').value + "%')";
			}
			if(Ext.getCmp('ag_executor_id').value != null && Ext.getCmp('ag_executor_id').value != ''){
				con += " AND (ag_executor_id like '%" + Ext.getCmp('ag_executor_id').value + "%')";
			}
			if(con != ''){
				grid.getCount('Arrange!Query', condition + con);
			} else {
				showError('请填写筛选条件');return;
			}
			console.log(condition);
    	}
	}, '-', {
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
		var param = {caller: caller, condition: ''};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
	},
	getGroupDa : function(condition, page, pageSize){
		var me = Ext.getCmp('grid');
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