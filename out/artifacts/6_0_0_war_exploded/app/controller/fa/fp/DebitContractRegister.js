Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.DebitContractRegister', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'fa.fp.DebitContractRegister','core.form.Panel','core.form.MultiField','core.form.FileField','core.form.YnField','core.form.MonthDateField',
		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
		'core.button.Audit','core.button.ResAudit','core.button.ReceivablePlan','core.button.Receivable','core.button.Upload',	
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpReceivablePlanButton': { 
    			click: function(){
    				me.FormUtil.onAdd('ReceivablePlan', '收款计划', 'jsps/fa/fp/ReceivablePlan.jsp?formCondition=dcr_id='+Ext.getCmp('dcr_id').getValue()+'&gridCondition=dcrd_dcrid='+Ext.getCmp('dcr_id').getValue());
    			}			
    		},
    		'erpReceivableButton': {
    			click: function(){
    				me.FormUtil.onAdd('Receivable', '收款', 'jsps/fa/fp/Receivable.jsp?formCondition=dcr_id='+Ext.getCmp('dcr_id').getValue()+'&gridCondition=dcrd_dcrid='+Ext.getCmp('dcr_id').getValue());
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addDebitContractRegister', '新借款合同', 'jsps/fa/fp/DebitContractRegister.jsp');
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.Date.format(Ext.getCmp('dcr_deadline').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('dcr_startdate').value,'Y-m-d')){
    					showError('贷款到期日期小于贷款开始日期'); return;
    				}
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('dcr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('dcr_deadline').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('dcr_startdate').value,'Y-m-d')){
    					showError('贷款到期日期小于贷款开始日期'); return;
    				}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('dcr_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('dcr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('dcr_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('dcr_id').value);
    			}
    		},
    		'erpSubmitButton': {afterrender: function(btn){
				var statu = Ext.getCmp('dcr_statuscode');
				if(statu && statu.value != 'ENTERING'){
					btn.hide();
				}
			},
    			click: function(btn){
    				var me=this;
    				var form = me.getForm(btn);
    				var id=Ext.getCmp('dcr_id').value;
    				me.FormUtil.onSubmit(Ext.getCmp('dcr_id').value);
    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('dcr_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('dcr_id').value);
    			}
    		},
    		'field[name=dcr_deadline]':{
    			change:function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('dcr_startdate')){
    					var e = Ext.util.Format.date(Ext.getCmp('dcr_startdate').value, 'Y-m-d');// 格式化日期控件值
    					var s = Ext.util.Format.date(Ext.getCmp('dcr_deadline').value, 'Y-m-d');// 格式化日期控件值
    					var end = new Date(s);
    					var start = new Date(e);
    		            var elapsed = Math.ceil((end.getTime() - start.getTime())/(86400000)); // 计算间隔天数    					
    					Ext.getCmp('dcr_overduedays').setValue(elapsed);
    				}
    			}    			
    		},
    		'field[name=dcr_deadline]':{
    			change:function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('dcr_startdate')){
    					var e = Ext.util.Format.date(Ext.getCmp('dcr_startdate').value, 'Y-m-d');// 格式化日期控件值
    					var s = Ext.util.Format.date(Ext.getCmp('dcr_deadline').value, 'Y-m-d');// 格式化日期控件值
    					var end = new Date(s);
    					var start = new Date(e);
    		            var elapsed = Math.ceil((end.getTime() - start.getTime())/(86400000*30)); // 计算间隔月数    					
    					Ext.getCmp('dcr_months').setValue(elapsed);
    				}
    			}    			
    		},
    		'field[name=dcr_startdate]':{
    			change:function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('dcr_deadline')){
    					var e = Ext.util.Format.date(Ext.getCmp('dcr_startdate').value, 'Y-m-d');// 格式化日期控件值
    					var s = Ext.util.Format.date(Ext.getCmp('dcr_deadline').value, 'Y-m-d');// 格式化日期控件值
    					var end = new Date(s);
    					var start = new Date(e);
    		            var elapsed = Math.ceil((end.getTime() - start.getTime())/(86400000*30)); // 计算间隔月数    					
    					Ext.getCmp('dcr_months').setValue(elapsed);
    				}
    			}    			
    		},
    		'numberfield[name=dcr_loanrate]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var dcr_loanamount = Ext.getCmp('dcr_loanamount').value;
    				//贷款月利率
    				var dcr_loanrate = Ext.getCmp('dcr_loanrate').value*0.01/12;
    				//贷款月份
    				var dcr_month = Ext.getCmp('dcr_months').value;
    				var dcr_alreadyreceiveamount = 0;
    				var objArray = new Array();
    				var dcr_payinterestway = Ext.getCmp('dcr_payinterestway').value;
    				var dcr_interest=0.0;
    				if(dcr_payinterestway == "等额本息"){
    					dcr_interest = ((dcr_loanamount*dcr_loanrate*Math.pow(1+dcr_loanrate,dcr_month))/(Math.pow(1+dcr_loanrate,dcr_month)-1))*dcr_month-dcr_loanamount;
    				}else if(dcr_payinterestway == "等额本金"){
    					for(i=1;i<=dcr_month;i++){
        					//第i月还款利息
        					t1 = (dcr_loanamount-dcr_loanamount*(i-1)/dcr_month)*dcr_loanrate;
        					//第i月还款额
        					interestM = dcr_loanamount/dcr_month + t1;
        					objArray[i-1]=interestM;
        					dcr_interest=dcr_interest+interestM-dcr_loanamount/dcr_month;
        				}
    				}else if(dcr_payinterestway == "利随本清"){
    					dcr_interest = dcr_loanamount*Math.pow(1+dcr_loanrate,dcr_month)-dcr_loanamount;
    				}else if(dcr_payinterestway == "不付息"){
    					dcr_interest = 0;
    				}
    				Ext.getCmp('dcr_interest').setValue(dcr_interest);
    			}
    		},
    		'numberfield[name=dcr_loanamount]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var dcr_loanamount = Ext.getCmp('dcr_loanamount').value;
    				//贷款月利率
    				var dcr_loanrate = Ext.getCmp('dcr_loanrate').value*0.01/12;
    				//贷款月份
    				var dcr_month = Ext.getCmp('dcr_months').value;
    				var dcr_alreadyreceiveamount = 0;
    				var objArray = new Array();
    				var dcr_payinterestway = Ext.getCmp('dcr_payinterestway').value;
    				var dcr_interest=0.0;
    				if(dcr_payinterestway == "等额本息"){
    					dcr_interest = ((dcr_loanamount*dcr_loanrate*Math.pow(1+dcr_loanrate,dcr_month))/(Math.pow(1+dcr_loanrate,dcr_month)-1))*dcr_month-dcr_loanamount;
    				}else if(dcr_payinterestway == "等额本金"){
    					for(i=1;i<=dcr_month;i++){
        					//第i月还款利息
        					t1 = (dcr_loanamount-dcr_loanamount*(i-1)/dcr_month)*dcr_loanrate;
        					//第i月还款额
        					interestM = dcr_loanamount/dcr_month + t1;
        					objArray[i-1]=interestM;
        					dcr_interest=dcr_interest+interestM-dcr_loanamount/dcr_month;
        				}
    				}else if(dcr_payinterestway == "利随本清"){
    					dcr_interest = dcr_loanamount*Math.pow(1+dcr_loanrate,dcr_month)-dcr_loanamount;
    				}else if(dcr_payinterestway == "不付息"){
    					dcr_interest = 0;
    				}
    				Ext.getCmp('dcr_interest').setValue(dcr_interest);
    			}
    		},
    		'numberfield[name=dcr_months]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var dcr_loanamount = Ext.getCmp('dcr_loanamount').value;
    				//贷款月利率
    				var dcr_loanrate = Ext.getCmp('dcr_loanrate').value*0.01/12;
    				//贷款月份
    				var dcr_month = Ext.getCmp('dcr_months').value;
    				var dcr_alreadyreceiveamount = 0;
    				var objArray = new Array();
    				var dcr_payinterestway = Ext.getCmp('dcr_payinterestway').value;
    				var dcr_interest=0.0;
    				if(dcr_payinterestway == "等额本息"){
    					dcr_interest = ((dcr_loanamount*dcr_loanrate*Math.pow(1+dcr_loanrate,dcr_month))/(Math.pow(1+dcr_loanrate,dcr_month)-1))*dcr_month-dcr_loanamount;
    				}else if(dcr_payinterestway == "等额本金"){
    					for(i=1;i<=dcr_month;i++){
        					//第i月还款利息
        					t1 = (dcr_loanamount-dcr_loanamount*(i-1)/dcr_month)*dcr_loanrate;
        					//第i月还款额
        					interestM = dcr_loanamount/dcr_month + t1;
        					objArray[i-1]=interestM;
        					dcr_interest=dcr_interest+interestM-dcr_loanamount/dcr_month;
        				}
    				}else if(dcr_payinterestway == "利随本清"){
    					dcr_interest = dcr_loanamount*Math.pow(1+dcr_loanrate,dcr_month)-dcr_loanamount;
    				}else if(dcr_payinterestway == "不付息"){
    					dcr_interest = 0;
    				}
    				Ext.getCmp('dcr_interest').setValue(dcr_interest);
    			}
    		},
    		'field[name=dcr_payinterestway]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				//贷款金额
    				var dcr_loanamount = Ext.getCmp('dcr_loanamount').value;
    				//贷款月利率
    				var dcr_loanrate = Ext.getCmp('dcr_loanrate').value*0.01/12;
    				//贷款月份
    				var dcr_month = Ext.getCmp('dcr_months').value;
    				var dcr_alreadyreceiveamount = 0;
    				var objArray = new Array();
    				var dcr_payinterestway = Ext.getCmp('dcr_payinterestway').value;
    				var dcr_interest=0.0;
    				if(dcr_payinterestway == "等额本息"){
    					dcr_interest = ((dcr_loanamount*dcr_loanrate*Math.pow(1+dcr_loanrate,dcr_month))/(Math.pow(1+dcr_loanrate,dcr_month)-1))*dcr_month-dcr_loanamount;
    				}else if(dcr_payinterestway == "等额本金"){
    					for(i=1;i<=dcr_month;i++){
        					//第i月还款利息
    						t1 = (dcr_loanamount-dcr_loanamount*(i-1)/dcr_month)*dcr_loanrate;
        					//第i月还款额
        					interestM = dcr_loanamount/dcr_month + t1;
        					objArray[i-1]=interestM;
        					dcr_interest=dcr_interest+interestM-dcr_loanamount/dcr_month; 
        				}
    				}else if(dcr_payinterestway == "利随本清"){
    					dcr_interest = dcr_loanamount*Math.pow(1+dcr_loanrate,dcr_month)-dcr_loanamount;
    				}else if(dcr_payinterestway == "不付息"){
    					dcr_interest = 0;
    				}
    				Ext.getCmp('dcr_interest').setValue(dcr_interest);
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