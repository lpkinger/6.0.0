Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.BusinessTrip', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.button.PrintByCondition','hr.attendance.BusinessTrip','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.Scan','core.form.FileField','core.form.SeparNumber',
    		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.form.DateHourMinuteField',
  				'core.button.ResSubmit','core.button.TurnCLFBX','core.button.TurnFYBX','core.button.TurnYHFKSQ','core.button.VoCreate','core.form.CustomerSelectField','core.form.BusinessTripField',
  				'core.button.TurnYWZDBX','core.button.End','core.button.ResEnd','core.button.Confirm','core.trigger.MultiDbfindTrigger','core.form.DateHourMinuteComboField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.button.TurnBankRegister','core.form.MultiField','oa.fee.FeeBackGrid',
  			'erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','common.datalist.Toolbar','oa.fee.feePleaseFYBX','core.form.ConDateHourMinuteField','core.form.CheckBoxGroup'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'dbfindtrigger[name=fpd_d5]':{
				afterrender:function(f){
					Ext.apply(f, {
						 extend: 'Ext.form.field.Trigger',
    				     triggerCls: 'x-form-search-trigger',
    				     autoDbfind:false,
    				     initComponent: function() {
    					   this.addEvents({
    							aftertrigger: true,
    							beforetrigger: true
    					   });
    					   this.callParent(arguments);  
    				 	 },
    				   	 onTriggerClick: function() {var trigger = this;me.onTriggerClick(trigger);}
					});
				}
    		},
    		'dbfindtrigger[name=fpd_air_starting]':{
    			aftertrigger:function(trigger, record, dbfinds){
    	 			var me = this;
    	 			var form = Ext.ComponentQuery.query('textfield');
    	 			if(record.data){
    	 				Ext.Array.each(form,function(item,index){
        	 				if(item.name=='fpd_air_starting'){
        	 					var id = trigger.id;
        	 					Ext.getCmp(id).setValue(record.data.ac_cityname);
        	 					Ext.getCmp('fpd_citycode1'+item.index).setValue(record.data.ac_citycode);
        	 				}
        	 				
        	 			});
    	 			}
    			}
    		},
    		'dbfindtrigger[name=fpd_air_destination]':{
    			aftertrigger:function(trigger, record, dbfinds){
    	 			var me = this;
    	 			var form = Ext.ComponentQuery.query('textfield');
    	 			if(record.data){
    	 				Ext.Array.each(form,function(item,index){
        	 				if(item.name=='fpd_air_destination'){
        	 					var id = trigger.id;
        	 					Ext.getCmp(id).setValue(record.data.ac_cityname);
        	 					Ext.getCmp('fpd_citycode2'+item.index).setValue(record.data.ac_citycode);
        	 				}
        	 			});
    	 			}
    			}
    		},
    		'dbfindtrigger[name=fpd_train_starting]':{
    			aftertrigger:function(trigger, record, dbfinds){
    	 			var me = this;
    	 			var form = Ext.ComponentQuery.query('textfield');
    	 			if(record.data){
    	 				Ext.Array.each(form,function(item,index){
        	 				if(item.name=='fpd_train_starting'){
        	 					var id = trigger.id;
        	 					Ext.getCmp(id).setValue(record.data.tc_cityname);
        	 					Ext.getCmp('fpd_citycode3'+item.index).setValue(record.data.tc_citycode);
        	 				}
        	 			});
    	 			}
    			}
    		},
    		'dbfindtrigger[name=fpd_train_destination]':{
    			aftertrigger:function(trigger, record, dbfinds){
    	 			var me = this;
    	 			var form = Ext.ComponentQuery.query('textfield');
    	 			if(record.data){
    	 				Ext.Array.each(form,function(item,index){
        	 				if(item.name=='fpd_train_destination'){
        	 					var id = trigger.id;
        	 					Ext.getCmp(id).setValue(record.data.tc_cityname);
        	 					Ext.getCmp('fpd_citycode4'+item.index).setValue(record.data.tc_citycode);
        	 				}
        	 			});
    	 			}
    			}
    		},
    		'dbfindtrigger[name=fpd_hotel_city]':{
    			aftertrigger:function(trigger, record, dbfinds){
    	 			var me = this;
    	 			var form = Ext.ComponentQuery.query('textfield');
    	 			if(record.data){
    	 				Ext.Array.each(form,function(item,index){
        	 				if(item.name=='fpd_hotel_city'){
        	 					var id = trigger.id;
        	 					Ext.getCmp(id).setValue(record.data.hc_cityname);
        	 					Ext.getCmp('fpd_citycode5'+item.index).setValue(record.data.hc_citycode);
        	 				}
        	 			});
    	 			}
    			}
    		},
    		'combo[name=fpd_res_type]':{
    			delay: 200,
    			change: function(combo){
					this.hidecolumns(combo,false);
				}
    		},
    		'combo[name=fp_turnmaster]':{
    			afterrender:function(combo){	
    				Ext.Ajax.request({
    					url:basePath+'common/getAbleMasters.action',
    					method:'post',
    					callback:function(opts,suc,res){
    						var r = Ext.decode(res.responseText),masterdata = [];
    						if(r.masters) {
    							var sobs = r.masters;
    							Ext.each(sobs, function(sob) {
    								var obj ={};
    								if(sob.ma_user) {
    									obj.ma_user = sob.ma_user;
    									obj.ma_function = sob.ma_function;
    									masterdata.push(obj);
    								}
    							});
    						}
    					combo.displayField ='ma_function';
    					combo.valueField = 'ma_user';
    					combo.queryMode ='local';
    					var store = Ext.create('Ext.data.Store',{
        						fields: ['ma_function'],
        						data:masterdata,
        						autoLoad:true,
        					});
    					combo.bindStore(store,true);
    					}
    				});
    			},
    			select:function(combo,record){
    				if(record[0]) {
    					combo.setValue(record[0].data.ma_function);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
        			this.beforeSave();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
   					me.FormUtil.onAdd('add' + caller, '新增出差申请', "jsps/hr/attendance/businessTrip.jsp");
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.beforeUpdate();
    				
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('fp_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
   					this.FormUtil.onSubmit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
    				var   reportName="AccountRegZW";
					var condition='{FeePlease.fp_id}='+Ext.getCmp('fp_id').value+'';
					var id=Ext.getCmp('fp_id').value;
					me.FormUtil.onwindowsPrint(id,reportName,condition);
				}
    		},
    		'erpEndButton': {
    			 afterrender: function(btn) {
                     var status = Ext.getCmp('fp_statuscode');
                     if (status && status.value != 'AUDITED') {
                         btn.hide();
                     }
                 },
    			click: function(btn){			
	    			this.FormUtil.onEnd(Ext.getCmp('fp_id').value);					
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResEnd(Ext.getCmp('fp_id').value);
    			}
    		},
    		'field[name=fp_endreason]':{
    			beforerender:function(field){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value == 'AUDITED'){
    					field.readOnly=false;
    				}
    			}
    		},
    		'erpTurnCLFBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入差旅费报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/feeplease/turnCLFBX.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('fp_id').value
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
    	    		    					var sob = localJson.sob;
    	    		    					if (sob) {
    	    		    						var code = localJson.code;
    	    		    						showMessage('提示','已成功抛转至'+sob+'账套,差旅费报销单编号: '+code);
    	    		    					} else {
    	    		    						var url = "jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
        	    		    					me.FormUtil.onAdd('FeePlease' + id, '差旅费报销单' + id, url);
    	    		    					}
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
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//客户编号、客户地址处理，实际是存在明细中
			var grid = Ext.getCmp('fp_cuname');
			var jsonGridData = new Array();		
			var tfnumber=0;
			var dd = new Object();
//			var count=grid.items.items.length/(grid.columns.length+2);
			var number=grid.columns.length+2;
			//验证明细的必填字段
			var items = grid.items.items;
			var count = 0;
			Ext.Array.each(items,function(i){
				if(i.gridlogic=='necessaryField'){
					if(i.value==null || i.value==''){
						count++;
					}
				}
			});
			if(count>0){
				showError('明细表有必填字段未完成填写,不能保存，请完善信息！');
				return false;
			}
			Ext.each(grid.items.items, function(item,index){
				if(item.index!=tfnumber){
					tfnumber=item.index;
					if(dd['fpd_d5']==''&&dd['fpd_d6']==''){
						dd = new Object();
					}else{
						if((index+1)%number==0){
							jsonGridData.push(Ext.JSON.encode(dd));
						}
					}			
				}				
				if(item.gridlogic!='ignore'){
					if(item.gridlogic=='mainField'){
						dd[item.name] =Ext.getCmp(form.keyField).value;
					}else if(item.gridlogic == 'detno'){
						dd[item.name] =item.index+1;
					}else if(item.xtype == 'numbercolumn'&&(item.value==null||item.value=='')){
						dd[item.name] =0;
					}else if(item.xtype=='datefield' || item.xtype=='datetimefield'){
						var date = new Date(item.value);
						dd[item.name] = item.value!=null?Ext.Date.format(date, 'Y-m-d H:i:s'):null
					}else if(item.name!='blank'){
						dd[item.name] = item.value; 
					}
				}
				f = form.down('#' + item.id);
				if(f){
					delete r[item.name];
				}
			});
			var param =jsonGridData;
			param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
			me.save(r, param);
		}else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(formvalue,param){
		var form=Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
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
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(param.toString().replace(/\\/g,"%"));
		params.caller=caller; 
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition+'&gridCondition=fpd_fpidIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=fpd_fpidIS'+value;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;

			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   		    	formCondition+'&gridCondition=fpd_fpidIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=fpd_fpidIS'+value;
				   		    }
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
		});
	},
	
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				//客户编号、客户地址处理，实际是存在明细中
				var grid = Ext.getCmp('fp_cuname');
				var jsonGridData = new Array();		
				var tfnumber=0;
				var dd = new Object();
