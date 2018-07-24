Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.SMTMove', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.SMTMove','core.form.Panel','common.query.GridPanel','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.button.Query','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpQueryButton' : {
    			click: function(btn) {
    				me.query(btn);    				
    			}
    		},
    		'#confirmBtn':{//确认转移
    			click:function(btn){
    				me.confirmMove();
    			}
    		},
    		'#de_oldCode':{
    			specialkey: function(f, e){//按ENTER执行确认
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
    						me.checkDevCode(f.value);
        				}
    				}
    			}
    		},
    		'#de_newCode':{
    			specialkey: function(f, e){//按ENTER执行确认
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
    						me.checkDevCode(f.value);
        				}
    				}
    			}
    		}   		
    	});
    },
	query : function(btn){
		var me = this, grid = Ext.getCmp('querygrid'), form = btn.ownerCt.ownerCt;
		var de_oldCode = Ext.getCmp("de_oldCode").value, mc_code = Ext.getCmp("mc_code").value,condition ;
		if(Ext.isEmpty(de_oldCode)){
			showError("请录入原机台号!");
			return ;
		}
		if(Ext.isEmpty(mc_code)){
			showError("请录入作业单号!");
			return ;
		}
		condition = {de_oldCode:de_oldCode,mc_code:mc_code};
		grid.setLoading(true);//loading...
		Ext.Ajax.request({
        	url : basePath + "pm/mes/loadSMTMoveStore.action",
        	params: condition,
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.datas;
        		if(!data || data.length == 0){
        			showError("没有需要转移的料卷!");
        			grid.store.removeAll();
        			me.GridUtil.add10EmptyItems(grid);
        		} else {
        			if(grid.buffered) {
        				var ln = data.length, records = [], i = 0;
        			    for (; i < ln; i++) {
        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
        			    }
        			    grid.store.purgeRecords();
        			    grid.store.cacheRecords(records);
        			    grid.store.totalCount = ln;
        			    grid.store.guaranteedStart = -1;
        			    grid.store.guaranteedEnd = -1;
        			    var a = grid.store.pageSize - 1;
        			    a = a > ln - 1 ? ln - 1 : a;
        			    grid.store.guaranteeRange(0, a);
        			} else {
        				grid.store.loadData(data);
        			}
        			Ext.getCmp('de_newCode').focus();
        		}
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });		
	},
	checkDevCode:function(data){//判断原机台号是否存在于device，并且de_runstatus=停止		
    	Ext.Ajax.request({
    		url : basePath + "pm/bom/getDescription.action",
    		params: {
    		   tablename: 'Device',
    		   field: 'de_runstatus',
    		   condition: "de_statuscode='AUDITED' and de_code='"+data+"'"
    		},
    		method : 'post',
    		async: false,
    		callback : function(options,success,response){
    			var res = new Ext.decode(response.responseText);
    			if(res.exceptionInfo){
    				showError(res.exceptionInfo);return;
    			}
    			if(res.success){
    				if(res.description == '' || res.description == null){
						showError('机台号:'+data+"不存在或者未审核!");
    					return;		
    				} else if(res.description != '停止'){
    					showError('机台号:'+data+"状态必须为停止!!");
    					return;	
    				}
    			}
    		}
    	});			
	},
	confirmMove:function(){//确认转移
		var de_oldCode = Ext.getCmp("de_oldCode").value,de_newCode = Ext.getCmp("de_newCode").value,
		mc_code = Ext.getCmp("mc_code").value;
		if(Ext.isEmpty(de_oldCode)){
			showError("请录入原机台编号!");return;
		}
		if(Ext.isEmpty(mc_code)){
			showError("请录入作业单号!");return;
		}
		if(Ext.isEmpty(de_newCode)){
			showError("请录入转至机台号!");return;
		}
		// confirm box modify
		// zhuth 2018-2-1
		Ext.Msg.confirm('提示', '确定转移机台?', function(btn) {
			if(btn == 'yes') {
				Ext.Ajax.request({
					url : basePath + "pm/mes/comfirmSMTMove.action",
					params: {de_oldCode:de_oldCode,mc_code:mc_code,de_newCode:de_newCode},
					method : 'post',
					async: false,
					callback : function(options,success,response){
						var res = new Ext.decode(response.responseText);
						if(res.exceptionInfo){
							showError(res.exceptionInfo);return;
						}
						if(res.success){
							 showMessage('系统提示', '机台转移成功!');
							 var grid = Ext.getCmp('querygrid');
							 Ext.getCmp('form').getForm().reset();
							 grid.store.removeAll();
							 me.GridUtil.add10EmptyItems(grid);
						}
					}
				});	
			}
		});
	}
});