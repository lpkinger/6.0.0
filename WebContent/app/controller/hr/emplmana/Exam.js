Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Exam', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.emplmana.Exam','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','hr.emplmana.StartExamForm',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2','hr.emplmana.AnswerForm',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		Ext.defer(function(){
    		me.timer();
    	}, 2000);
		this.control({
			'erpSubmitButton': {
				click: function(btn){
					var form = Ext.getCmp('form');
                    if(form.getForm().isValid()){
						Ext.MessageBox.confirm('提示', '确认要交卷吗?', del);
	                    function del(btn){
	                        if(btn == 'yes'){
	                        		me.getExamValues();
	                        	}
	                        }
	                    }
                    else{
                    	showError("请根据错误提示完善试卷内容！");
                    }
				}
			},
			'erpDeleteButton':{
				beforerender:function(btn){
					btn.setText('退出');
				},
				click:function(btn){
					Ext.MessageBox.confirm('提示', '确认要退出吗?', del);
                    function del(btn){
                        if(btn == 'yes'){
                        	Ext.Ajax.request({
                    			url: basePath + 'hr/emplmana/logoutExam.action',
                    			params: {},
                    			method: 'POST',
                    			callback: function(opt, s, r) {
                    				if(s)
                    					window.location.href = basePath + 'exam/exam.action';
                    			}
                    		});
                        }
                    }
				}
			},
			'radiogroup':{
				change:function(field){
					me.changeCss(field.id,true);
				}
			},
			'checkboxgroup':{
				change:function(field){
					var count = 0;
					Ext.each(field.items.items,function(item,index){
						if(item.checked){
							count++;
						}
					});
					if (count > 0){
						me.changeCss(field.id,true);
					}else{
						me.changeCss(field.id,false);
					}
				}
			},
			'textareafield':{
				change:function(field){
					if(field.value){
						me.changeCss(field.id,true);
					}else{
						me.changeCss(field.id,false);
					}
					
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
	changeCss:function(qid,bool){
		var o = document.getElementById('a_'+qid.split('_')[1]);
		if(bool&&o != null){
	        o.style.background="#E4F2FD";
		}else{
			o.style.background="#FFFFFF";
		}
	},
	timer:function(){
		var me1=this;
		var time=endtime.getTime()-(new Date()).getTime();
		if(time<1000*60*5&&!isShow){//剩余5分钟提示下
			isShow = true;
			document.getElementById("timer1").style.color="red";
			showMessage('提  示','距离考试结束只剩5分钟了！');
		}
		if(time<=0&&!isSubmit){//时间到了，自动交卷
			isSubmit = true;
			showMessage('提  示','考试结束，自动提交试卷!');
			me1.getExamValues();
		}
		var hh=parseInt(time / 1000 / 60 / 60 % 24, 10);
		var mm=parseInt(time / 1000 / 60 % 60, 10);
		var ss=parseInt(time / 1000 % 60, 10);
		if(hh<10)
			hh='0'+hh;
		if(mm<10)
			mm='0'+mm;
		if(ss<10)
			ss='0'+ss;
		document.getElementById("timer1").innerHTML = '剩余时间: <span style="color:red;font-weight:bold">' + hh + ':' + mm +':'+ss+'</span>';  
		setTimeout(function(){
			me1.timer();
		},1000);  
	},
	getExamValues:function(){
		var items=Ext.getCmp('form').items.items,values=new Array();
		Ext.each(items,function(name,index){
			if(items[index].xtype=="radiogroup"||items[index].xtype=="checkboxgroup"){
				var o=new Object();
				o.exd_id=items[index].exdid;
				var i=items[index].items.items;
				o.answer='';
				Ext.each(i,function(nam,inde){
					if(i[inde].checked){
						o.answer=o.answer+i[inde].anvalue;
					}
				});
				values.push(o);
			}else if(items[index].xtype=="textareafield"){
				var o=new Object();
				o.exd_id=items[index].exdid;
				o.answer=items[index].value;
				values.push(o);
			}
		});
		var v=Ext.JSON.encode(values)
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'hr/emplmana/submitExam.action',
			params: {values:v},
			method : 'post',
			callback : function(options, success, response){
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				window.location.href=basePath +'jsps/hr/emplmana/question/endExam.html';
			}
		});
	}
});