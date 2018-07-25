Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.EmpWorkDateSpecial', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','hr.attendance.EmpWorkDateSpecial','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Delete','core.button.Submit','core.button.ResSubmit',
      		'core.button.Print','core.button.Audit','core.button.ResAudit','core.button.Close','core.button.Update',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
            'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var flag=1;
    				flag=me.check();
    				if(flag){
	    				//保存之前的一些前台的逻辑判定
    					this.FormUtil.beforeSave(this);
    				}			
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ews_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ews_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var flag=1;
    				flag=me.check();
    				if(flag){
	    				this.FormUtil.onUpdate(this);
    				}
	    		}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addEmpWorkDateSpecial', '新增特殊调班', 'jsps/hr/attendance/empworkdateSpecial.jsp');
    			}
    		},
    		'erpCloseButton': {
    			afterrender:function(btn){
    				var form = me.getForm(btn);
    				var degree=Ext.getCmp('ews_count').value;
	    			if(degree==1){
	    				Ext.getCmp('ews_beg2').hide();
	    				Ext.getCmp('ews_end2').hide();
	    			}else if(degree==2){
	    				Ext.getCmp('ews_beg2').show();
	    				Ext.getCmp('ews_end2').show();
	    			} 			
	   			},
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ews_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ews_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ews_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('ews_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ews_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ews_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ews_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('ews_id').value);
    			}
    		},
    		'field[name=ews_count]':{
	    		change:function(f){
	    			var form =Ext.getCmp('form');
	    			if(f.value==1){
	    				Ext.getCmp('ews_beg2').hide();
	    				Ext.getCmp('ews_end2').hide();
	    			}else if(f.value==2){
	    				Ext.getCmp('ews_beg2').show();
	    				Ext.getCmp('ews_end2').show();
	    			} 	    			
	      		}
    		},
    		'field[name=ews_emname]':{
    			beforetrigger: function(field) {
    				var value = Ext.getCmp('ews_date').getValue();
    				if(value) {
    					field.findConfig = "to_char(ew_date, 'yyyy-mm-dd')='"+Ext.Date.format(value, 'Y-m-d')+"'";
    				}else{
    					showError("请先选择调班时间!");
    					return false;
    				}
				}
    		},
    		'field[name=ews_beg1]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('ews_end1').setMinValue(t);
    			}
    		},
    		'field[name=ews_end1]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('ews_beg1').setMaxValue(t);
	        		Ext.getCmp('ews_beg2').setMinValue(t);
    			}
    		},'field[name=ews_beg2]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('ews_end1').setMaxValue(t);
	        		Ext.getCmp('ews_end2').setMinValue(t);
    			}
    		},
    		'field[name=ews_end2]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('ews_beg2').setMaxValue(t);
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	check:function(){
		var count=Ext.getCmp('ews_count').value;
		var t1=Ext.getCmp('ews_beg1').value;
		var t2=Ext.getCmp('ews_end1').value;
		var t3=Ext.getCmp('ews_beg2').value;
		var t4=Ext.getCmp('ews_end2').value;
		if(count==1){
			if(!t1){
				showError('正班一起始时间未设置');
				return 0;
			}else if(!t2){
				showError('正班一结束时间未设置');
				return 0;
			}
			return 1;
		}else if(count==2){
			if(!t1){
				showError('正班一起始时间未设置');
				return 0;
			}else if(!t2){
				showError('正班一结束时间未设置');
				return 0;
			}else if(!t3){
				showError('正班二起始时间未设置');
				return 0;
			}else if(!t4){
				showError('正班二结束时间未设置');
				return 0;
			}
			return 1;
		}
	}
});