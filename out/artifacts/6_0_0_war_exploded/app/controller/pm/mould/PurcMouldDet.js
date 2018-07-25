Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.PurcMouldDet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','pm.mould.PurcMouldDet','core.grid.Panel2','core.grid.Panel5','core.toolbar.Toolbar','core.form.MultiField', 'core.form.FileField',
     		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
 			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
 			'core.button.TurnPurcProdIO', 'core.button.UpdatePayStatus', 'core.button.TurnFeePlease',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.YnField','core.button.CopyByConfigs'      
 	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
			},
			'erpGridPanel5': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
			},
			'field[name=pm_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pm_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var grid2 = Ext.getCmp('grid2'), items = grid2.store.data.items, pm_date = Ext.getCmp('pm_date').value;
    				var i=0;
    				//计划付款日期
    		        Ext.each(items, function(item) {
    		        	if (item.data['pd_paydesc'] == '尾款') {
							i++;
						}
    		            if (item.dirty && item.data[grid2.necessaryField] != null && item.data[grid2.necessaryField] != "") {
    		                if (item.data['pd_planpaydate'] == null) {
    		                    item.set('pd_planpaydate', pm_date);
    		                } else if (Ext.Date.format(item.data['pd_planpaydate'],'Y-m-d') < Ext.Date.format(pm_date,'Y-m-d')) {
    		                    bool = false;
    		                    showError('明细表第' + item.data['pd_detno'] + '行的计划付款日期小于单据日期');
    		                    return;
    		                }
    		            }
    		        });
    		        if (i > 1) {
						showError('尾款只能选择一个,请重新选择!');
						return;
					}
    				this.beforeSave();
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pm_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid2 = Ext.getCmp('grid2'), items = grid2.store.data.items, pm_date = Ext.getCmp('pm_date').value;
    				var i=0;
    				//计划付款日期不能小于单据日期
    		        Ext.each(items, function(item) {
    		        	if (item.data['pd_paydesc'] == '尾款') {
							i++;
						}
    		            if (item.dirty && item.data[grid2.necessaryField] != null && item.data[grid2.necessaryField] != "") {
    		                if (item.data['pd_planpaydate'] == null) {
    		                    item.set('pd_planpaydate', pm_date);
    		                } else if (Ext.Date.format(item.data['pd_planpaydate'],'Y-m-d') < Ext.Date.format(pm_date,'Y-m-d')) {
    		                    bool = false;
    		                    showError('明细表第' + item.data['pd_detno'] + '行的计划付款日期小于单据日期');
    		                    return;
    		                }
    		            }
    		        });
    		        if (i > 1) {
						showError('尾款只能选择一个,请重新选择!');
						return;
					}
    				this.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addPurMould', '新增模具采购单', 'jsps/pm/mould/purcMouldDet.jsp?whoami=Purc!Mould');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pm_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pm_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pm_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode'),
    					paystatus = Ext.getCmp('pm_paystatus'),
    					turnstatus = Ext.getCmp('pm_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(paystatus && paystatus.value != '未付款'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pm_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
	    			var condition = '{PURMOULD.pm_id}=' + Ext.getCmp(me.getForm(btn).keyField).value + '';
	    			var reportName="MouldPur";
			    	var id = Ext.getCmp(me.getForm(btn).keyField).value;
	    			me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			},
			'dbfindtrigger[name=pmd_pscode]': {
    			afterrender: function(t){
    				var grid = Ext.getCmp('grid');
    				var column = grid.down('gridcolumn[dataIndex=pmd_pscode]');
    				if(column && column.dbfind && column.dbfind.indexOf('MouldQuoteDetail') > -1) {
    					t.gridKey = "pm_vendcode";
    	    			t.mappinggirdKey = "mq_vendcode";
    	    			t.gridErrorMessage = "请填写供应商号!";
    				}
    			}
    		},
    		'multidbfindtrigger[name=pmd_pscode]': {
    			afterrender: function(t){
    				var grid = Ext.getCmp('grid');
    				var column = grid.down('gridcolumn[dataIndex=pmd_pscode]');
    				if(column && column.dbfind && column.dbfind.indexOf('MouldQuoteDetail') > -1) {
	    				t.gridKey = "pm_vendcode";
	    				t.mappinggirdKey = "mq_vendcode";
	    				t.gridErrorMessage = "请填写供应商号!";
    				}
    			}
    		},
			'erpTurnPurcProdIOButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode'), turnstatus = Ext.getCmp('pm_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具验收报告吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnYSReport.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pm_id').value
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
    	    		    					var url = "jsps/pm/mould/YSReport.jsp?whoami=YSReport!Mould&formCondition=mo_id=" + id + 
    	    		    						"&gridCondition=yd_moid=" + id ;
    	    		    					me.FormUtil.onAdd('YSReport' + id, '模具验收报告' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
			},
			'erpTurnFeePleaseButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode'), turnstatus = Ext.getCmp('pm_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具付款申请单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/purcTurnFeePlease.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pm_id').value
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
    	    		    					var url = "jsps/common/commonpage.jsp?whoami=FeePlease!Mould&formCondition=mp_id=" + id + 
    	    		    						"&gridCondition=mfd_mpid=" + id ;
    	    		    					me.FormUtil.onAdd('MOULDFEEPLEASE' + id, '模具付款申请单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
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
	beforeSave: function(){
		var me = this;
		var mm = me.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
 		   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
 			   mm.getSeqId(form);
 		   }
 	   }
 	   var grids = Ext.ComponentQuery.query('gridpanel');
 	   var arg=new Array();
 	   if(grids.length > 0){
 		   for(var i=0;i<grids.length;i++){
 			   var param = me.GridUtil.getGridStore(grids[i]);
 			   if(grids[i].necessaryField.length > 0 && (param == null || param == '')){
 				   arg.push([]);
 			   } else {
 				   arg.push(param);
 			   }
 		   }
 		   me.onSave(arg[0],arg[1]);
 	   }else {
 		   me.onSave([]);
 	   }
    },
    onSave:function(param,param2){
 	   var me = this;
 	   var form = Ext.getCmp('form');
 	   param = param == null ? [] : "[" + param.toString() + "]";
 	   param2 = param2 == null ? [] : "[" + param2.toString() + "]";
 	   if(form.getForm().isValid()){
 		   //form里面数据
 		   Ext.each(form.items.items, function(item){
 			   if(item.xtype == 'numberfield'){
 				   //number类型赋默认值，不然sql无法执行
 				   if(item.value == null || item.value == ''){
 					   item.setValue(0);
 				   }
 			   }
 		   });
 		   var r = form.getValues();
 		   //去除ignore字段
 		   var keys = Ext.Object.getKeys(r), f;
 		   var reg = /[!@#$%^&*()'":,\/?]/;
 		   Ext.each(keys, function(k){
 			   f = form.down('#' + k);
 			   if(f && f.logic == 'ignore') {
 				   delete r[k];
 			   }
 			   //codeField值强制大写,自动过滤特殊字符
 			   if(k == form.codeField && !Ext.isEmpty(r[k])) {
 				   r[k] = r[k].trim().toUpperCase().replace(reg, '');
 			   }
 		   });
 		   if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
 			   form.saveUrl = form.saveUrl + "?caller=" + caller;
 		   }
 		   me.FormUtil.save(r,param,param2);
 	   }else{
 		   me.FormUtil.checkForm();
 	   }
    },
	beforeUpdate: function(){
		var me = this;
		var mm = me.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
 		   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
 			   mm.getSeqId(form);
 		   }
 	   }
 	   var grids = Ext.ComponentQuery.query('gridpanel');
 	   var arg=new Array();
 	   if(grids.length > 0){
 		   for(var i=0;i<grids.length;i++){
 			   var param = me.GridUtil.getGridStore(grids[i]);
 			   if(grids[i].necessaryField.length > 0 && (param == null || param == '')){
 				   arg.push([]);
 			   } else {
 				   arg.push(param);
 			   }
 		   }
 		   me.onUpdate(arg[0],arg[1]);
 	   }else {
 		   me.onUpdate([]);
 	   }
    },
    onUpdate:function(param,param2){
 	   var me = this;
 	   var form = Ext.getCmp('form');
 	   param = param == null ? [] : "[" + param.toString() + "]";
 	   param2 = param2 == null ? [] : "[" + param2.toString() + "]";
 	   if(form.getForm().isValid()){
 		   //form里面数据
 		   Ext.each(form.items.items, function(item){
 			   if(item.xtype == 'numberfield'){
 				   //number类型赋默认值，不然sql无法执行
 				   if(item.value == null || item.value == ''){
 					   item.setValue(0);
 				   }
 			   }
 		   });
 		   var r = form.getValues();
 		   //去除ignore字段
 		   var keys = Ext.Object.getKeys(r), f;
 		   var reg = /[!@#$%^&*()'":,\/?]/;
 		   Ext.each(keys, function(k){
 			   f = form.down('#' + k);
 			   if(f && f.logic == 'ignore') {
 				   delete r[k];
 			   }
 			   //codeField值强制大写,自动过滤特殊字符
 			   if(k == form.codeField && !Ext.isEmpty(r[k])) {
 				   r[k] = r[k].trim().toUpperCase().replace(reg, '');
 			   }
 		   });
 		   if(!me.FormUtil.contains(form.updateUrl, '?caller=', true)){
 			   form.updateUrl = form.updateUrl + "?caller=" + caller;
 		   }
 		   me.FormUtil.update(r,param,param2);
 	   }else{
 		   me.FormUtil.checkForm();
 	   }
    }
});