Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Exam1', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.emplmana.Exam1','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','hr.emplmana.StartExamForm1',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2','hr.emplmana.AnswerForm1',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpSubmitButton': {
				click: function(btn){
					me.getExamValues();
				}
			},
			'radiogroup':{
				change:function(field){
					me.changeCss(field);
				}
			},
			'checkboxgroup':{
				change:function(field){
					me.changeCss(field);
				}
			},
			'textareafield':{
				change:function(field){
					me.changeCss(field);
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
	},
	changeCss:function(field){
		var qid = field.id;
	    var o = document.getElementById('a_'+qid.split('_')[1]);
	    if (o != null){
		    switch(field.isRight){
			case 'right':
			 	o.style.background="#E4F2FD";
			  	break;
			case 'rightPart':
			 	o.style.background="#A7CEEE";
				break;
			case 'error':
				o.style.background="red";
				o.style.color="#ffffff";
				break;
			default :
			  o.style.background="#ffffff";
			  o.style.color=" #333333";
			  o.style.border="1px solid #B3B3B3";
			}
	    }
	},
	getExamValues:function(){
		var items=Ext.getCmp('form').items.items,values=new Array();
		var msg='';
		Ext.each(items,function(name,index){
			if(items[index].whoami&&items[index].whoami=='jianda'){
				var score = document.getElementById('s_'+items[index].id.split('_')[1]);
				if(score.value==''){
					msg="第"+items[index].id.split('_')[1]+"题没评分，请核对后重试!";
				}
				if(isNaN(score.value)){
					msg="第"+items[index].id.split('_')[1]+"题评分不合法，请使用数字进行评分!";
				}
				var s=parseFloat(score.value);
				if(s<0||s>items[index].stanscore){
					msg="第"+items[index].id.split('_')[1]+"题评分不合理，请在合理范围内进行评分!";
				}
				var o=new Object();
				o.exd_id=items[index].exdid;
				o.score=s;
				values.push(o);
			}
		});
		if(msg!=''){
			showError(msg);return;
		}
		var v=Ext.JSON.encode(values);
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'hr/emplmana/judgeExam.action',
			params: {values:v},
			method : 'post',
			callback : function(options, success, response){
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				showMessage('提示','试卷提交成功');
				window.location.reload();
			}
		});
	}
});