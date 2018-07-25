Ext.QuickTips.init();
Ext.define('erp.controller.oa.fee.feePlease', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.fee.feePlease','oa.fee.feePleaseFYBX','oa.fee.FeeBackGrid',
    			'core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    			'core.form.MultiField','core.form.FileField','core.form.SeparNumber',
    			'core.form.DateHourMinuteField','core.form.ConDateHourMinuteField','core.form.CheckBoxGroup','core.form.DateHourMinuteComboField',
    		'core.button.Scan','core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  				'core.button.ResSubmit','core.button.TurnCLFBX','core.button.TurnFYBX','core.button.TurnYHFKSQ','core.button.VoCreate',
  				'core.button.TurnYWZDBX','core.button.End','core.button.ResEnd','core.button.Confirm','core.button.PrintByCondition',
  				'core.button.TurnBankRegister','core.button.TurnBillAP','core.button.TurnBillARChange',
  				'erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
  	      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','core.form.CascadingCityField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','common.datalist.Toolbar','core.trigger.BankNameTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(record.data.fpd_id != 0 && record.data.fpd_id != null && record.data.fpd_id != ''){
						var btn = Ext.getCmp('factdays');
						btn && btn.setDisabled(false);
					}   								
					this.onGridItemClick(selModel, record);
    			}
    		},
    		'mfilefield':{
    			beforerender:function(f){
    				if(caller=='FeePlease!ZWSQ'){
    					f.readOnly=false;
    				}
    			}
    		},
    		'FeeBackGrid': { 
    			itemclick: this.onGridItemClick2
    		},
    		'erpFormPanel' : {
    			afterload : function(form) {
    				this.hidecolumns(true);
				}
    		},
    		'field[name=fp_v11]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
    		'dbfindtrigger[name=fp_v1]': {
    			afterrender:function(trigger){
    				if(caller=='FeePlease!JKSQ'){
	    			trigger.dbKey='fp_department';
	    			trigger.mappingKey='fcs_departmentname';
	    			trigger.dbMessage='请先选择申请部门';}
    			}
    		},
    		'dbfindtrigger[name=fpd_d1]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='fp_department';
	    			trigger.mappingKey='fcs_departmentname';
	    			trigger.dbMessage='请先选择报销部门';
    			}
    		},
    		'dbfindtrigger[name=fp_pleaseman]':{
    			aftertrigger:function(trigger,record){
    				Ext.Ajax.request({
    					url:basePath+'oa/fee/getFeeAccount.action',
    					method:'POST',
    					params:{
    						emcode:trigger.value
    					},
    					async:false,
    					callback:function(opts,success,res){
    						var r = new Ext.decode(res.responseText);
    						trigger.up('form').getForm().setValues(r.info);
    					}
    					
    				});
    			}
    		},
    		'dbfindtrigger[name=fb_jksqcode]': {
  			   	focus: function(trigger){
				   	if(caller=='FeePlease!FYBX'){
		    			trigger.dbKey='fp_pleasemancode';
		    			trigger.mappingKey='fp_pleasemancode';
		    			trigger.dbMessage='请先选择报销人';
				   	}
  			   }
            },
            'dbfindtrigger[name=fp_sourcecode]': {
    			beforetrigger:function(trigger){ 
    				if(caller=='FeePlease!HKSQ'){
	    			trigger.dbKey='fp_pleasemancode';
	    			trigger.mappingKey='fp_pleasemancode';
	    			trigger.dbMessage='请先选择申请人';
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					if(caller=='FeePlease!CLFBX'){
    						me.BaseUtil.getRandomNumber('FeePlease!FYBX');//差旅费报销取费用报销同样的caller取编号
    					}else{
    						me.BaseUtil.getRandomNumber(caller);//自动添加编号
    					}
    					
    				}
    				if(caller == 'FeePlease!CZCFSQ'|| caller == 'FeePlease!FYBX'){
    					this.getamount();
    				}
    				if(caller == 'FeePlease!CCSQ'){
    					this.getTotal();
    					if(Ext.getCmp('fp_prestartdate')!=null && Ext.getCmp('fp_preenddate')!=null){
	    					var startField = Ext.getCmp('fp_prestartdate');
	    					var start = null;
	    					if(startField.xtype=='condatehourminutefield'){
	    						start = new Date(Ext.getCmp('fp_prestartdate').items.items[5].value);
	    					}else{
		    					start=new Date(Ext.getCmp('fp_prestartdate').value);
	    					}
	        				var end=new Date(Ext.getCmp('fp_preenddate').value);
	        				if(start-end>0){
	        					showError('预计时间跨度输入有误，请确认后重新输入');
	        				}
	        				else{
	        					this.beforeSave();
	        				}
        				}else{
        					this.beforeSave();
        			}
    				}else if(caller == 'FeePlease!JKSQ'){
    					if(Ext.getCmp('fp_startdate')!=null && Ext.getCmp('fp_enddate')!=null){
    					var start=new Date(Ext.getCmp('fp_startdate').value);
        				var end=new Date(Ext.getCmp('fp_enddate').value);
        				if(start-end>0){
        					showError('借款日期不能早于还款日期，请确认后重新输入');
        				}else{
        					this.beforeSave();
        				}
        			}
        			else{
        					this.beforeSave();
        				}
    				}else{
    					this.beforeSave();
    				}
					
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				if(caller!='FeePlease!FYBX'){
    					me.FormUtil.onAdd('add' + caller, '新增单据', "jsps/oa/fee/feePlease.jsp?whoami=" + caller);
    				}else{
    					me.FormUtil.onAdd('add' + caller, '新增费用申请', "jsps/oa/fee/feePleaseFYBX.jsp?whoami=" + caller);
    				}
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				if(caller == 'FeePlease!CZCFSQ'){
    					this.getamount();
    				}
    				if(caller == 'FeePlease!CCSQ'){
    					this.getTotal();
	    				if(Ext.getCmp('fp_prestartdate')!=null && Ext.getCmp('fp_preenddate')!=null){
	    					var startField = Ext.getCmp('fp_prestartdate');
	    					var start = null;
	    					if(startField.xtype=='condatehourminutefield'){
	    						start = new Date(Ext.getCmp('fp_prestartdate').items.items[5].value);
	    					}else{
		    					start=new Date(Ext.getCmp('fp_prestartdate').value);
	    					}
	        				var end=new Date(Ext.getCmp('fp_preenddate').value);
	        				if(start-end>0){
	        					showError('预计时间跨度输入有误，请确认后重新输入');
	        				}else{
	        					this.beforeUpdate();
	        				}        				
	        			}else{
        					this.beforeUpdate();
        				} 
    				}else if(caller == 'FeePlease!JKSQ'){
    					if(Ext.getCmp('fp_startdate')!=null && Ext.getCmp('fp_enddate')!=null){
    					var start=new Date(Ext.getCmp('fp_startdate').value);
        				var end=new Date(Ext.getCmp('fp_enddate').value);
        				if(start-end>0){
        					showError('借款日期不能早于还款日期，请确认后重新输入');
        				}else{
        					this.beforeUpdate();
        				}
        			}else{
        					this.beforeUpdate();
        				}
    				}else{
    					this.beforeUpdate();
    				}
    				
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
    				if(caller == 'FeePlease!CCSQ'){
    					this.getTotal();
    				}
    				if(caller == 'FeePlease!JKSQ'){
    					if(Ext.getCmp('fp_startdate')!=null && Ext.getCmp('fp_enddate')!=null){
    					var start=new Date(Ext.getCmp('fp_startdate').value);
        				var end=new Date(Ext.getCmp('fp_enddate').value);
        				if(start-end>0){
        					showError('借款日期不能早于还款日期，请确认后重新输入');
        				}else{
        					this.FormUtil.onSubmit(Ext.getCmp('fp_id').value);
        				}      				
        			}else{
        					this.FormUtil.onSubmit(Ext.getCmp('fp_id').value);
        				}
    				}else{
    					this.FormUtil.onSubmit(Ext.getCmp('fp_id').value);
    				}
    				
    				
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
    			var reportName='';
    			var kind=Ext.getCmp('fp_kind').value;
    			if(kind=="借款申请单"){
    				reportName="AccountRegZW2";
    			}else if(kind=="费用报销单"){
    				reportName="AccountRegZW_fybx";
    			}else if(kind=="差旅费报销单"){
    				reportName="AccountRegZW_clfbx";
    			}else if(kind=="还款申请单"){
    				reportName="AccountRegZW_hksq";
    			}else{
        		    reportName="AccountRegZW";
    			}

				var condition='{FeePlease.fp_id}='+Ext.getCmp('fp_id').value+'';
				var id=Ext.getCmp('fp_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},
    		'field[name=fp_n3]': {
	   			change:function(f){
	 				   if(caller=='FeePlease!ZWSQ'){
	 					   Ext.getCmp('fp_pleaseamount').setValue((Ext.getCmp('fp_n3').value-0)+(Ext.getCmp('fp_n4').value-0));;
	 				   }
	 			   }
	   		},
	   		'field[name=fp_n4]': {
	   			change:function(f){
	   				if(caller=='FeePlease!ZWSQ'){
	 					   Ext.getCmp('fp_pleaseamount').setValue((Ext.getCmp('fp_n3').value-0)+(Ext.getCmp('fp_n4').value-0));;
	 				   }
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
    		'combo[name=fp_object]': {
    			delay: 200,
    			change: function(m){
					this.hidecolumns(false);
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
    		'erpConfirmButton': {afterrender: function(btn){
				var statu = Ext.getCmp('fp_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('fp_id').value);
    				
    			}
    		} ,
    		'erpVoCreateButton':{//业务招待费转费用报销
    			beforerender:function(btn){
    				btn.setWidth(100);
    				btn.setText("转费用报销");
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				var turnStatus=Ext.getCmp('fp_v11');
    				if(status && status.value != 'AUDITED'&&turnStatus||turnStatus.value!='未转费用申请'){
    					btn.hide();
    				}
    			},
    			click: {
    				fn:function(btn){
    					this.turnFYBX(me);
        			},
        			lock:2000
    			}
    		},
    		//转银行登记
    		'erpTurnBankRegisterButton':{
    			click: {
    				fn:function(btn){
    					me.turnBankRegister();
        			},
        			lock:2000
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		//应付票据付款
    		'erpTurnBillAPButton':{
    			click: {
    				fn:function(btn){
    					me.turnBillAP();
        			},
        			lock:2000
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		//应收票据背书转让
    		'erpTurnBillARChangeButton':{
    			click: {
    				fn:function(btn){
    					me.turnBillARChange();
        			},
        			lock:2000
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		/**
    		 * 更改实际天数
    		 */
    		'#factdays': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.UpdateFactdays(record);
    			}
    		},
    		'field[name=fp_v11]':{
    			beforerender:function(field){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value == 'AUDITED'){
    					field.readOnly=false;
    				}
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
    		'field[name=fp_n2]':{
    			beforerender:function(field){
    				if(caller!='FeePlease!CCSQ'){
	    				var status = Ext.getCmp('fp_statuscode');
	    				if(status && status.value == 'AUDITED'){
	    					field.readOnly=false;
	    					var back=0;
	    					if(Ext.getCmp('fp_n6')&&Ext.getCmp('fp_n6').value!=''){
	    						back=Ext.getCmp('fp_n6').value;
	    					}
	    					field.setValue((Ext.getCmp('fp_pleaseamount').value-back-Ext.getCmp('fp_n1').value).toFixed(3));
	    				}
    				}
    			}
    		},
    		'dbfindtrigger[name=fp_kind]': {
    			afterrender:function(trigger){
    				if(caller == 'FeePlease!JKSQ'){
    					trigger.dbKey='fp_department';
    	    			trigger.mappingKey='fcs_departmentname';
    	    			trigger.dbMessage='请先选择申请部门！';
    				}
    			}
    		},
    		'field[name=fp_sourcecode]': {
 			   afterrender:function(f){
 				   if(caller=='FeePlease!CLFBX'|| caller=='FeePlease!FYBX'||caller=='FeePlease!HKSQ'){
 					   if(Ext.getCmp('fp_sourcecode').value!=""){
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
    		},
    		'erpTurnFYBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入费用报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/feeplease/turnFYBX.action',
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
    	    		    					var url = "jsps/oa/fee/feePlease.jsp?whoami=FeePlease!FYBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
    	    		    					me.FormUtil.onAdd('FeePlease' + id, '费用报销单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpTurnYHFKSQButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入银行付款申请单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/feeplease/turnYHFKSQ.action',
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
    	    		    					var url = "jsps/oa/fee/feePlease.jsp?whoami=FeePlease!YHFKSQ&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
    	    		    					me.FormUtil.onAdd('FeePlease' + id, '银行付款申请单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpTurnYWZDBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入业务招待费报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/feeplease/turnYWZDBX.action',
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
    	    		    					var url = "jsps/oa/fee/feePlease.jsp?whoami=FeePlease!YWZDBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
    	    		    					me.FormUtil.onAdd('FeePlease' + id, '业务招待费报销单' + id, url);
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
    getTotal:function(){
    	var carfare=Ext.getCmp('fp_n2')!=null?(Ext.getCmp('fp_n2').value=="" ? 0 : Ext.getCmp('fp_n2').value-0):0;//交通费
    	var accommodation=Ext.getCmp('fp_n3')!=null?(Ext.getCmp('fp_n3').value=="" ? 0 : Ext.getCmp('fp_n3').value-0):0;//住宿费
    	var prfee=Ext.getCmp('fp_n4')!=null?(Ext.getCmp('fp_n4').value=="" ? 0 : Ext.getCmp('fp_n4').value-0):0;//公关费
    	if(Ext.getCmp('fp_pleaseamount')!=null)
    	Ext.getCmp('fp_pleaseamount').setValue(carfare+accommodation+prfee);
    },
    getamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		Ext.each(items,function(item,index){
			if(item.data['fpd_date1']!=null&&item.data['fpd_date1']!=""){
				amount= amount + Number(item.data['fpd_total']);
			}
		});
		Ext.getCmp('fp_pleaseamount').setValue(amount);
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onConfirm: function(id){
		var form = Ext.getCmp('form');	
		Ext.Ajax.request({
	   		url : basePath + form.confirmUrl,
	   		params: {
	   			id: id,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			//me.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				//audit成功后刷新页面进入可编辑的页面 
    				//auditSuccess(function(){
	   					window.location.reload();
	   				//});    				
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", '确认成功');
    	   					//auditSuccess(function(){
    	   						window.location.reload();
    	   					//});
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	},
	openRelative:function(e, el, obj){
		var f = obj.scope;
		var name='';
		var cal='';
		var field=''
		var conField=''
		var type=Ext.getCmp('fp_sourcekind').value;
		if(type=='客户拜访记录'){
			name='VisitRecord';
			cal='visitrecord';
			field='vr_id';
			conField='vr_class=\'OfficeClerk\' and vr_code';
		}else if(type=='原厂拜访记录'){
			name='VisitRecord';
			cal='visitrecord';
			field='vr_id';
			conField='vr_class=\'VisitRecord!Vender\' and vr_code';
		}else if(type=='业务招待费申请单'){
			name='FeePlease';
			cal='FeePlease';
			field='fp_id';
			conField='fp_kind=\'业务招待费申请单\' and fp_code';
		}else if(type=='出差申请单'){
			name='FeePlease';
			cal='FeePlease';
			field='fp_id';
			conField='fp_kind=\'出差申请单\' and fp_code';
		}else if(type=='资源开发记录'){
			name='VisitRecord';
			cal='visitrecord';
			field='vr_id';
			conField='vr_class=\'VisitRecord!Resource\' and vr_code';
		}else if(type=='借款申请单'){
			name='FeePlease';
			cal='FeePlease';
			field='fp_id';
			conField='fp_kind=\'借款申请单\' and fp_code';
		}
		else if(type=='费用申请'){
			name='PreFeePlease';
			cal='PreFeePlease';
			field='fp_id';
			conField=' fp_code';
		}else if(type=='资产维修'){
			name='Propertyrepair';
			cal='Propertyrepair';
			field='pr_id';
			conField=' pr_code';
		}else if(type=='车辆维修'){
			name='vehiclearchivesdetail';
			cal='vehiclearchivesdetail';
			field='vd_id';
			conField=' vd_code';
		}
		if(type=='市场调研立项'||type=='客户拜访记录' || type=='原厂拜访记录' || type=='业务招待费申请单'||type=='出差申请单'||type=='资源开发记录'||type=='借款申请单'||type=='费用申请'||type=='资产维修'||type=='车辆维修') {
			var code = Ext.getCmp('fp_sourcecode').value;
			if(code.indexOf('(')>-1){//来自其他的单据
				this.turnOtherSob(code,field,conField,cal);
			}else{
				var url=this.getRelativeUrl(code,field,conField,cal);
				this.FormUtil.onAdd(name+code, Ext.getCmp('fp_sourcekind').value, 
						url);
			}
			
		}
	},
	getRelativeUrl:function(code,field,conField,cal){
		var id = 0;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: cal,
	   			field: field,
	   			condition: conField+'=\'' + code + '\''
	   		},
	   		method : 'post',
	   		callback : function(o, s, r){
	   			var rs = new Ext.decode(r.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return;
	   			}
    			if(rs.success){
    				if(rs.data != null){
    					id = rs.data;
    				}
	   			}
	   		}
		});
		var url='#';
		var type=Ext.getCmp('fp_sourcekind').value;
		if(type=='客户拜访记录'){
			url='jsps/crm/customermgr/customervisit/visitRecord.jsp?formCondition=vr_idIS'+id+'&gridCondition=vrd_vridIS'+id;
		}else if(type=='原厂拜访记录'){
			url='jsps/crm/customermgr/customervisit/visitRecord3.jsp?formCondition=vr_idIS'+id+'&gridCondition=vrd_vridIS'+id;
		}else if(type=='业务招待费申请单'){
			url='jsps/oa/fee/feePlease.jsp?whoami=FeePlease!YWZDSQ&formCondition=fp_idIS'+id+'&gridCondition=fpd_fpidIS'+id;
		}else if(type=='出差申请单'){
			url='jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CCSQ&formCondition=fp_idIS'+id+'&gridCondition=fpd_fpidIS'+id;
		}else if(type=='资源开发记录'){
			url='jsps/crm/customermgr/customervisit/visitRecord4.jsp?formCondition=vr_idIS'+id+'&gridCondition=vrd_vridIS'+id;
		}else if(type=='借款申请单'){
			url='jsps/oa/fee/feePlease.jsp?whoami=FeePlease!JKSQ&formCondition=fp_idIS'+id+'&gridCondition=fpd_fpidIS'+id;
		}else if(type=='费用申请'){
			url='jsps/oa/fee/preFeePlease.jsp?formCondition=fp_idIS'+id+'&gridCondition=fpd_fpidIS'+id;
		}else if(type=='资产维修'){
			url='jsps/oa/storage/propertyrepair.jsp?formCondition=pr_idIS'+id+'&gridCondition=prd_pridIS'+id;
		}else if(type=='车辆维修'){
			url='jsps/oa/vehicle/vehiclemaintain.jsp?formCondition=vd_idIS'+id;
		}
		return url;
	},
	turnOtherSob:function(code,field,conField,cal){
		var newSob=null;//code形如:2015040052(资料中心) 2016010052((华商龙)资料中心)
		Ext.Ajax.request({//去资料中心寻找括号内的帐套
	   		url : basePath + 'oa/feeplease/getFromSob.action',
	   		async: false,
	   		params: {
	   			condition: 'ma_function=\'' + code.substring((code.lastIndexOf('(')+1),code.lastIndexOf(')')) + '\''
	   		},
	   		method : 'post',
	   		callback : function(o, s, r){
	   			var rs = new Ext.decode(r.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return;
	   			}
    			if(rs.success){
    				if(rs.sob != null){
    					newSob = rs.sob;
    				}
	   			}
	   		}
		});
		if(newSob==null){
			showError('没有找到要转的帐套!');
			return;
		}else{
			var url=this.getRelativeUrl(code.split('(')[0],field,conField,newSob+'.'+cal);
			var currentMaster = parent.sob?parent.sob:parent.parent.sob;
			url=parent.location.href.split('jsps')[0]+url;//防止因为父页面url的改变,而找不到页面
			Ext.Ajax.request({
				url: basePath + 'common/changeMaster.action',
				params: {
					to: newSob
				},
				callback: function(opt, s, r) {
					if (s) {
					 	var win = parent.Ext.create('Ext.Window', {
			    			width: '100%',
			    			height: '100%',
			    			draggable: false,
			    			closable: false,
			    			modal: true,
			    			id:'modalwindow',
			    			historyMaster:currentMaster,
			    			title: '创建到帐套' + code.substring(code.lastIndexOf('('),code.lastIndexOf(')')) + '的临时会话',
			    			html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
			    			buttonAlign: 'center',
			    			buttons: [{
								text: $I18N.common.button.erpCloseButton,
								cls: 'x-btn-blue',
								id: 'close',
								handler: function(b) {
									Ext.Ajax.request({
										url: basePath + 'common/changeMaster.action',
										params: {
											to: currentMaster
										},
										callback: function(opt, s, r) {
											if (s) {
												b.up('window').close();
											} else {
												alert('切换到原帐套失败!');
											}
										}
									});
								}
							}]
			    		});
						win.show();
					} else {
						alert('无法创建到帐套' + code.substring(code.lastIndexOf('('),code.lastIndexOf(')')) + '的临时会话!');
					}
				}
			});
		}
	},
	turnFYBX:function(me){
		warnMsg("确定要转入费用报销单吗?", function(btn){
			if(btn == 'yes'){
				me.FormUtil.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'oa/feeplease/turnFYBX2.action',
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
		    				//turnSuccess(function(){
		    				showMessage("提示", '转入成功');
		    				window.location.reload();
		    				var id = localJson.id;
		    				var url = "jsps/oa/fee/feePlease.jsp?whoami=FeePlease!FYBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
		    				me.FormUtil.onAdd('FeePlease' + id, '费用报销单' + id, url);
		    				//});
		    				
			   			}
			   		}
				});
			}
		});
	},
	turnBankRegister:function(){
		var form = Ext.getCmp('form');
		var catecode = Ext.getCmp('fp_v11').value;
		if(catecode == null || catecode == ''){
	        showMessage("警告", '请填写需要转银行登记的付款方信息!');
	        return;
	    }
		var thispayamount=form.BaseUtil.numberFormat(Ext.getCmp('fp_n2').value,2);
		var back=0;//还款金额
		if(Ext.getCmp('fp_n6')&&Ext.getCmp('fp_n6').value!=null&&Ext.getCmp('fp_n6').value!=''){
			back=form.BaseUtil.numberFormat(Ext.getCmp('fp_n6').value,2);
		}
		if(form.BaseUtil.numberFormat(Ext.getCmp('fp_n1').value+thispayamount,2) > form.BaseUtil.numberFormat(Ext.getCmp('fp_pleaseamount').value-back,2)){
			showMessage("警告", '本次转金额超出剩余金额!');
	        return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'oa/fee/turnBankRegister.action',
	   		params: {
	   			id:Ext.getCmp("fp_id").value,
	   			paymentcode:Ext.getCmp('fp_v11').value,
	   			payment:Ext.getCmp('fp_v10').value,
	   			thispayamount:Ext.getCmp('fp_n2').value,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
	   		}
		});  
	},
	turnBillAP: function(){
		var form = Ext.getCmp('form');
		var catecode = Ext.getCmp('fp_v11').value;
		if(catecode == null || catecode == ''){
	        showMessage("警告", '请填写需要转应付票据的付款方信息!');
	        return;
	    }
		var thispayamount=form.BaseUtil.numberFormat(Ext.getCmp('fp_n2').value,2);
		var back=0;//还款金额
		if(Ext.getCmp('fp_n6')&&Ext.getCmp('fp_n6').value!=null&&Ext.getCmp('fp_n6').value!=''){
			back=form.BaseUtil.numberFormat(Ext.getCmp('fp_n6').value,2);
		}
		if(form.BaseUtil.numberFormat(Ext.getCmp('fp_n1').value+thispayamount,2) > form.BaseUtil.numberFormat(Ext.getCmp('fp_pleaseamount').value-back,2)){
			showMessage("警告", '本次转金额超出剩余金额!');
	        return;
		}
		if(thispayamount == null || thispayamount=='' || thispayamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'oa/fee/turnBillAP.action',
	   		params: {
	   			id:Ext.getCmp("fp_id").value,
	   			paymentcode:Ext.getCmp('fp_v11').value,
	   			payment:Ext.getCmp('fp_v10').value,
	   			thispayamount:Ext.getCmp('fp_n2').value,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
	   		}
		});
    },
    turnBillARChange: function(){
    	var form = Ext.getCmp('form');
		var catecode = Ext.getCmp('fp_v11').value;
		if(catecode == null || catecode == ''){
	        showMessage("警告", '请填写需要转应付票据的付款方信息!');
	        return;
	    }
		var thispayamount=form.BaseUtil.numberFormat(Ext.getCmp('fp_n2').value,2);
		var back=0;//还款金额
		if(Ext.getCmp('fp_n6')&&Ext.getCmp('fp_n6').value!=null&&Ext.getCmp('fp_n6').value!=''){
			back=form.BaseUtil.numberFormat(Ext.getCmp('fp_n6').value,2);
		}
		if(form.BaseUtil.numberFormat(Ext.getCmp('fp_n1').value+thispayamount,2) > form.BaseUtil.numberFormat(Ext.getCmp('fp_pleaseamount').value-back,2)){
			showMessage("警告", '本次转金额超出剩余金额!');
	        return;
		}
		if(thispayamount == null || thispayamount=='' || thispayamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'oa/fee/turnBillARChange.action',
	   		params: {
	   			id:Ext.getCmp("fp_id").value,
	   			paymentcode:Ext.getCmp('fp_v11').value,
	   			payment:Ext.getCmp('fp_v10').value,
	   			thispayamount:Ext.getCmp('fp_n2').value,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
	   		}
		});
    },
	UpdateFactdays:function(record){
		var win = this.factdayswindow;
		if (!win) {
			win = this.getFactdaysWindow();
		}
		win.show();
	},
	getFactdaysWindow : function() {
		var me = this;
		return Ext.create('Ext.window.Window',{
			width: 330,
	       	height: 180,
	       	closeAction: 'hide',
	       	cls: 'custom-blue',
	       	title:'<h1>更改实际天数</h1>',
	       	layout: {
	       		type: 'vbox'
	       	},
	       	items:[{
	        	 margin: '5 0 0 5',
	       		 xtype:'numberfield',
	       		 fieldLabel:'实际天数',
	       	     name:'factdays',
	       	     id:'factdays'
	       	 },{
	       		margin: '5 0 0 5',
	                xtype: 'fieldcontainer',
	                fieldLabel: '全部更新',
	                combineErrors: false,
	                defaults: {
	                    hideLabel: true
	                },
	                layout: {
	                    type: 'column',
	                    defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
	                },
	                items: [{
	                    xtype:'checkbox',
	                    columnidth: 0.4,
	                    fieldLabel:'全部更新',
	                    name:'allupdate',
	                    id:'allupdate'
	           	 },{
	           		 xtype:'displayfield',
	           		 fieldStyle:'color:red',
	           		 columnidth: 0.6,
	           		 value:'  *更改当前所有明细'
	           	 }]
	         }],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				text:'保存',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){
	 					var w = btn.up('window');
	 					me.saveFactdays(w);
	 					w.hide();
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'关闭',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					btn.up('window').hide();
	 				}
	 			}]
	        });
	},
	saveFactdays: function(w) {
		var factdays = w.down('field[name=factdays]').getValue(),
			grid = Ext.getCmp('grid'),
			record = grid.getSelectionModel().getLastSelected(); 
		if(!factdays) {
			showError('请先设置预计天数.') ;  
			return;
		} else {
			var allupdate = w.down('field[name=allupdate]').getValue();
			var dd = {
					fpd_id : record.data.fpd_id,
					fpd_fpid : record.data.fpd_fpid,
					factdays : factdays? factdays : record.data.fpd_n5,
					allupdate : allupdate ? 1 : 0
			};
			Ext.Ajax.request({
				url : basePath +'oa/fee/updatefactdays.action',
				params : {
					data: unescape(Ext.JSON.encode(dd))
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.success){
						grid.GridUtil.loadNewStore(grid,{
							 caller:'FeePlease!CCSQ',
							 condition: 'fpd_fpid=' + record.data.fpd_fpid
						});
		   			} else if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else{
		   				saveFailure();
		   			}
				}
			});
		}
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(caller=='FeePlease!FYBX'&&Ext.getCmp('fp_object')){
			var obj = Ext.getCmp("fp_object").value;
			if(obj == '供应商' && (Ext.getCmp('fp_vendcode').value==null||Ext.getCmp('fp_vendcode').value=='')){
				showError('供应商不能为空！');//对象为供应商时，供应商编号不能为空
				return;
			}
			if(obj == '客户' && (Ext.getCmp('fp_cucode').value==null||Ext.getCmp('fp_cucode').value=='')){
				showError('供应商不能为空！');//对象为客户时，客户编号不能为空
				return;
			}
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');		
		var detail2 = Ext.getCmp('FeeBackGrid');
		Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});
		var grids = new Array();
		grids.push(detail);
		var param = new Array();
		param.push(me.GridUtil.getGridStore(detail));
		if(detail2) {
			param.push(me.GridUtil.getGridStore(detail2));
			//grids.push(detail2);  //现在只判断第一个表，需要判断第二个表就放出来
		}else{
			param.push(new Array());
		}
		//lidy 2017110394   费用报销单保存时添加提示 明细表必填字段未完成填写
		var errInfo = new Array();
		for(var i = 0 ; i < grids.length ; i ++){	
			if(grids[i].necessaryField&&grids[i].necessaryField.length > 0 && (param[i] == null || param[i] == '' || param[i] == '[]')){
				errInfo.push(me.GridUtil.getUnFinish(grids[i]));
				var len = errInfo.length-1;
				if(errInfo[len].length > 0)
					errInfo[len] = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
				else
					errInfo[len] = '明细表还未添加数据, 是否继续?';
			}else if(grids[i].necessaryField&&grids[i].necessaryField.length > 0 && !(param[i] == null || param[i] == '' || param[i].length == 0)){
				errInfo.push(me.GridUtil.getUnFinish(grids[i]));
				var len = errInfo.length-1;
				if(errInfo[len].length > 0){
					errInfo[len] = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
				}else{
					errInfo.pop();
				}
			}
		}
		param[0] = param[0] == null ? [] : "[" + param[0].toString().replace(/\\/g,"%") + "]";
		param[1] = param[1] == null ? [] : "[" + param[1].toString().replace(/\\/g,"%") + "]";
		if(errInfo.length>0){
			warnMsg(errInfo, function(btn){
				if(btn == 'yes'){
					me.beforeSaveCheck(param);
				} else {
					return;
				}
			});
		}else{
			me.beforeSaveCheck(param);
		}
	},
	beforeSaveCheck: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var param = arguments[0];
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			me.save(r, param[0], param[1]);
		}else{
			me.FormUtil.checkForm();
		}
	},
	save: function(){
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
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.caller=caller;
		/*for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}*/  
		var me = this;
		var form = Ext.getCmp('form');
		//me.getActiveTab().setLoading(true);//loading...
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
		if(caller=='FeePlease!FYBX'&&Ext.getCmp('fp_object')){
			var obj = Ext.getCmp("fp_object").value;
			if(obj == '供应商' && (Ext.getCmp('fp_vendcode').value==null||Ext.getCmp('fp_vendcode').value=='')){
				showError('供应商不能为空！');//对象为供应商时，供应商编号不能为空
				return;
			}
			if(obj == '客户' && (Ext.getCmp('fp_cucode').value==null||Ext.getCmp('fp_cucode').value=='')){
				showError('客户不能为空！');//对象为客户时，客户编号不能为空
				return;
			}
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');		
		var detail2 = Ext.getCmp('FeeBackGrid');
		Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});
		var grids = new Array();
		grids.push(detail);
		var param = new Array();
		param.push(me.GridUtil.getGridStore(detail));
		if(detail2) {
			param.push(me.GridUtil.getGridStore(detail2));
			//grids.push(detail2);  //现在只判断第一个表，需要判断第二个表就放出来
		}else{
			param.push(new Array());
		}
		//lidy 2017110394   费用报销单保存时添加提示 明细表必填字段未完成填写
		var errInfo = new Array();
		for(var i = 0 ; i < grids.length ; i ++){	
			if(grids[i].columns.length > 0 && !grids[i].ignore){
				if(grids[i].GridUtil.isEmpty(grids[i])) {
					errInfo.push('明细还未录入数据,是否继续保存?');
				} else if(grids[i].GridUtil.isDirty(grids[i])) {
					if(grids[i].necessaryField && grids[i].necessaryField.length > 0 && (param[i] == null || param[i].length == 0 || param[i] == '')){
						errInfo.push(me.GridUtil.getUnFinish(grids[i]));
						var len = errInfo.length-1;
						if(errInfo[len].length > 0){
							errInfo[len] = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
						}else{
							return;																
						}
					}
				}
			}
		}
		param[0] = param[0] == null ? [] : "[" + param[0].toString().replace(/\\/g,"%") + "]";
		param[1] = param[1] == null ? [] : "[" + param[1].toString().replace(/\\/g,"%") + "]";
		if(errInfo.length>0){
			warnMsg(errInfo, function(btn){
				if(btn == 'yes' || btn == 'ok'){
					me.beforeupdateCheck(param);
				} else {
					return;
				}
			});
		}else{
			me.beforeupdateCheck(param);
		}
	},
	beforeupdateCheck:function(){
		var me = this;
		var form = Ext.getCmp('form');
		var param = arguments[0];
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			me.update(r, param[0], param[1]);
		}else{
			me.FormUtil.checkForm();
		}
	},
	update:function(){
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
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.caller=caller;
		var me = this;
		var form = Ext.getCmp('form');
		//me.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			//me.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
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
	   					saveSuccess(function(){
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
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	onGridItemClick2: function(selModel,record){
    	this.onGridItemClick(selModel,record,'FeeBackGrid');
    	
    },
    hidecolumns: function(isNoClean){
    	if(caller=='FeePlease!CLFBX'){
    		var source = Ext.getCmp('fp_sourcekind').value;
    		var form = Ext.getCmp('fp_sourcekind').ownerCt;
    		if(source == '原厂拜访记录'){
    			if(Ext.getCmp('fp_cucode')){
    				form.down('#fp_cucode').hide();
    			}
    			if(Ext.getCmp('fp_cuname')){
    				form.down('#fp_cuname').hide();
    			}
    		}else if(source == '客户拜访记录'){
    			if(Ext.getCmp('fp_vendcode')){
    				form.down('#fp_vendcode').hide();
    			}
    			if(Ext.getCmp('fp_vendname')){
    				form.down('#fp_vendname').hide();
    			}
    		}else{
    			if(Ext.getCmp('fp_vendcode')){
    				form.down('#fp_vendcode').hide();
    			}
    			if(Ext.getCmp('fp_vendname')){
    				form.down('#fp_vendname').hide();
    			}
    			if(Ext.getCmp('fp_cucode')){
    				form.down('#fp_cucode').hide();
    			}
    			if(Ext.getCmp('fp_cuname')){
    				form.down('#fp_cuname').hide();
    			}
    		}
    	}
    	if(caller=='FeePlease!FYBX'&&Ext.getCmp('fp_object')){
    		var obj=Ext.getCmp('fp_object').value;
    		var form = Ext.getCmp('fp_object').ownerCt;
    		if(obj=='供应商'){
    			form.down('#fp_cucode').hide();
				form.down('#fp_cuname').hide();
				form.down('#fp_vendcode').show();
				form.down('#fp_vendname').show();
				if(!isNoClean){
					form.down('#fp_cucode').setValue('');
					form.down('#fp_cuname').setValue('');
					form.down('#fp_vendcode').setValue('');
					form.down('#fp_vendname').setValue('');
				}
    		}else if(obj=='客户'){
    			form.down('#fp_cucode').show();
				form.down('#fp_cuname').show();
				form.down('#fp_vendcode').hide();
				form.down('#fp_vendname').hide();
				if(!isNoClean){
					form.down('#fp_cucode').setValue('');
					form.down('#fp_cuname').setValue('');
					form.down('#fp_vendcode').setValue('');
					form.down('#fp_vendname').setValue('');
				}
    		}else{
    			form.down('#fp_cucode').hide();
				form.down('#fp_cuname').hide();
				form.down('#fp_vendcode').hide();
				form.down('#fp_vendname').hide();
				if(!isNoClean){
					form.down('#fp_cucode').setValue('');
					form.down('#fp_cuname').setValue('');
					form.down('#fp_vendcode').setValue('');
					form.down('#fp_vendname').setValue('');
				}
    		}
    	}
    }
});