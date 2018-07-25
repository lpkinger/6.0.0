Ext.QuickTips.init();
Ext.define('erp.controller.oa.info.PagingSent', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'oa.info.Viewport','common.datalist.GridPanel','common.datalist.Toolbar','oa.info.Forms',
	       'core.trigger.DbfindTrigger','core.form.ConDateField','oa.info.PagingGrid','core.form.FileField','core.form.HrOrgSelectField'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'checkbox[name=only_online]':{
	    			   change:function(field,newvalue){
	    				   var grid=Ext.getCmp('on-line');
	    				   if(newvalue){
                              grid.getStore().filter('ISONLINE',1);
	    				   }else {
	    					   grid.getStore().clearFilter(true);
	    					   grid.getStore().load();
	    				   } 
	    			   }  
	    		   },
	    		   'button[itemId=send]':{
	    			   click:function(btn){
	    				   me.showSendWin();
	    			   }
	    		   },
	    		   'button[id=unread]': {
	    			   click: function(){
	    				   var grid = Ext.getCmp('grid');
	    				   var condition = 'pr_releaserid=' + em_uu + ' AND prd_status=-1';
	    				   grid.getCount('PagingRelease', condition);
	    				   grid.filterCondition = 'pr_releaserid=' + em_uu + ' AND prd_status=-1';
	    			   }
	    		   },
	    		   'button[id=read]': {
	    			   click: function(){
	    				   var grid = Ext.getCmp('grid');
	    				   var condition = 'pr_releaserid=' + em_uu + ' AND prd_status=1';
	    				   grid.getCount('PagingRelease', condition);
	    				   grid.filterCondition = 'pr_releaserid=' + em_uu + ' AND prd_status=1';
	    			   }
	    		   },
	    		   'grid[id=on-line]':{
	    			   'activate':function(grid){
	    				   grid.getStore().load();
	    			   }
	    		   },
	    		   'erpDatalistGridPanel':{
	    			   'activate':function(grid){
	    				   grid.getColumnsAndStore(grid.caller,grid.defaultCondition);
	    			   }
	    		   },
	    		   'button[id=all]': {
	    			   click: function(){
	    				   var grid = Ext.getCmp('grid');
	    				   var condition = 'pr_releaserid=' + em_uu;
	    				   grid.getCount('PagingRelease', condition);
	    				   grid.filterCondition = 'pr_releaserid=' + em_uu;
	    			   }
	    		   },
	    		   'button[itemId=refresh]':{
	    			   click:function(btn){
	    				   window.location.reload();
	    			   }
	    		   }
	    	   });
	       },
	       showSendWin:function(){
	    	   var me=this,win=Ext.getCmp('send-win');
	    	   if(!win){
	    		   win=new Ext.window.Window({
	    			   height:500,
	    			   title:'发送消息',
	    			   width:800,
	    			   layout: 'column',
	    			   modal:true,
	    			   buttonsAlign:'right',
	    			   defaults:{
	    				   columnWidth:1,
	    				   margin:'2 5 2 5'
	    			   },
	    			   items: [{ 
	    				   xtype:'HrOrgSelectfield',
	    				   fieldLabel:'接收人',
	    				   name:'man',
	    				   id:'man',
	    				   logic:'manid',
	    				   secondname:'manid',
	    				   allowBlank:false
	    			   },{
	    				   xtype:'hidden',
	    				   id:'manid',
	    				   name:'manid'
	    			   },{
	    				   xtype:'mfilefield',
	    				   name:'pr_attach'
	    			   },{
	    				   xtype:'htmleditor',
	    				   name:'context',
	    				   height:200,
	    				   allowBlank:false,
	    				   value:''
	    			   }],
	    			   buttons:[{ xtype: 'button', text: '发送',itemId:'sendmsg',formBind: true,width:60,handler:me.sendMsg,scope:this},
	    			            { xtype: 'button', text: '关闭',width:60,handler:function(btn){
	    			            	btn.ownerCt.ownerCt.close();
	    			            }}]
	    		   });

	    	   }
	    	   win.show();
	    	   me.initSendInfo();
	       },
	       sendMsg:function(btn){
	    	   var f=btn.ownerCt.ownerCt,mans=f.down('#manid').getValue(),context=f.down('htmleditor[name=context]'),me=this;
	    	   var value=context.cleanHtml(context.getValue());
	    	   if(!mans || mans==null || mans==''){
	    		   alert('未选择任何消息接受人!');
	    	   }else if( value.trim() ==  ""){
	    		   alert('未填写任务消息内容!');
	    	   }else {
	    		   me.FormUtil.setLoading(true);
	    		   Ext.Ajax.request({
	    			   method:'post',
	    			   url:basePath+"/oa/info/paging.action",
	    			   params:{
	    				   mans:Ext.getCmp('manid').getValue(),
	    				   context:context.getValue()
	    			   },
	    			   callback:function(options,success,response){
	    				   var localJson = new Ext.decode(response.responseText);
	    				   me.FormUtil.setLoading(false);
	    				   if(localJson.success){
	    					   alert('发送消息成功!');
	    					   f.close();
	    				   }
	    			   }

	    		   });
	    	   }
	       },
	       initSendInfo:function(){
	    	   var tabP=Ext.ComponentQuery.query('tabpanel')[0],currentTab=tabP.getActiveTab();
	    	   var selects=this.GridUtil.getGridSelected(currentTab);
	    	   if(selects.length>0){
	    		   var manids=new Array(),mans=new Array();
	    		   Ext.Array.each(selects,function(item){
	    			   manids.push("employee#"+item[currentTab.manidField]);
	    			   mans.push('<font color="#4DB34D">[个人]</font>'+item[currentTab.manField]);
	    		   });
	    		   Ext.getCmp('man').setValue(mans.join(";"));
	    		   Ext.getCmp('manid').setValue(manids.join(";"));
	    	   }

	       }
});