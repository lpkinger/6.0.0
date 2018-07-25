Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workMonthly.AddWorkMonthly', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.workMonthly.AddWorkMonthly','core.form.Panel','core.button.CatchWorkContent',
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
    				me.FormUtil.onAdd('addWorkDaily', '新增工作月报', 'jsps/oa/persontask/workMonthly/addWorkMonthly.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wm_id').value);
    			}
    		},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wm_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('wm_id').value);
    			}
    		},
    		'erpCatchWorkContentButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp('wm_statuscode');
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
    				var status = Ext.getCmp('wm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('wm_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('wm_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wm_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('wm_id').value);
    			}
    		},
      		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'field[name=wm_month]':{
				beforerender:function(f){					
					if(formCondition!=null&&formCondition!=''){
						f.readOnly=true;
						f.fieldStyle=Ext.getCmp('wm_code').fieldStyle;
					}
				},
				afterrender:function(f){
					if(f.value==''){
    					var value = Ext.Date.format(new Date(Ext.getCmp('wm_date').value),'m');
				   		f.setValue(value);
				   		Ext.getCmp('wm_starttime').setValue(Ext.util.Format.date(Ext.Date.getFirstDateOfMonth(new Date(Ext.getCmp('wm_date').value)), "Y-m-d"));
    					Ext.getCmp('wm_endtime').setValue(Ext.util.Format.date(Ext.Date.getLastDateOfMonth(new Date(Ext.getCmp('wm_date').value)), "Y-m-d"));
    				}
    				//获取参数配置限制的月份数
    				me.BaseUtil.getSetting('WorkMonthly','workMonthlyLimit',function(val){
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
			    			var date =  Ext.Date.add(nowDate, Ext.Date.MONTH, -i);
			    			var month = Ext.Date.format(date,'m')
			    			var startDate = Ext.util.Format.date(Ext.Date.getFirstDateOfMonth(date), "Y-m-d");
			    			var endDate = Ext.util.Format.date(Ext.Date.getLastDateOfMonth(date), "Y-m-d");
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
					Ext.getCmp('wm_starttime').setValue(value[0]);
					Ext.getCmp('wm_endtime').setValue(value[1]);
				}
			},
			'field[name=wm_comment]':{
				beforerender:function(f){
					f.grow=true;//true 如果此表单项需要自动增长、收缩到它的内容长度(默认为false)
				}
			},
			'htmleditor[name=wm_context]': {
    			initialize:function(f){
    			   var iframe=document.getElementById('wm_context').getElementsByTagName("iframe")[0];
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
    		'htmleditor[name=wm_unfinishedtask]': {
    			initialize:function(f){
    			   var iframe=document.getElementById('wm_unfinishedtask').getElementsByTagName("iframe")[0];
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
    	var id=Ext.getCmp('wm_id').value;
    	form.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + 'oa/persontask/catchWorkContentMonthly.action',
			params: {
				id: id,
				caller:'WorkMonthly'
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