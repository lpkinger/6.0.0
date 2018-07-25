Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.PriceMould', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.PriceMould','core.grid.Panel2','core.grid.Panel5','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.TurnInquiry','core.button.TurnPurc',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('pd_statuscode');
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
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			reconfigure: function(grid){
    				Ext.defer(function(){
    					me.isTurnInquiry(function(isTurnInquiry){
    						grid.readOnly = isTurnInquiry > 0;
    						Ext.getCmp('grid').readOnly = isTurnInquiry > 0;
    					});
    				}, 500);
    			},
    			itemclick: this.onGridItemClick
			},
			'field[name=pd_vend1]': {
				afterrender: function(f){
					Ext.defer(function(){
    					me.isTurnInquiryVend(f.value, function(isTurnInquiry){
    						f.setHideTrigger(isTurnInquiry > 0);
    	    				f.setReadOnly(isTurnInquiry > 0);
    					});
    				}, 500);
    			}
    		},
    		'field[name=pd_vend2]': {
				afterrender: function(f){
					Ext.defer(function(){
    					me.isTurnInquiryVend(f.value, function(isTurnInquiry){
    						f.setHideTrigger(isTurnInquiry > 0);
    	    				f.setReadOnly(isTurnInquiry > 0);
    					});
    				}, 500);
    			}
    		},
    		'field[name=pd_vend3]': {
				afterrender: function(f){
					Ext.defer(function(){
    					me.isTurnInquiryVend(f.value, function(isTurnInquiry){
    						f.setHideTrigger(isTurnInquiry > 0);
    	    				f.setReadOnly(isTurnInquiry > 0);
    					});
    				}, 500);
    			}
    		},
    		'field[name=pd_vend4]': {
				afterrender: function(f){
					Ext.defer(function(){
    					me.isTurnInquiryVend(f.value, function(isTurnInquiry){
    						f.setHideTrigger(isTurnInquiry > 0);
    	    				f.setReadOnly(isTurnInquiry > 0);
    					});
    				}, 500);
    			}
    		},
    		'field[name=pd_vend5]': {
				afterrender: function(f){
					Ext.defer(function(){
    					me.isTurnInquiryVend(f.value, function(isTurnInquiry){
    						f.setHideTrigger(isTurnInquiry > 0);
    	    				f.setReadOnly(isTurnInquiry > 0);
    					});
    				}, 500);
    			}
    		},
			'field[name=pd_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pd_date]'),
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
    				this.beforeSavemould();
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pd_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addPriceMould', '新增模具报价单', 'jsps/pm/mould/priceMould.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pd_id').value, false, this.beforeUpdate, this);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pd_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var vend1 = Ext.getCmp('pd_vend1').value, vend2 = Ext.getCmp('pd_vend2').value,
				        vend3 = Ext.getCmp('pd_vend3').value, vend4 = Ext.getCmp('pd_vend4').value,
				        vend5 = Ext.getCmp('pd_vend5').value;
				    if((vend1 && vend2 && vend2 == vend1) || (vend1 && vend3 && vend3 == vend1) || (vend1 && vend4 && vend4 == vend1) || (vend1 && vend5 && vend5 == vend1) || (vend2 && vend3 && vend3 == vend2) || (vend2 && vend4 && vend4 == vend2) || (vend2 && vend5 && vend5 == vend2) || (vend4 && vend3 && vend3 == vend4) || (vend5 && vend3 && vend3 == vend5) || (vend4 && vend5 && vend5 == vend4)){
					   showError('供应商不能重复填写！') ;  
					   return;
				    }
    				me.FormUtil.onAudit(Ext.getCmp('pd_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pd_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('pd_id').value);
				}
			},
			'erpTurnInquiryButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.onTurnInquiry(Ext.getCmp('pd_id').value, false, btn)
    			}
			},
			'erp2PurcButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('pd_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具采购单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnPurcMould.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('pd_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(response.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    		    			if(r.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = r.id;
    	    		    					var url = "jsps/pm/mould/purcMouldDet.jsp?whoami=Purc!Mould&formCondition=pm_id=" + id + 
    	    		    						"&gridCondition=pmd_pmid=" + id ;
    	    		    					window.location.reload();
    	    		    					me.FormUtil.onAdd('PurMould' + id, '模具采购单' + id, url);
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
	beforeSavemould: function(){
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
	   var vend1 = Ext.getCmp('pd_vend1').value, vend2 = Ext.getCmp('pd_vend2').value,
	       vend3 = Ext.getCmp('pd_vend3').value, vend4 = Ext.getCmp('pd_vend4').value,
	       vend5 = Ext.getCmp('pd_vend5').value;
	   if((vend1 && vend2 && vend2 == vend1) || (vend1 && vend3 && vend3 == vend1) || (vend1 && vend4 && vend4 == vend1) || (vend1 && vend5 && vend5 == vend1) || (vend2 && vend3 && vend3 == vend2) || (vend2 && vend4 && vend4 == vend2) || (vend2 && vend5 && vend5 == vend2) || (vend4 && vend3 && vend3 == vend4) || (vend5 && vend3 && vend3 == vend5) || (vend4 && vend5 && vend5 == vend4)){
		   showError('供应商不能重复填写！') ;  
		   return;
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
    onSave:function(param1,param2){
  	   var me = this;
  	   var form = Ext.getCmp('form');
  	   param1 = param1 == null ? [] : "[" + param1.toString() + "]";
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
  		   me.FormUtil.save(r,param1,param2);
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
	   var vend1 = Ext.getCmp('pd_vend1').value, vend2 = Ext.getCmp('pd_vend2').value,
	       vend3 = Ext.getCmp('pd_vend3').value, vend4 = Ext.getCmp('pd_vend4').value,
	       vend5 = Ext.getCmp('pd_vend5').value;
	   if((vend1 && vend2 && vend2 == vend1) || (vend1 && vend3 && vend3 == vend1) || (vend1 && vend4 && vend4 == vend1) || (vend1 && vend5 && vend5 == vend1) || (vend2 && vend3 && vend3 == vend2) || (vend2 && vend4 && vend4 == vend2) || (vend2 && vend5 && vend5 == vend2) || (vend4 && vend3 && vend3 == vend4) || (vend5 && vend3 && vend3 == vend5) || (vend4 && vend5 && vend5 == vend4)){
		   showError('供应商不能重复填写！') ;  
		   return;
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
    onUpdate:function(param1,param2){
       var me = this;
  	   var form = Ext.getCmp('form');
  	   param1 = param1 == null ? [] : "[" + param1.toString() + "]";
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
  		   me.FormUtil.update(r,param1,param2);
  	   }else{
  		   me.FormUtil.checkForm();
  	   }
     },
     isTurnInquiry : function(callback) {
    	 var me = this, field = Ext.getCmp("pd_id"), id = field ? field.getValue() : null;
    	 if(id){
	    	 Ext.Ajax.request({
	 			url : basePath + 'common/getFieldData.action',
	 			params : {
	 				caller: 'InquiryMould',
		   			field: 'count(*)',
		   			condition: 'in_sourceid=' + Ext.getCmp("pd_id").value + ' AND in_sourcetype=\'模具报价单\' and in_statuscode<>\'NULLIFIED\''
	 			},
	 			callback : function(opt, s, res) {
	 				var r = Ext.decode(res.responseText);
	 				if (r.exceptionInfo) {
	 					showError(r.exceptionInfo);
	 				} else if (r.success && r.data) {
	 					callback.call(null, r.data);
	 				}
	 			}
	 		});
	    }
 	},
 	isTurnInquiryVend : function(vecode, callback) {
 		var me = this, field = Ext.getCmp("pd_id"), id = field ? field.getValue() : null;
 		if (vecode && id){
	    	 Ext.Ajax.request({
	 			url : basePath + 'common/getFieldData.action',
	 			params : {
	 				caller: 'InquiryMould',
		   			field: 'count(*)',
		   			condition: 'in_sourceid=' + id + ' AND in_sourcetype=\'模具报价单\' and in_statuscode<>\'NULLIFIED\' and in_vendcode=\'' + vecode + '\''
	 			},
	 			callback : function(opt, s, res) {
	 				var r = Ext.decode(res.responseText);
	 				if (r.exceptionInfo) {
	 					showError(r.exceptionInfo);
	 				} else if (r.success && r.data) {
	 					callback.call(null, r.data);
	 				}
	 			}
	 		});
	    }
	},
 	onTurnInquiry: function(id, allowEmpty, btn){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			var s = me.FormUtil.checkFormDirty(form);
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
				var param = grids[0].GridUtil.getAllGridStore(grids[0]);
				if(grids[0].necessaryField && grids[0].necessaryField.length > 0 && (param == null || param == '') && (allowEmpty !== true)){
					var errInfo = grids[0].GridUtil.getUnFinish(grids[0]);
					if(errInfo.length > 0)
						showError("明细表有必填字段未完成填写<hr>" + errInfo);
					else
						showError("明细表还未添加数据,无法转询价!");
					return;
				}
				Ext.each(grids, function(grid, index){
					if(grid.GridUtil){
						var msg = grid.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
							s = s + '<br/>' + grid.GridUtil.checkGridDirty(grid);
						}
					}
				});
			}
			if(s == '' || s == '<br/>'){
				me.turnInquiry(btn);
			} else {
				Ext.MessageBox.show({
					title:'保存修改?',
					msg: '该单据已被修改:<br/>' + s + '<br/>转单前要先保存吗？',
					buttons: Ext.Msg.YESNOCANCEL,
					icon: Ext.Msg.WARNING,
					fn: function(btn){
						if(btn == 'yes'){
							me.beforeUpdate();
						} else if(btn == 'no'){
							me.turnInquiry(btn);	
						} else {
							return;
						}
					}
				});
			}
		} else {
			me.checkForm();
		}
	},
	turnInquiry: function(btn){
		var me=this;
		warnMsg("确定要转入模具询价单吗?", function(btn){
			if(btn == 'yes'){
				me.FormUtil.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'pm/mould/turnInquiry.action',
			   		params: {
			   			id: Ext.getCmp('pd_id').value
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			me.FormUtil.getActiveTab().setLoading(false);
			   			var r = new Ext.decode(response.responseText);
			   			if(r.exceptionInfo){
			   				showError(r.exceptionInfo);
			   				return;
			   			}
			   			if(r.success && r.content){
			   				var msg = "";
			   				Ext.Array.each(r.content, function(item){
			   					if(item.errMsg) {
			   						msg += item.errMsg + '<hr>';
			   					} else if(item.id) {
			   						msg += '模具询价单号:<a href="javascript:openUrl2(\'jsps/pm/mould/inquiry.jsp?formCondition=in_idIS' 
		    							+ item.id + '&gridCondition=idd_inidIS' + item.id + '\',\'模具询价单\',\'pd_id\','+item.id+');">' + item.code + '</a><hr>';	
			   					}
			   				});
	    					showMessage('提示', msg);
	    					window.location.reload();
			   			}
			   		}
				});
			}
		});
	}
});