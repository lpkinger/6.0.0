Ext.QuickTips.init();
Ext.define('erp.controller.crm.customermgr.customervisit.ExpandPlan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'crm.customermgr.customervisit.ExpandPlan','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.form.MonthDateField',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpFormPanel':{
				beforerender:function(){
					Ext.getCmp('form').seec=function(code,time,mid,caller){
						me.FormUtil.onAdd('seeCalendar'+code, '查看工作日历', '/jsps/oa/persontask/myAgenda/PersonCalendar.jsp?emcode='+code+'&time='+time+'&mid='+mid+'&caller='+caller);
					};
				}
			},
			'erpSaveButton': {
                click: function(btn) {
                	var form = me.getForm(btn);
                	if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
                    //保存之前的一些前台的逻辑判定
                    this.beforeSave();
                }
            },
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ep_id').value);
				}
			},
			'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ep_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.beforeUpdate();
                }
            },
			'erpAddButton': {
				click: function(){
					if(caller=='ExpandPlan'){
						me.FormUtil.onAdd('addExpandPlan', '新增项目推广计划', 'jsps/crm/customermgr/customervisit/expandPlan.jsp');
					}
					if(caller=='ExpandPlan!DY'){
						me.FormUtil.onAdd('addExpandPlanDY', '新增调研任务计划', 'jsps/crm/customermgr/customervisit/expandPlan.jsp?whoami=ExpandPlan!DY');
					}
				}
			},
			'erpConfirmButton':{
				beforerender:function(btn){
					btn.setWidth(120);
					btn.setText('查看日程安排');
				},
				click:function(btn){
					var grid = Ext.getCmp('grid');//grid.lastSelectedRecord|| || grid.selModel.lastSelected||grid.getSelectionModel().selected.items[0];
					var record= grid.selModel.lastSelected;
					if(!record){
						showError('没选明细行!');
						return;
					}
					if(record.data.epd_emname==''){
						showError('第'+record.data.epd_detno+'行,没写业务员!');
						return;
					}
					if(record.data.epd_starttime==null){
						showError('第'+record.data.epd_detno+'行,没写开始时间!');
						return;
					}
					me.FormUtil.onAdd('seeCalendar'+record.data.epd_emname, '查看工作日历', 
							'jsps/plm/calendar/NewCalendar.jsp?name='+record.data.epd_emname+'&time='+record.data.epd_starttime);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ep_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ep_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ep_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ep_id').value);
				}
			},
			'multidbfindtrigger[name=epd_emname]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='ep_prcode';
	    			trigger.mappingKey='prj_code';
	    			trigger.dbMessage='请先选择项目编号';
    			}
    		},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ep_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ep_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ep_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ep_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'field[name=ep_tpname]': {
	 			   afterrender:function(f){
	 				  if(caller=='ExpandPlan!DY'){
	 					 f.setFieldStyle({
		   					 'color': 'blue'
		   				  });
		   				  f.focusCls = 'mail-attach';
		   				   var c = Ext.Function.bind(me.openRelative, me);
		   				   Ext.EventManager.on(f.inputEl, {
		   					   mousedown : c,
		   					   scope: f,
		   					   buffer : 100
		   				   });
	 				  }
	 			   }
	   		}
    	});
	},
    beforeSave: function() {
        var grid = Ext.getCmp('grid'), items = grid.store.data.items;
        var bool = true;
        Ext.each(items, function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (Ext.Date.format(item.data['epd_endtime'],'Y-m-d') < Ext.Date.format(item.data['epd_starttime'],'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['epd_detno'] + '行的结束时间小于开始日期');
                    return;
                }
            }
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (Ext.Date.format(item.data['epd_endtime'],'Y-m') > Ext.Date.format(item.data['epd_starttime'],'Y-m')) {
                    bool = false;
                    showError('明细表第' + item.data['epd_detno'] + '行的开始时间和结束时间不在同一个月');
                    return;
                }
            }
        });
        if (bool) this.FormUtil.beforeSave(this);
    },
    beforeUpdate: function() {
        var grid = Ext.getCmp('grid'), items = grid.store.data.items;
        var bool = true;
        Ext.each(items, function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (Ext.Date.format(item.data['epd_endtime'],'Y-m-d') < Ext.Date.format(item.data['epd_starttime'],'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['epd_detno'] + '行的结束时间小于开始日期');
                    return;
                }
            }
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (Ext.Date.format(item.data['epd_endtime'],'Y-m') > Ext.Date.format(item.data['epd_starttime'],'Y-m')) {
                    bool = false;
                    showError('明细表第' + item.data['epd_detno'] + '行的开始时间和结束时间不在同一个月');
                    return;
                }
            }
        });
        if (bool) this.FormUtil.onUpdate(this);
    },
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	openRelative:function(e, el, obj){
		if(Ext.getCmp('ep_tpname').value=='')return;
		var tpcode=Ext.getCmp('ep_tpcode').value;
		var url='jsps/crm/marketmgr/marketresearch/preView.jsp?_noc=1&formCondition=rt_codeIS'+tpcode;
		this.FormUtil.onAdd('ReportTemplates!PreView', '模板预览', 
				url);
		
	}
});