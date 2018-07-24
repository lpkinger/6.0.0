Ext.QuickTips.init();
Ext.define('erp.controller.oa.info.PagingGet', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:['oa.info.PagingGet','core.form.FileField','core.form.HrOrgSelectField','common.datalist.GridPanel','common.datalist.Toolbar'],
	init:function(){
		var me = this;
		
		this.control({
			'htmleditor[id=readEl]':{
				afterrender:function(editor){
					editor.getToolbar().hide();
					editor.setHeight(300);
				}
			},
			'htmleditor[id=replyEl]':{
				afterrender:function(editor){					
					editor.setHeight(300);
				}
			},
			'HrOrgSelectfield>htmleditor':{
				beforerender:function(field){
					Ext.apply(field,{
						height:30,
						fieldLabel:'<div style="margin-top:15px">接收人:</div>',
						labelStyle:'margin-right:2px !important;',
						style: {
							background: 'transparent'
						},
						hideBorders: true,
						fieldBodyCls:'x-editor'
					});
				},
				afterrender:function(editor){
					editor.getToolbar().hide();
				}
			},
			'htmleditor[name=man]':{
				afterrender:function(editor){
					editor.getToolbar().hide();
					editor.setHeight(30);
				}
			},
			'button[itemId=saveTask]':{
				click:function(){
					me.saveScheduleTask();
				}
			},
			'button[itemId=reply]':{
				click:function(btn){
					me.showSendTab();
					me.setReplyMan();
				}
			},
			'button[itemId=close]':{
				click:function(btn){
					window.close();
				}
			},
			'button[itemId=send]':{
				click:function(btn){
					var f=btn.ownerCt.ownerCt,mans=Ext.getCmp('manid').getValue(),context=f.down('htmleditor[name=replycontext]');
					var value=context.cleanHtml(context.getValue());
					if(!mans || mans==null || mans==''){
						alert('未选择任何消息接受人!');
					}else if( value.trim() ==  ""){
						alert('未填写任务消息内容!');
					}else {
						me.sendMsg(f);
					}
				}
			},
			'button[itemId=turnOver]':{
				click:function(btn){
					me.showSendTab();
				}
			}			
		});
	},
	showSendTab:function(){
		var tabP=Ext.getCmp('msgTab'),msgTab=tabP.items.items[0],replyTab=tabP.items.items[1];
		var tabbar=tabP.getTabBar(); 
		tabbar.items.items[1].show();
		replyTab.show();
		tabP.remove(msgTab);
	},
	setReplyMan:function(){
		Ext.getCmp('manid').setValue("employee#"+data.PR_RELEASERID);
		Ext.getCmp('man').setValue('<font color="#4DB34D">[个人]</font>'+data.PR_RELEASER);
	},
	sendMsg:function(f){
		var data=f.getForm().getValues(),me=this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			method:'post',
			url:basePath+"/oa/info/paging.action",
			params:{
				mans:data.manid,
				context:data.replycontext
			},
			callback:function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				me.FormUtil.setLoading(false);
				if(localJson.success){
					alert('发送成功!');
					window.close();
				}
			}

		});
	},
	saveScheduleTask:function(){
		var me=this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			method:'post',
			url:basePath+"plm/task/addScheduleTask.action",
			params:{
				title:data.PR_RELEASER,
				context:data.PR_CONTEXT
			},
			callback:function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				me.FormUtil.setLoading(false);
				if(localJson.success){
					alert('发送成功!');
					window.close();
				}
			}

		});
	}
});