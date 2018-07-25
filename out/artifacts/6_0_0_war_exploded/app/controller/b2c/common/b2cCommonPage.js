Ext.QuickTips.init();
Ext.define('erp.controller.b2c.common.b2cCommonPage', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'b2c.common.b2cForm','b2c.common.b2cGrid','b2c.common.b2cPanel','b2c.common.Viewport','core.toolbar.Toolbar',
      		'core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup','core.button.TurnMJProject',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('grid');
    				if(grid)
    					me.resize(form, grid);
    			} 			
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure: function(grid){
    				var form = Ext.getCmp('form');
        			if(form)
        				me.resize(form, grid);
    			}
    		},
    		'erpSaveButton': {
    			afterrender: function(btn){
    				var form = me.getForm(btn);
    				var codeField = Ext.getCmp(form.codeField);  				
    				if(Ext.getCmp(form.codeField) && (Ext.getCmp(form.codeField).value != null && Ext.getCmp(form.codeField).value != '')){
    						btn.hide();
    					}
    			},
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
    						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				if(caller == 'FeePlease!Mould'){
    					me.getamount();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var bool=true;
    				if(caller == 'FeePlease!Mould'){
    					me.getamount();
    				}
    				if(caller=='AttendSystem'){//考勤系统设置 
    					bool=me.checkTime();
    				}
					if(bool)
    					this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onBanned(crid);
				}
			},
			'erpResBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'DISABLE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResBanned(crid);
				}
			},
			'erpEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onEnd(crid);
				}
			},
			'erpResEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResEnd(crid);
				}
			},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    			var condition="";
    			var reportName="";
    			    if(caller=="FeePlease!Mould"){
    			    	 condition = '{MOULDFEEPLEASE.mp_id}=' + Ext.getCmp(me.getForm(btn).keyField).value + '';
    			    	 reportName="MouldFeePlease";
    			    }
    			    if(caller=="Purc!Mould"){
    			    	condition = '{PURMOULD.pm_id}=' + Ext.getCmp(me.getForm(btn).keyField).value + '';
   			    	    reportName="MouldPur";
    			    }
    			    var id = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpConfirmButton': {
    			click: function(btn){
    				me.FormUtil.onConfirm(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpTurnMJProjectButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转模具模具委托保管书吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnMJProject.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('mo_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(o, s, res){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(res.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    			   			if(r.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = r.id;
    	    		    					var url = "jsps/common/commonpage.jsp?whoami=MJProject!Mould&formCondition=ws_id=" + id + 
    	    		    						"&gridCondition=wd_wsid=" + id;
    	    		    					me.FormUtil.onAdd('MJProject' + id, '模具委托保管书' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'htmleditor': {
    			afterrender: function(f){
    				f.setHeight(500);
    			}
    		},
    		'datetimefield[id=ag_end]': {
    			change: function(field){
    				var time = Ext.getCmp('ag_start').value;
    				if(time == null || time == ''){
    					showError('请先选择日程开始时间');
    					field.setValue('');
    				} else {
    					var start = Date.parse(time);
    					var end = Date.parse(field.value);
    					if(start > end){
    						showError('结束时间不能早于开始时间 ');
    						field.setValue('');
    					}    					
    				}
    			}
    		},
    		'datetimefield[id=ag_predict]': {
    			change: function(field){
    				var time = Ext.getCmp('ag_start').value;
    				if(time == null || time == ''){
    					showError('请先选择日程开始时间');
    					field.setValue('');
    				} else {
    					var start = Date.parse(time);
    					var end = Date.parse(field.value);
    					if(start < end){
    						showError('提醒时间不能晚于开始时间 ');
    						field.setValue('');
    					}    					
    				}
    			}
    		},
    		'dbfindtrigger[name=mfd_purcdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['mfd_purccode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "pm_code='" + code + "'";
    				}
    			}
    		}
    	});
    }, 
    getamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		var appamount = 0;
		Ext.each(items,function(item,index){
			if(item.data['mfd_pscode']!=null&&item.data['mfd_pscode']!=""){
				amount= amount + Number(item.data['mfd_purcamount']);
				appamount = appamount + Number(item.data['mfd_amount']);
			}
		});
		Ext.getCmp('mp_orderamount').setValue(Ext.util.Format.number(amount,'0.00'));
		Ext.getCmp('mp_total').setValue(Ext.util.Format.number(appamount,'0.00'));
	},
	checkTime:function(){//考勤系统设置 
 		var amstarttime=Ext.getCmp('as_amstarttime').value==null?'':Ext.getCmp('as_amstarttime').value,
 		    amendtime=Ext.getCmp('as_amendtime').value==null?'':Ext.getCmp('as_amendtime').value,
 		    pmstarttime=Ext.getCmp('as_pmstarttime').value==null?'':Ext.getCmp('as_pmstarttime').value,
 		    pmendtime=Ext.getCmp('as_pmendtime').value==null?'':Ext.getCmp('as_pmendtime').value;
 		if((amstarttime!=''||amendtime!=''||pmstarttime!=''||pmendtime!='')){
 			if((amstarttime==''||amendtime==''||pmstarttime==''||pmendtime=='')){//上下班时间设置了其中一个就要全部设置
 				showError('上下班时间设置不完整');
 				return false;
 			}else if(new Date(amstarttime)>new Date(amendtime)||
 					 new Date(amendtime)>new Date(pmstarttime)||
 					 new Date(pmstarttime)>new Date(pmendtime)){
 				showError('上下班时间设置有误，时间顺序应为：上午上班时间<=上午下班时间<=下午上班时间<=下午下班时间');
 				return false;
 			}
 		}
    	return true;    					
	},
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
    			fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(70 + fh);
			grid.setHeight(height - fh - 70);
			this.resized = true;
		}
    }
});