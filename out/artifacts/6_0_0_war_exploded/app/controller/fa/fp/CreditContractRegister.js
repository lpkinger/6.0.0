Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.CreditContractRegister', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'fa.fp.CreditContractRegister','core.form.Panel','core.form.MultiField','core.form.FileField','core.form.YnField','core.form.MonthDateField',
		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
		'core.button.Audit','core.button.ResAudit','core.button.ReturnPlan','core.button.Return','core.button.Upload',	
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
	],
	init:function(){
    	var me = this;
    	this.control({ 
    		'erpReturnPlanButton': {
    			click: function(){
    				me.FormUtil.onAdd('ReturnPlan', '还款计划', 'jsps/fa/fp/ReturnPlan.jsp?formCondition=ccr_id='+Ext.getCmp('ccr_id').getValue()+'&gridCondition=ccrd_ccrid='+Ext.getCmp('ccr_id').getValue());
    			}
    		},
    		'erpReturnButton': {
    			click: function(){
    				me.FormUtil.onAdd('Return', '还款', 'jsps/fa/fp/Return.jsp?formCondition=ccr_id='+Ext.getCmp('ccr_id').getValue()+'&gridCondition=ccrd_ccrid='+Ext.getCmp('ccr_id').getValue());
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCreditContractRegister', '新贷款合同', 'jsps/fa/fp/CreditContractRegister.jsp');
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ccr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('ccr_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('ccr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('ccr_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('ccr_id').value);
    			}
    		},
    		'erpSubmitButton': {
	    		afterrender: function(btn){
					var statu = Ext.getCmp('ccr_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				var me=this;
    				var form = me.getForm(btn);
    				var id=Ext.getCmp('ccr_id').value;
    				me.FormUtil.onSubmit(Ext.getCmp('ccr_id').value);
    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('ccr_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('ccr_id').value);
    			}
    		},   		
    		'field[name=ccr_deadline]':{
    			change:function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('ccr_startdate')){
    					var e = Ext.util.Format.date(Ext.getCmp('ccr_startdate').value, 'Y-m-d');// 格式化日期控件值
    					var s = Ext.util.Format.date(Ext.getCmp('ccr_deadline').value, 'Y-m-d');// 格式化日期控件值
    					var end = new Date(s);
    					var start = new Date(e);
    		            var elapsed = Math.ceil((end.getTime() - start.getTime())/(86400000)); // 计算间隔天数    					
    					Ext.getCmp('ccr_overduedays').setValue(elapsed);
    				}
    			}    			
    		},
    		'field[name=ccr_deadline]':{
    			change:function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('ccr_startdate')){
    					var e = Ext.util.Format.date(Ext.getCmp('ccr_startdate').value, 'Y-m-d');// 格式化日期控件值
    					var s = Ext.util.Format.date(Ext.getCmp('ccr_deadline').value, 'Y-m-d');// 格式化日期控件值
    					var end = new Date(s);
    					var start = new Date(e);
    		            var elapsed = Math.ceil((end.getTime() - start.getTime())/(86400000*30)); // 计算间隔月数    					
    					Ext.getCmp('ccr_months').setValue(elapsed);
    				}
    			}    			
    		},
    		'field[name=ccr_startdate]':{
    			change:function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('ccr_deadline')){
    					var e = Ext.util.Format.date(Ext.getCmp('ccr_startdate').value, 'Y-m-d');// 格式化日期控件值
    					var s = Ext.util.Format.date(Ext.getCmp('ccr_deadline').value, 'Y-m-d');// 格式化日期控件值
    					var end = new Date(s);
    					var start = new Date(e);
    		            var elapsed = Math.ceil((end.getTime() - start.getTime())/(86400000*30)); // 计算间隔月数    					
    					Ext.getCmp('ccr_months').setValue(elapsed);
    				}
    			}    			
    		},
    		'numberfield[name=ccr_loanrate]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var ccr_loanamount = Ext.getCmp('ccr_loanamount').value;
    				//贷款月利率
    				var ccr_loanrate = Ext.getCmp('ccr_loanrate').value*0.01/12;
    				//贷款月份
    				var ccr_month = Ext.getCmp('ccr_months').value;
    				var ccr_alreadyreturnamount = 0;
    				var objArray = new Array();
    				var ccr_payinterestway = Ext.getCmp('ccr_payinterestway').value;
    				var ccr_interest=0.0;
    				if(ccr_payinterestway == "等额本息"){
    					ccr_interest = ((ccr_loanamount*ccr_loanrate*Math.pow(1+ccr_loanrate,ccr_month))/(Math.pow(1+ccr_loanrate,ccr_month)-1))*ccr_month-ccr_loanamount;
    				}else if(ccr_payinterestway == "等额本金"){
    					for(i=1;i<=ccr_month;i++){
        					//第i月还款利息
        					t1 = (ccr_loanamount-ccr_loanamount*(i-1)/ccr_month)*ccr_loanrate;
        					//第i月还款额
        					interestM = ccr_loanamount/ccr_month + t1;
        					objArray[i-1]=interestM;
        					ccr_interest=ccr_interest+interestM-ccr_loanamount/ccr_month; 
        				}
    				}else if(ccr_payinterestway == "利随本清"){
    					ccr_interest = ccr_loanamount*Math.pow(1+ccr_loanrate,ccr_month)-ccr_loanamount;
    				}else if(ccr_payinterestway == "不付息"){
    					ccr_interest = 0;
    				}
    				Ext.getCmp('ccr_interest').setValue(ccr_interest);
    			}
    		},
    		'numberfield[name=ccr_loanamount]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var ccr_loanamount = Ext.getCmp('ccr_loanamount').value;
    				//贷款月利率
    				var ccr_loanrate = Ext.getCmp('ccr_loanrate').value*0.01/12;
    				//贷款月份
    				var ccr_month = Ext.getCmp('ccr_months').value;
    				var ccr_alreadyreturnamount = 0;
    				var objArray = new Array();
    				var ccr_payinterestway = Ext.getCmp('ccr_payinterestway').value;
    				var ccr_interest=0.0;
    				if(ccr_payinterestway == "等额本息"){
    					ccr_interest = ((ccr_loanamount*ccr_loanrate*Math.pow(1+ccr_loanrate,ccr_month))/(Math.pow(1+ccr_loanrate,ccr_month)-1))*ccr_month-ccr_loanamount;
    				}else if(ccr_payinterestway == "等额本金"){
    					for(i=1;i<=ccr_month;i++){
        					//第i月还款利息
        					t1 = (ccr_loanamount-ccr_loanamount*(i-1)/ccr_month)*ccr_loanrate;
        					//第i月还款额
        					interestM = ccr_loanamount/ccr_month + t1;
        					objArray[i-1]=interestM;
        					ccr_interest=ccr_interest+interestM-ccr_loanamount/ccr_month; 
        				}
    				}else if(ccr_payinterestway == "利随本清"){
    					ccr_interest = ccr_loanamount*Math.pow(1+ccr_loanrate,ccr_month)-ccr_loanamount;
    				}else if(ccr_payinterestway == "不付息"){
    					ccr_interest = 0;
    				}
    				Ext.getCmp('ccr_interest').setValue(ccr_interest);
    			}
    		},
    		'numberfield[name=ccr_months]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var ccr_loanamount = Ext.getCmp('ccr_loanamount').value;
    				//贷款月利率
    				var ccr_loanrate = Ext.getCmp('ccr_loanrate').value*0.01/12;
    				//贷款月份
    				var ccr_month = Ext.getCmp('ccr_months').value;
    				var ccr_alreadyreturnamount = 0;
    				var objArray = new Array();
    				var ccr_payinterestway = Ext.getCmp('ccr_payinterestway').value;
    				var ccr_interest=0.0;
    				if(ccr_payinterestway == "等额本息"){
    					ccr_interest = ((ccr_loanamount*ccr_loanrate*Math.pow(1+ccr_loanrate,ccr_month))/(Math.pow(1+ccr_loanrate,ccr_month)-1))*ccr_month-ccr_loanamount;
    				}else if(ccr_payinterestway == "等额本金"){
    					for(i=1;i<=ccr_month;i++){
        					//第i月还款利息
        					t1 = (ccr_loanamount-ccr_loanamount*(i-1)/ccr_month)*ccr_loanrate;
        					//第i月还款额
        					interestM = ccr_loanamount/ccr_month + t1;
        					objArray[i-1]=interestM;
        					ccr_interest=ccr_interest+interestM-ccr_loanamount/ccr_month; 
        				}
    				}else if(ccr_payinterestway == "利随本清"){
    					ccr_interest = ccr_loanamount*Math.pow(1+ccr_loanrate,ccr_month)-ccr_loanamount;
    				}else if(ccr_payinterestway == "不付息"){
    					ccr_interest = 0;
    				}
    				Ext.getCmp('ccr_interest').setValue(ccr_interest);
    			}
    		},
    		'field[name=ccr_payinterestway]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var ccr_loanamount = Ext.getCmp('ccr_loanamount').value;
    				//贷款月利率
    				var ccr_loanrate = Ext.getCmp('ccr_loanrate').value*0.01/12;
    				//贷款月份
    				var ccr_month = Ext.getCmp('ccr_months').value;
    				var ccr_alreadyreturnamount = 0;
    				var objArray = new Array();
    				var ccr_payinterestway = Ext.getCmp('ccr_payinterestway').value;
    				var ccr_interest=0.0;
    				if(ccr_payinterestway == "等额本息"){
    					ccr_interest = ((ccr_loanamount*ccr_loanrate*Math.pow(1+ccr_loanrate,ccr_month))/(Math.pow(1+ccr_loanrate,ccr_month)-1))*ccr_month-ccr_loanamount;
    				}else if(ccr_payinterestway == "等额本金"){
    					for(i=1;i<=ccr_month;i++){
        					//第i月还款利息
        					t1 = (ccr_loanamount-ccr_loanamount*(i-1)/ccr_month)*ccr_loanrate;
        					//第i月还款额
        					interestM = ccr_loanamount/ccr_month + t1;
        					objArray[i-1]=interestM;
        					ccr_interest=ccr_interest+interestM-ccr_loanamount/ccr_month; 
        				}
    				}else if(ccr_payinterestway == "利随本清"){
    					ccr_interest = ccr_loanamount*Math.pow(1+ccr_loanrate,ccr_month)-ccr_loanamount;
    				}else if(ccr_payinterestway == "不付息"){
    					ccr_interest = 0;
    				}
    				Ext.getCmp('ccr_interest').setValue(ccr_interest);
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});