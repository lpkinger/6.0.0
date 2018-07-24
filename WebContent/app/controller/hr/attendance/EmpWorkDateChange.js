Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.EmpWorkDateChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','hr.attendance.EmpWorkDateChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.trigger.DbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.Audit',
      		'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit','core.trigger.TextAreaTrigger',
      		'core.trigger.MultiDbfindTrigger','core.form.EmpSelectField'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
            'erpGridPanel2': {
    			//itemclick: this.onGridItemClick
    		},
    		'field[name=edc_oldwdcode]':{
    			beforetrigger: function(field) {
    				var d1 = Ext.getCmp('edc_beginDate').getValue(); 
    				var d2 = Ext.getCmp('edc_endDate').getValue(); 
    				if(!d1||!d2) {
    					showError("请先选择日期范围!");
    					return false;
    				}else{
    					if(Ext.Date.format(d1,'Y-m-d') > Ext.Date.format(d2,'Y-m-d')){
							showError('日期范围输入有误');
							return false;
						}
    				}
				}
    		},
    		'EmpSelectfield[name=edc_emnames]':{
    			afterrender:function(){
				},
    			afterclick:function(f){
    				var wdcode=Ext.getCmp('edc_oldwdcode').getValue();
    				if(wdcode){
    					var d1 = Ext.Date.format(Ext.getCmp('edc_beginDate').getValue(),'Y-m-d'); 
    					var d2 = Ext.Date.format(Ext.getCmp('edc_endDate').getValue(),'Y-m-d');
    					var condition="ew_wdcode='"+wdcode+"' and ew_date BETWEEN to_date('"+d1+"','yyyy-mm-dd') and to_date('"+d2+"','yyyy-mm-dd')";
    					Ext.Ajax.request({//查询数据
		  					url : basePath + '/hr/attendance/getEmp.action',
							params:{
						 		 condition:condition
							},
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);													
						 		if(res.data){
									Ext.getCmp('itemselector-field').fromField.store.loadData(res.data);
								 } else if(res.exceptionInfo){
							    	 showError(res.exceptionInfo);
								 }
						 }
					 });
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var flag=1;
    				var d1 = Ext.getCmp('edc_beginDate').getValue(); 
    				var d2 = Ext.getCmp('edc_endDate').getValue(); 
    				if(Ext.Date.format(d1,'Y-m-d') > Ext.Date.format(d2,'Y-m-d')){
						showError('日期范围输入有误');
						flag=0;
					}
    				if(!Ext.getCmp('edc_emids').getValue()){
						showError("请选择员工");
						flag=0;
					}
					var oldwd=Ext.getCmp('edc_oldwdcode').value;
					var newwd=Ext.getCmp('edc_wdcode').value;
					if(oldwd==newwd){
						showError("现班次编号可和原班次编号不能相同");
						flag=0;
					}
					if(flag){
						me.FormUtil.beforeSave(this);  
					}	
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('edc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('edc_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var flag=1;
    				var d1 = Ext.getCmp('edc_beginDate').getValue(); 
    				var d2 = Ext.getCmp('edc_endDate').getValue(); 
    				if(Ext.Date.format(d1,'Y-m-d') > Ext.Date.format(d2,'Y-m-d')){
						showError('日期范围输入有误');
						flag=0;
					}
    				if(!Ext.getCmp('edc_emids').getValue()){
						showError("请选择员工");
						flag=0;
					}
					var oldwd=Ext.getCmp('edc_oldwdcode').value;
					var newwd=Ext.getCmp('edc_wdcode').value;
					if(oldwd==newwd){
						showError("现班次编号和原班次编号不能相同");
						flag=0;
					}
					if(flag){
						me.FormUtil.onUpdate(this);
	    			}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addEmpWorkDateChange', '新增员工调班', 'jsps/hr/attendance/empworkdatechange.jsp');
    			}
    		},
    		'erpCloseButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('edc_statuscode');
					if(status && status.value == 'ENTERING'){
						Ext.getCmp('grid').hide();
					}
				},
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('edc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('edc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('edc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('edc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('edc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('edc_id').value);
				}
			}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});