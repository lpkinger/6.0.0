Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workDaily.AddWorkDaily', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'oa.persontask.workDaily.AddWorkDaily','core.form.Panel','core.grid.Panel2','core.button.CatchWorkContent',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.form.DetailTextField','core.form.FileField','common.datalist.GridPanel','common.datalist.Toolbar',
    		'core.form.HrOrgSelectField','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    		},
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
    				me.FormUtil.onAdd('addWorkDaily', '新增工作日报', 'jsps/oa/persontask/workDaily/addWorkDaily.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wd_id').value);
    			}
    		},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wd_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('wd_id').value);
    			}
    		},
    		'erpCatchWorkContentButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp('wd_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    		    	if (grid) {
    		    		if (grid.store.RawData && grid.store.RawData.length>0) {
    		    			warnMsg('明细行已有数据,若再次抓取可能会覆盖已有数据', function(btn){
    		    				if(btn == 'yes'){
    		    					me.catchWorkContent();
    		    				}
    		    			});
    		    		} else {
    		    			me.catchWorkContent();
    		    		}
    		    	} else {
    		    		me.catchWorkContent();
    		    	} 
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wd_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('wd_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wd_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('wd_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wd_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('wd_id').value);
    			}
    		},
      		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'field[name=wd_date]':{
				beforerender:function(f){					
					if(formCondition!=null&&formCondition!=''){
						f.readOnly=true;
						f.fieldStyle=Ext.getCmp('wd_code').fieldStyle;
					}
				}
			},
			'field[name=wd_comment]':{
				beforerender:function(f){
					f.grow=true;//true 如果此表单项需要自动增长、收缩到它的内容长度(默认为false)
				}
			},
			'htmleditor[name=wd_context]': {
    			initialize:function(f){
    			   var iframe=document.getElementById('wd_context').getElementsByTagName("iframe")[0];
    			   if(iframe.contentWindow.document.body.childNodes.length>0){
    				   iframe.scrolling="no";//从不显示滚动条
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
    		'htmleditor[name=wd_unfinishedtask]': {
    			initialize:function(f){
    			   var iframe=document.getElementById('wd_unfinishedtask').getElementsByTagName("iframe")[0];
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
    		},
    		'dbfindtrigger[name=wdd_workcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var workkind = record.data['wdd_workkind'];
    				if(workkind == null || workkind == ''){
    					showError("请先选择工作类型!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					if (workkind=='项目任务') {
    						//任务项目必须是本人的任务
    						t.dbBaseCondition = "WV_KIND='" + workkind + "' and wv_emcode='" + em_code + "'";
    					} else {
    						t.dbBaseCondition = "WV_KIND='" + workkind + "'";
    					} 
    				}
    			},
    		},
    	});
    },
    catchWorkContent:function(){
    	var me=this;
    	var form =Ext.getCmp('form');
    	var id=Ext.getCmp('wd_id').value;
    	form.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + 'oa/persontask/catchWorkContent.action',
			params: {
				id: id,
				caller:'WorkDaily'
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
	},
	onGridItemClick: function(selModel, record){//grid行选择
	   this.GridUtil.onGridItemClick(selModel, record);
	 },
});