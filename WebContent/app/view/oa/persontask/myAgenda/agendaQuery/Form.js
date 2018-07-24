Ext.define('erp.view.oa.persontask.myAgenda.agendaQuery.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpAgendaQueryFormPanel',
	id: 'queryform', 
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
			var grid = Ext.getCmp('querygrid');
			var form = Ext.getCmp('queryform');
			var condition = '';
			Ext.each(form.items.items, function(f){
				if(f.logic != null && f.logic != '' && f.value != null && f.value != ''){
					if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
						if(condition == ''){
							condition += f.logic + " " + f.value;
						} else {
							condition += ' AND ' + f.logic + " " + f.value;
						}
					} else {
						if(condition == ''){
							condition += f.logic + " like '%" + f.value + "%'";
						} else {
							condition += ' AND ' + f.logic + " like '%" + f.value + "%'";
						}
					}
				}
			});
			if(condition != ''){
				condition += ' AND ' + '(ag_arrange_id=' + em_uu + " OR ag_executor_id like '%" + em_uu + "%')";
				url = "oa/persontask/myAgenda/searchArrange.action";
				form.getGroupDa(condition);
			} else {
				showError('请填写筛选条件');return;
			}
			console.log(condition);
//			alert(condition);
    	}
	}, '-', {
		name: 'summary',
		text: '汇总',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(){
//    		var grid = Ext.getCmp('querygrid');
//    		grid.BaseUtil.exportexcel(grid);
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
		text: '查看日程日历',
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
//    		Ext.Ajax.request({//拿到grid的columns
//            	url : basePath + 'plm/calendar/getMyAgenda.action',
//            	params:{
//            	  emid: em_uu
//            	},
//            	
//            	method : 'post',
//            	callback : function(options,success,response){
//            		var res = new Ext.decode(response.responseText);
//            		if(res.exceptionInfo){
//            			showError(res.exceptionInfo);return;
//            		}if(res.success){
//            			console.log(response);
//            		}
//            	}
//        	});
//    		window.location.href = basePath + "jsps/oa/persontask/myAgenda/myAgendaCalendar.jsp";
    		window.open(basePath + "jsps/oa/persontask/myAgenda/myAgendaCalendar.jsp");
    	}
	}],
//	bbar:[{
//		text: '查看日程日历',
//		iconCls: 'x-button-icon-query',
//    	cls: 'x-btn-gray',
//    	handler: function(){
//    		Ext.Ajax.request({//拿到grid的columns
//            	url : basePath + 'plm/calendar/getMyAgenda.action',
//            	params:{
//            	  emid: em_uu
//            	},
//            	
//            	method : 'post',
//            	callback : function(options,success,response){
//            		var res = new Ext.decode(response.responseText);
//            		if(res.exceptionInfo){
//            			showError(res.exceptionInfo);return;
//            		}if(res.success){
//            			console.log(response);
//            		}
//            	}
//        	});
//    		window.location.href = basePath + "jsps/oa/persontask/myAgenda/myAgendaCalendar.jsp";
//    		window.open(basePath + "jsps/oa/persontask/myAgenda/myAgendaCalendar.jsp");
//    	}
//	}
//	, '-', {
//		text: '按周查看',
//		iconCls: 'x-button-icon-query',
//    	cls: 'x-btn-gray',
//    	handler: function(){
//    		
//    	}
//	}, '-', {
//		text: '按月查看',
//		iconCls: 'x-button-icon-query',
//    	cls: 'x-btn-gray',
//    	handler: function(){
//    		
//    	}
//	}
//	],
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