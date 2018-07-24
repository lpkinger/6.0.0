Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workWeekly.AddWorkWeekly', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.workWeekly.AddWorkWeekly','core.form.Panel','core.button.CatchWorkContent',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.form.DetailTextField','core.form.FileField','common.datalist.GridPanel','common.datalist.Toolbar',
    		'core.form.HrOrgSelectField'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addWorkDaily', '新增工作周报', 'jsps/oa/persontask/workWeekly/addWorkWeekly.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ww_id').value);
    			}
    		},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ww_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ww_id').value);
    			}
    		},
    		'erpCatchWorkContentButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp('ww_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.catchWorkContent();
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ww_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ww_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ww_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ww_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ww_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ww_id').value);
    			}
    		},
      		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'field[name=ww_week]':{
				beforerender:function(f){					
					if(formCondition!=null&&formCondition!=''){
						f.readOnly=true;
						f.fieldStyle=Ext.getCmp('ww_code').fieldStyle;
					}
				},
				afterrender:function(f){
					if(f.value==''){
    					var value = Ext.Date.format(new Date(Ext.getCmp('ww_date').value),'W');
				   		f.setValue(value);
				   		var nowDate = new Date(Ext.getCmp('ww_date').value);
				   		var w = nowDate.getDay();
				   		if(w=='0'){
				   			w=7;
				   		}
				   		var start = Ext.Date.add(nowDate, Ext.Date.DAY, (1-w));
				   		var end = Ext.Date.add(nowDate, Ext.Date.DAY, (7-w));
				   		Ext.getCmp('ww_starttime').setValue(Ext.util.Format.date(start, "Y-m-d"));
    					Ext.getCmp('ww_endtime').setValue(Ext.util.Format.date(end, "Y-m-d"));
    				}
    				//获取参数配置限制的周数
    				me.BaseUtil.getSetting('WorkWeekly','workWeeklyLimit',function(val){
    					var nowDate = new Date();
			    		var datas = new Array();
    					if(val===''||val===null){
    						val=1;
    					}else if(val<0){
    						val=0
    					}else if(val>12){
    						val=12;
    					}
    					for(var i = 0 ; i <= val ; i++){
			    			var date =  Ext.Date.add(nowDate, Ext.Date.DAY, (-i*7));
			    			var month = Ext.Date.format(date,'W')
							var w = date.getDay();
							if(w=='0'){
								w=7;
							}
							var start = Ext.Date.add(date, Ext.Date.DAY, (1-w));
							var end = Ext.Date.add(date, Ext.Date.DAY, (7-w));
			    			var startDate = Ext.util.Format.date(start, "Y-m-d");
			    			var endDate = Ext.util.Format.date(end, "Y-m-d");
			    			datas.push({display: month, value: month, dates:startDate+","+endDate});
			    		}
			    		f.store = {
			    			fields: ["display", "value","dates"], 
			    			data:datas
			    		}
    				},false);
				},
				select:function(f,records){
					var value = records[0].data.dates.split(',');
					Ext.getCmp('ww_starttime').setValue(value[0]);
					Ext.getCmp('ww_endtime').setValue(value[1]);
				}
			},
			'field[name=ww_comment]':{
				beforerender:function(f){
					f.grow=true;//true 如果此表单项需要自动增长、收缩到它的内容长度(默认为false)
				}
			},
			'htmleditor[name=ww_context]': {
    			initialize:function(f){
    			   var iframe=document.getElementById('ww_context').getElementsByTagName("iframe")[0];
    			   if(iframe.contentWindow.document.body.childNodes.length>0){
    				   iframe.scrolling="yes";//从不显示滚动条
           			   var body=iframe.contentWindow.document.body;
       				   var child=body.childNodes;
       				   var h=0;
       				   for(var i=0;i<child.length;i++){
       					   if(child[i].offsetHeight){
       						h+=child[i].offsetHeight;
       						if(child[i].nodeName!='tr'){
       							h+=14;
       						}
       					   }else if(child[i].nodeName!='br'){
       						   h+=20;
       					   }
       				   }  
       				   if(h<350){
       					 f.setHeight(350);
       				   }else{
       					f.setHeight(h);
       				   }
    			   }else{
    				   f.setHeight(350);
    			   }
    			   var form=Ext.getCmp('form');
    				form.doLayout();
    			}    			
    		},
    		'htmleditor[name=ww_unfinishedtask]': {
    			initialize:function(f){
    			   var iframe=document.getElementById('ww_unfinishedtask').getElementsByTagName("iframe")[0];
    			   if(iframe.contentWindow.document.body.childNodes.length>0){
    				   iframe.scrolling="yes";
           			   var body=iframe.contentWindow.document.body;
       				   var child=body.childNodes;
       				   var h=0;
       				   for(var i=0;i<child.length;i++){
       					   if(child[i].offsetHeight){
       						h+=child[i].offsetHeight;
       						if(child[i].nodeName!='tr'){
       							h+=14;
       						}
       					   }else if(child[i].nodeName!='br'){
       						   h+=20;
       					   }
       				   }   
       				   h+=100;
       				   if(h<300){
       					 f.setHeight(300);
       				   }else{
       				   	f.setHeight(300);
       				   }
    			   }else{
    				   f.setHeight(300);
    			   }
    			}
    		}
    	});
    },
    catchWorkContent:function(){
    	var me=this;
    	var form =Ext.getCmp('form');
    	var id=Ext.getCmp('ww_id').value;
    	form.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + 'oa/persontask/catchWorkContentWeekly.action',
			params: {
				id: id,
				caller:'WorkWeekly'
			},
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					window.location.reload();
				} else {
					showError('操作失败');return;
				}
			}
		});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});