Ext.define('erp.view.hr.emplmana.AnswerForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AnswerForm',
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
		formCondition = getUrlParam('id');//从url解析参数
		formCondition = (formCondition == null) ? id : formCondition.replace(/IS/g,"=");
		//集团版
		var master=getUrlParam('newMaster');
		var param = {caller: this.caller || caller, id: this.formCondition || formCondition, _noc: (getUrlParam('_noc') || this._noc),other:1};
		if(master){
			param.master=master;
		}
		this.createItemsAndButtons(this,this.params || param);
	},
	createItemsAndButtons:function(form,params){
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'hr/emplmana/getExam.action',
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
				var qendtype ='';
				endtime=new Date(res.endtime);
				var start=new Date(res.starttime);
				items.push('<div class="msg"> <div class="ExamineeMsg"><div class="ExamineeTitle">考生信息</div><div class="ExamineeContent">');
				items.push('<div>考生: '+res.name+'</div>');
				var sh = start.getHours()<10?'0'+start.getHours():start.getHours();
				var eh = endtime.getHours()<10?'0'+endtime.getHours():endtime.getHours();
				var sm = start.getMinutes()<10?'0'+start.getMinutes():start.getMinutes();
				var em = endtime.getMinutes()<10?'0'+endtime.getMinutes():endtime.getMinutes();
				items.push('<div>考试时间: '+sh+':'+sm+'~~'+eh+':'+em+'</div>');
				items.push('<div id=timer1></div>');
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
						var it='<div>'+numb[detno++]+data[index][3]+'</div>';
						items.push(it);
						need=0;
					}
					need++;
					var item='<div style="display:inline;float:left;width:20%;" class="x-timepicker-item x-timepicker-hours">'
						+'<a id=a_'+data[index][1]+' style="border: 1px solid #979797;" href="#q_'+data[index][1]+'" hidefocus="on" >'+data[index][1]+'</a>'+'</div>';
					items.push(item);
				});
				items.push('</div></div><div class="subjectTip"><div class="tip1"><span></span>已答</div><div class="tip2"><span></span>未答</div></div></div>');
				form.html=items;
			}
		});
	},
	items: [],
	buttons: [/*{
		xtype: 'erpStartAccountButton'
	},*/{
		xtype: 'erpSubmitButton',
		id:"SubmitExam"
	},{
		xtype: 'erpDeleteButton',
		id:"CloseExam",
		text:'关闭',
		iconCls : 'x-button-icon-close'
	}],
	listeners:{
		afterrender:function(){
			var panel = parent.Ext.getCmp('tree-tab');
			if(panel && !panel.collapsed) {
				panel.toggleCollapse();
			}else{
				panel = parent.parent.Ext.getCmp('tree-tab');
				if(panel && !panel.collapsed) {
					panel.toggleCollapse();
				}	
			}
			if(parent.Ext.getCmp('win')){
				parent.Ext.getCmp('win').maximize();
			}
			var gap = document.getElementsByClassName("msg")[0].clientHeight+20;
			var tooltips = Ext.getCmp('');
		}
	}
});