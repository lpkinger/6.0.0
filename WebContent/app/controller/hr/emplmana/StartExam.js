Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.StartExam', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.StartExamForm','hr.emplmana.StartExam','core.form.Panel','core.form.FileField','core.form.MultiField',
    		'core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger','core.button.Save','core.button.Close',
    		'core.button.OverAccount','core.button.Close','core.form.MonthDateField','core.button.StartAccount',
    		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpSaveButton':{
        			beforerender:function(btn){
        				btn.setWidth(100);
        				btn.setText('开始考试');
        			},
        			click:function(btn){
        				this.startExam();
        			}
        		},
        		'erpStartAccountButton': {
        			click: function(btn){
        				this.startAccount();
        			}
        		},
        		'erpOverAccountButton': {
        			click: function(btn){
        				this.overAccount();
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	startExam: function() {
    		var ex_recorder = Ext.getCmp('ex_recorder');
    		var ex_name = Ext.getCmp('name');
    		if(ex_recorder&&ex_recorder.value){
    			name = ex_recorder.value;
    		}else if(ex_name&&ex_name.value){
    			name = ex_name.value;
    		}else{
    			showError('请填写姓名');
    				return;
    		}
    		var password=Ext.getCmp('password').value;
    		if(password==''){
    			showError('请填写手机号后六位');
    			return;
    		}
    		var ex_recordercode=Ext.getCmp('ex_recordercode');
    		if(ex_recordercode){
    			code= ex_recordercode.value;
    		}
    		Ext.Ajax.request({
    			url: basePath + 'hr/emplmana/startExam.action',
    			params: {
    				password: password,
    				name: name,
    				code: code
    			},
    			method: 'POST',
    			callback: function(opt, s, r) {
    				if(r){
    					var res = new Ext.decode(r.responseText);
    					if(res.success){
    						window.location.href = basePath + 'exam/exam.action';
    					}else{
    						showMessage("提示",res.exmsg);
    					}
    				}
    			}
    		});
    	}
    });