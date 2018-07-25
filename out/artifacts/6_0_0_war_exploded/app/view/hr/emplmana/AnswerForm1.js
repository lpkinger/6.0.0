Ext.define('erp.view.hr.emplmana.AnswerForm1',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AnswerForm1',
	id: 'form1', 
	/*title: '答题导航 ',*/
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	bodyStyle:'background:#FFFFFF',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=").split('=')[1];
		//集团版
		var master=getUrlParam('newMaster');
		var param = {caller: this.caller || caller, id: this.formCondition || formCondition, _noc: (getUrlParam('_noc') || this._noc),other:1};
		if(master){
			param.master=master;
		}
		this.createItemsAndButtons(this,this.params || param);
	},
	createItemsAndButtons:function(form,params){
		var me = this;
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'hr/emplmana/checkExam.action',
			params: params,
			method : 'post',
			async:false,
			callback : function(options, success, response){
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				var items=new Array();
				var data=res.data;
				var qtype='',detno=0,numb=['一、','二、','三、','四、','五、','六、','七、'],need=0;
				var score=res.score==0?'':res.score;
				var judger=res.judger==null?'':res.judger;
				var time = res.judgetime==null?'':res.judgetime;
				var qendtype ='';
				items.push('<div class="msg"> <div class="ExamineeMsg ExamineeMsg1"><div class="ExamineeTitle">考生信息</div><div class="ExamineeContent">');
				items.push('<div>考  生:'+res.name+'</div>');
				items.push('<div>满  分:'+res.total+'</div>');
				items.push('<div>选择得分:'+res.choicescore+'</div>');
				items.push('<div>总  分:'+score+'</div>');
				items.push('<div>阅卷人:'+judger+'</div>');
				items.push('<div>阅卷时间:'+time+'</div>');
				items.push('</div></div><div class="subjectMsg"><div class="subjectTitle">答题信息</div><div class="subjectContent"><div class="subjectLine subjectLineFirst">');
				
				Ext.each(data,function(name,index){
					if(qtype!=data[index][3]){
						if(need%5!=0){
							for(var i=0;i<5-need%5;i++){
								items.push('<div style="display:inline;float:left;width:20%;" class="x-timepicker-item x-timepicker-hours"></div>');
							}
						}
						qendtype = qtype;
						qtype=data[index][3];
						//题目跳转导航
						if(qtype!=qendtype&&qtype!=''&&index!=0){
							qendtype = qtype;
							items.push('</div><div class="subjectLine">');
						}
						var it='<div >'+numb[detno++]+data[index][3]+'</div>';
						items.push(it);
						need=0;
					}
					need++;
					//		item.stanscore=data[index][6];//实际得分
					//   	item.score=data[index][7]==null?0:data[index][7];//实际得分
					var item='<div style="display:inline;float:left;width:20%;" class="x-timepicker-item x-timepicker-hours">'
						+'<a id=a_'+data[index][1]+' style="'+me.judge(data[index][7],data[index][6])+'" href="#q_'+data[index][1]+'" hidefocus="on" >'+data[index][1]+'</a>'+'</div>';
					items.push(item);
				});
				if(need%5!=0){
					for(var i=0;i<5-need%5;i++){
						items.push('<div style="display:inline;float:left;width:20%;" class="x-timepicker-item x-timepicker-hours"></div>');
					}
				}
				items.push('</div></div><div class="subjectTip"><div class="tipright"><span></span>答对</div><div class="tiperror"><span></span>答错</div><div class="tiprightpart"><span></span>部分正确</div></div></div>');
				form.html=items;
			}
		});
	},
	judge:function(score,stanscore){
		if (score > 0 && score < stanscore){
			return "background:#A7CEEE;";
		}else if (score > 0 && score == stanscore){
			return "background:#E4F2FD;";
		}else{
			return "background:red; color:white;";
		}
	},
	items: [],
	buttons: [{
		xtype: 'erpSubmitButton',
		id:"SubmitExam"
	},{
		xtype: 'erpCloseButton',
		id:"CloseExam",
		text:'关闭',
		iconCls : 'x-button-icon-close'
	}]
});