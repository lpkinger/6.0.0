Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.InquiryMould', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.InquiryMould','core.grid.Panel2','core.grid.Panel5','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.ResSubmit',
  				'core.button.ResAudit','core.button.AgreeToPrice', 'core.button.Nullify',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({
			'field[name=in_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=in_date]'),
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
    				if(Ext.Date.format(Ext.getCmp('in_enddate').value, 'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
    					showError('报价截止日期小于当前日期'); return;
    				}
    				this.beforeSave();
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('in_enddate').value, 'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
    					showError('报价截止日期小于当前日期'); return;
    				}
    				me.beforeUpdate();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addInquiryMould'+Ext.getCmp('in_id').value, '新增模具询价单', 'jsps/pm/mould/inquiry.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('in_enddate').value, 'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
    					showError('报价截止日期小于当前日期'); return;
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode'), sendstatus = Ext.getCmp('in_sendstatus');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(sendstatus && sendstatus.value == '已上传'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpNullifyButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this, win = Ext.getCmp('nullify-win'), bool=true;
    				Ext.Ajax.request({
						url : basePath + "pm/mould/nullifybeforeCheck.action",
						params: {
							id:Ext.getCmp('in_id').value
						},
						method : 'post',
						async: false,
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);
								bool = false;
							}
						}
					});
  				   	if(!win){
  					   var field = Ext.getCmp('in_reason'), reason = field ? field.value : '';
  					   win = Ext.create('Ext.Window', {
  						   id: 'nullify-win',
  						   title: '作废',
  						   height: 150,
  						   width: 400,
  						   items: [{
  							   xtype: 'form',
  							   height: '100%',
  							   width: '100%',
  							   bodyStyle: 'background:#f1f2f5;',
  							   items: [{
  								   margin: '10 0 0 0',
  								   xtype: 'textareatrigger',
  								   fieldLabel: '作废原因',
  								   name:'in_reason',
  								   value: reason
  							   }],
  							   closeAction: 'hide',
  							   buttonAlign: 'center',
  							   layout: {
  								   type: 'vbox',
  								   align: 'center'
  							   },
  							   buttons: [{
  								   text: $I18N.common.button.erpConfirmButton,
  								   cls: 'x-btn-gray',
  								   handler: function(btn) {
  									   var form = btn.ownerCt.ownerCt,
  									   a = form.down('textfield[name=in_reason]');
  									   if(form.getForm().isDirty()) {
  										   if(Ext.isEmpty(a.value)){
  											 showError('请填写作废原因！'); return;
  										   }
  										   me.nullify(Ext.getCmp('in_id').value, a.value);
  									   }
  								   }
  							   }, {
  								   text: $I18N.common.button.erpCloseButton,
  								   cls: 'x-btn-gray',
  								   handler: function(btn) {
  									   btn.up('window').hide();
  								   }
  							   }]
  						   }]
  					   });
  				   }
  				   if(bool){
  					 win.show();
  				   }
    			}
    		},
    		'erpAgreeToPriceButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
       				warnMsg("确定要转入价格库吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnPurcPrice.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('in_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/purchase/purchasePrice.jsp?formCondition=pp_id=" + id + 
    	    		    						"&gridCondition=ppd_ppid=" + id;
    	    		    					me.FormUtil.onAdd('PurchasePrice' + id, '物料核价单' + id, url);
    	    		    				});
    	    		    				window.location.reload();
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
     nullify: function(in_id, val1) {
 		var me = this;
     	Ext.Ajax.request({
     		url: basePath + 'pm/mould/nullifyInquiryMould.action',
     	   	params: {
     	   		caller: caller,
     	   		id: in_id,
 	    		reason: val1
     	   	},
 	    	callback: function(opt, s, r) {
 	    		var rs = Ext.decode(r.responseText);
 		   		if(rs.exceptionInfo) {
 		   			showError(rs.exceptionInfo);
 		   		} else {
 		   			alert('作废成功!');
 		   			window.location.reload();
 		   		}
    			}
     	});
 	}
});