//				var count=grid.items.items.length/(grid.columns.length+2);
				var items = grid.items.items;
				var count = 0;
				Ext.Array.each(items,function(i){
					if(i.gridlogic=='necessaryField'){
						if(i.value==null || i.value==''){
							count++;
						}
					}
				});
				if(count>0){
					showError('明细表有必填字段未完成填写,不能保存，请完善信息！');
					return false;
				}
				var number=grid.columns.length+2;
				Ext.each(grid.items.items, function(item,index){
					if(item.index!=tfnumber){
						tfnumber=item.index;
						if(dd['fpd_d5']==''&&dd['fpd_d6']==''){
							dd = new Object();
						}else{
							if((index+1)%number==0){
								jsonGridData.push(Ext.JSON.encode(dd));
							}
						}			
					}
					if(item.gridlogic!='ignore'){
						if(item.gridlogic=='mainField'){
							dd[item.name] =Ext.getCmp(form.keyField).value;
						}else if(item.gridlogic == 'detno'){
							dd[item.name] =item.index+1;
						}else if(item.xtype == 'numbercolumn'&&(item.value==null||item.value=='')){
							dd[item.name] =0;
						}else if(item.xtype=='datefield' || item.xtype=='datetimefield'){
							var date = new Date(item.value);
							dd[item.name] = item.value!=null?Ext.Date.format(date, 'Y-m-d H:i:s'):null
						}else if(item.name!='blank'){
							dd[item.name] = item.value; 
						}
					}
					f = form.down('#' + item.id);
					if(f){
						delete r[item.name];
					}
				});
				var param =jsonGridData;
				param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
				me.update(r, param);
			}else{
				me.FormUtil.checkForm();
			}
		
	},
	update:function(formvalue,param){
		var form=Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
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
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(param.toString().replace(/\\/g,"%"));
		params.caller=caller;
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				updateSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					updateSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;
			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   					formCondition;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   					formCondition;
				   		    }
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
	},
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
	    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	},	
	saveAddress:function(data){
		Ext.Ajax.request({
    	   url : basePath + 'oa/feeplease/saveOutAddress.action',
    	   params: {
    	    	formStore: data
    	   },
    	   method : 'post',
    	   callback : function(options,success,response){
    	   		var localJson = new Ext.decode(response.responseText);
    	    	if(localJson.exceptionInfo){
    	 			showError(localJson.exceptionInfo);
    	 		}
    	 		if(localJson.success){}
    	   }
		});
	},
	onTriggerClick: function(trigger){
		var me=this;
		var dbwin = new Ext.window.Window({
			id: 'dbwin',
			title: '查找',
			height: 560,
			width: 600,
			maximizable: false,
			buttonAlign: 'center',
			layout: 'anchor',
			items: [{
				tag: 'iframe',
				frame: true,
				anchor: '100% 100%',
				layout: 'fit',
				html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/hr/attendance/customerDbfind.jsp " height="100%" width="100%" frameborder="0" scrolling="auto""></iframe>'
			}],
			buttons: [{
				text: '确定',
				iconCls: 'x-button-icon-save',
				cls: 'x-btn-gray',
				handler: function() {
					var grid = Ext.getCmp('dbwin').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('resultList');
					var data=grid.selModel.lastSelected.data;
					var confrimWin=new Ext.window.Window({
				       	 width:340,
				       	 height:160,
				       	 bodyStyle: 'background:#f1f1f1;border-width:0',
				       	 id:'attwin',
				        title:'<center><h1>完善单位名称</h1></center>',
				       	 items:[{
								margin: '10 10 0 10',
								width:310,
								xtype: 'textfield',
								fieldLabel: '单位名称',
								id:'COMPANY',
								name:'COMPANY',
								value:data.MD_COMPANY
							},{
								margin: '10 10 0 10',
								width:310,
								style: 'font-size: 12px;',
								xtype: 'tbtext', text: '详细地址：'+ data.MD_ADDRESS
							}],
				       	 buttonAlign:'center',
				       	 buttons:[{text: '跳过此步',cls:'MyBtn',id:'MyBtn_jump',
							        handler: function() {	
							        Ext.getCmp(trigger.id).setValue(Ext.getCmp('COMPANY').value);
							        var fpd_d6 = Ext.getCmp("fpd_d6"+trigger.index);
							        if (fpd_d6) {
							        	Ext.getCmp("fpd_d6"+trigger.index).setValue(data.MD_ADDRESS);
							        }
							        Ext.getCmp('attwin').close();
							        Ext.getCmp('dbwin').close();  
							        }
							      },{text: '保存',cls:'MyBtn',id:'MyBtn_save',handler: function() {
							      		data.MD_COMPANY=Ext.getCmp('COMPANY').value;
							      		me.saveAddress(unescape(Ext.JSON.encode(data).replace(/\\/g,"%")));
							      		Ext.getCmp(trigger.id).setValue(Ext.getCmp('COMPANY').value);
							      		var fpd_d6 = Ext.getCmp("fpd_d6"+trigger.index);
							      		if (fpd_d6) {
							      			Ext.getCmp("fpd_d6"+trigger.index).setValue(data.MD_ADDRESS);
							      		}
								        Ext.getCmp('attwin').close();
								        Ext.getCmp('dbwin').close(); 
							      }}]
				    });
				    confrimWin.show() ;
				}
			},{
				text: '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler: function() {
					Ext.getCmp('dbwin').close();
				}
			}]
		});
		dbwin.show();
    },
    hidecolumns: function(combo,isNoClean){
    	if(caller=='FeePlease!CCSQ!new'){
    		var index = combo.index;
    		var value = combo.value;
    		var form = Ext.getCmp('form');
    		if(value == '飞机票'){
    			form.down('#fpd_train_destination'+index).hide();
				form.down('#fpd_train_starting'+index).hide();
				form.down('#fpd_hotel_city'+index).hide();
				form.down('#fpd_hotel_address'+index).hide();
				form.down('#fpd_business_name'+index).hide();
				form.down('#fpd_air_starting'+index).show();
				form.down('#fpd_air_destination'+index).show();
				form.down('#fpd_flight_code'+index).show();
				form.down('#fpd_level'+index).show();
				form.down('#fpd_seat'+index).show();
				if(!isNoClean){
					form.down('#fpd_train_destination'+index).setValue('');
					form.down('#fpd_train_starting'+index).setValue('');
					form.down('#fpd_hotel_city'+index).setValue('');
					form.down('#fpd_hotel_address'+index).setValue('');
					form.down('#fpd_business_name'+index).setValue('');
					form.down('#fpd_air_starting'+index).setValue('');
					form.down('#fpd_air_destination'+index).setValue('');
					form.down('#fpd_flight_code'+index).setValue('');
					form.down('#fpd_seat'+index).setValue('');
					form.down('#fpd_level'+index).setValue('');
				}
    		}else if(value == '火车票'){
    			form.down('#fpd_train_destination'+index).show();
				form.down('#fpd_train_starting'+index).show();
				form.down('#fpd_hotel_city'+index).hide();
				form.down('#fpd_hotel_address'+index).hide();
				form.down('#fpd_business_name'+index).hide();
				form.down('#fpd_air_starting'+index).hide();
				form.down('#fpd_air_destination'+index).hide();
				form.down('#fpd_level'+index).show();
				form.down('#fpd_seat'+index).show();
				if(!isNoClean){
					form.down('#fpd_train_destination'+index).setValue('');
					form.down('#fpd_train_starting'+index).setValue('');
					form.down('#fpd_hotel_city'+index).setValue('');
					form.down('#fpd_hotel_address'+index).setValue('');
					form.down('#fpd_business_name'+index).setValue('');
					form.down('#fpd_air_starting'+index).setValue('');
					form.down('#fpd_air_destination'+index).setValue('');
					form.down('#fpd_seat'+index).setValue('');
					form.down('#fpd_level'+index).setValue('');
				}
    		}else if(value == '住宿'){
    			form.down('#fpd_train_destination'+index).hide();
				form.down('#fpd_train_starting'+index).hide();
				form.down('#fpd_hotel_city'+index).show();
				form.down('#fpd_hotel_address'+index).show();
				form.down('#fpd_business_name'+index).show();
				form.down('#fpd_air_starting'+index).hide();
				form.down('#fpd_air_destination'+index).hide();
				form.down('#fpd_seat'+index).hide();
				form.down('#fpd_level'+index).hide();
				if(!isNoClean){
					form.down('#fpd_train_destination'+index).setValue('');
					form.down('#fpd_train_starting'+index).setValue('');
					form.down('#fpd_hotel_city'+index).setValue('');
					form.down('#fpd_hotel_address'+index).setValue('');
					form.down('#fpd_business_name'+index).setValue('');
					form.down('#fpd_air_starting'+index).setValue('');
					form.down('#fpd_air_destination'+index).setValue('');
					form.down('#fpd_seat'+index).setValue('');
					form.down('#fpd_level'+index).setValue('');
				}
    		}
    	}
    }
});