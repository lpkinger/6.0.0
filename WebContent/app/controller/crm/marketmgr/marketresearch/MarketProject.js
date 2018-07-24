Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.MarketProject', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'crm.marketmgr.marketresearch.MarketProject','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.button.TurnMake',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(me.check()){
						return;
					};
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('prjplan_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('prjplan_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('prjplan_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					if(me.check()){
						return;
					};
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMarketProject', '新增调研计划', 'jsps/crm/marketmgr/marketresearch/MarketProject.jsp');
				}
			},
			'erpTurnMakeButton':{
				beforerender:function(btn){
					btn.setText('生成任务');
					btn.setWidth(120);
				},
				afterrender: function(btn){
					btn.hide();
					var status = Ext.getCmp('prjplan_statuscode');
					/*if(status && status.value != 'AUDITED'||isturn==1){
						btn.hide();
					}*/
				},
				click:function(){
					var tt_id=Ext.getCmp('prjplan_ttid');
					if(tt_id==0){
						showError('任务模板没选或者无效，请重选！');return;
					}
					me.turnTask(Ext.getCmp('prjplan_id').value);
					var prjplan_id=Ext.getCmp('prjplan_id').value;
					var s=basePath+'jsps/common/gridpage.jsp?whoami=PlanTurnTask&gridCondition=prjplan_id='+prjplan_id;
					var html='<iframe width=100% height=100% src="'+s+'"/>';
					var win=new Ext.window.Window({
			    		height:500,
			    		width:800,
			    		modal:true,
			    		listeners : {
    	    				close : function(){
    	    					window.location.reload();
    	    				}
    	    			},
			    		html:html});
					win.show();
				}
			},
			'erpTurnCustomerButton':{
				beforerender: function(btn){
					btn.setText('项目计划');
					btn.setWidth(120);
				},
				click:function(){
					me.FormUtil.onAdd('projectsub', '项目计划', 'jsps/crm/projectscheduler/projectScheduler.jsp?startDate='+ Ext.getCmp('prjplan_startdate').value+'&endDate='+Ext.getCmp('prjplan_enddate').value+'&formCondition='+'prjplanid='+Ext.getCmp('prjplan_id').value+ '&level=0');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('prjplan_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('prjplan_id').value,true);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('prjplan_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('prjplan_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('prjplan_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('prjplan_id').value);
				}
			},'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('prjplan_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('prjplan_id').value);
				}
			}
    	});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getGriddata: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var jsonGridData = new Array();
		var s = grid.getStore().data.items;//获取store里面的数据
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			jsonGridData.push(Ext.JSON.encode(data));
		}
		return jsonGridData;
	},
	check:function(){
		if(Ext.getCmp('prjplan_startdate').value>Ext.getCmp('prjplan_enddate').value){
			showError('结束时间不能小于开始时间！');return true;
		}
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var flag=false;
		Ext.each(items,function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['ppd_costid']==null||item.data['ppd_costid']==0){
					showError('第'+item.data['ppd_detno']+'行的费用类型没选或无效！');flag=true;
				}
				if(item.data['ppd_standardamount']<item.data['ppd_amount']){
					showError('第'+item.data['ppd_detno']+'行的预算费用超出标准金额！');flag=true;
				}
			}
		});
		return flag;
	},
	turnTask:function(id){
		Ext.Ajax.request({
        	url : basePath + '/crm/marketmgr/turnTask.action',
        	params: {id:id},
        	method : 'post',
        	async:false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        	}
        });
	}
});