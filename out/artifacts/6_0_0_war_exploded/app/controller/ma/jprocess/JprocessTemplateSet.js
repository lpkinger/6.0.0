Ext.QuickTips.init();
Ext.define('erp.controller.ma.jprocess.JprocessTemplateSet', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','ma.jprocess.JprocessTemplateSet','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
	       'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','oa.doc.OrgTreePanel',
	       'core.button.Consign','core.button.End','core.button.ResEnd','core.trigger.MultiDbfindTrigger','core.form.HrOrgSelectField',
	       'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.form.FileField'

	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   this.save(btn);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete({id: Number(Ext.getCmp('pt_id').value)});
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pt_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.update(btn);
	    			   }
	    		   },
	    		   'htmleditor':{
	    			   beforerender: function(field){
	    				   if(!field.autoHeight) field.height=500;
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addJprocessTemplate', '自定流程模板', 'jsps/ma/jprocess/JprocessTemplateSet.jsp');
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },   	
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pt_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('pt_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pt_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('pt_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pt_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('pt_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pt_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('pt_id').value);
	    			   }
	    		   }, 
	    		   'erpFeatureDefinitionButton':{
	    			   click: function(btn){
	    				   var grid = Ext.getCmp('grid');
	    				   var record = grid.selModel.lastSelected;
	    				   if(record.data.sd_prodcode != null){
	    					   Ext.Ajax.request({//拿到grid的columns
	    						   url : basePath + "pm/bom/getDescription.action",
	    						   params: {
	    							   tablename: 'Product',
	    							   field: 'pr_specvalue',
	    							   condition: "pr_code='" + record.data.sd_prodcode + "'"
	    						   },
	    						   method : 'post',
	    						   async: false,
	    						   callback : function(options,success,response){
	    							   var res = new Ext.decode(response.responseText);
	    							   if(res.exceptionInfo){
	    								   showError(res.exceptionInfo);return;
	    							   }
	    							   if(res.success){
	    								   if(res.description != '' && res.description != null && res.description == 'NOTSPECIFIC'){
	    									   var win = new Ext.window.Window({
	    										   id : 'win',
	    										   title: '生成特征料号',
	    										   height: "90%",
	    										   width: "95%",
	    										   maximizable : true,
	    										   buttonAlign : 'center',
	    										   layout : 'anchor',
	    										   items: [{
	    											   tag : 'iframe',
	    											   frame : true,
	    											   anchor : '100% 100%',
	    											   layout : 'fit',
	    											   html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
	    											   "jsps/pm/bom/FeatureValueSet.jsp?fromwhere=SaleDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
	    										   }]
	    									   });
	    									   win.show();    									
	    								   } else {
	    									   showError('物料特征必须为虚拟特征件');return;
	    								   }
	    							   }
	    						   }
	    					   });
	    				   }
	    			   }
	    		   },
	    		   '#workflow':{
	    			   activate:function (tab){
	    			  if(tab.items.length==0){
	    				   var caller=Ext.getCmp('pt_caller').value;
	    				   var bool=me.isHaveFlow(caller);
	    				  var items=new Array();
	    				   if(!bool){
	    					   items.push({
	    						   tag : 'iframe',
	    						   style:{
	    							   background:'#f0f0f0',
	    							   border:'none'
	    						   },						  
	    						   frame : true,
	    						   border : false,
	    						   layout : 'fit',
	    						   height:window.innerHeight*0.9,
	    						   iconCls : 'x-tree-icon-tab-tab',
	    						   html : '<iframe id="iframe_maindetail_" src="'+basePath+'workfloweditor/workfloweditor2.jsp?caller='+caller+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'	
	    					   });
	    				   }else {
	    					   var url="workfloweditor/workfloweditor.jsp";    					 
	    					   items.push({
	    						   title:'查看流程',
	    						   frame:true,
	    						   html:'<h2>你还没有设置个人导航流程或系统没有定义相应的导航图</br><a class="x-btn-link" onclick="openTable('+'\''+"查看流程"+'\',\''+url+'\''+ ');">快去看看吧!</a></h2>'	    						 
	    					   });
	    				   }
	    				   tab.add(items);
	    			   }}
	    		   }
	    	   });
	       }, 
	       save:function(btn){
	    	   var me=this;
	    	   var form = me.getForm(btn);
	    	   if(! this.FormUtil.checkForm()){
	    		   return;
	    	   }
	    	   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
	    		   this.FormUtil.getSeqId(form);
	    	   }
	    	   var r=form.getValues();
	    	   Ext.each(Ext.Object.getKeys(r), function(k){
	    		   if(contains(k, 'ext-', true)){
	    			   delete r[k];
	    		   }
	    	   });
	    	   var clobtext=r["pt_text"];
	    	   delete r['pt_text'];
	    	   form.setLoading(true);//loading...
	    	   Ext.Ajax.request({
	    		   url : basePath + form.saveUrl,
	    		   params : {
	    			   formStore: unescape(Ext.JSON.encode(r)),
	    			   clobtext:clobtext
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   form.setLoading(false);
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   showMessage('提示','保存成功!',1000);
	    				   var value = r[form.keyField];
	    				   var formCondition = form.keyField + "IS" + value ;
	    				   window.location.href = window.location.href + '?formCondition=' +formCondition;
	    			   } else if(localJson.exceptionInfo){
	    				   var str = localJson.exceptionInfo;
	    				   if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	    					   str = str.replace('AFTERSUCCESS', '');
	    					   showError(str);
	    				   } else {
	    					   showError(str);
	    					   return;
	    				   }
	    			   } else{
	    				   saveFailure();
	    			   }
	    		   }

	    	   });
	       },
	       update:function(btn){
	    	   var me=this;
	    	   var form = me.getForm(btn);
	    	   if(! this.FormUtil.checkForm()){
	    		   return;
	    	   }
	    	   var r=form.getValues();
	    	   Ext.each(Ext.Object.getKeys(r), function(k){
	    		   if(contains(k, 'ext-', true)){
	    			   delete r[k];
	    		   }
	    	   });
	    	   var clobtext=r["pt_text"];
	    	   delete r['pt_text'];
	    	   form.setLoading(true);//loading...
	    	   Ext.Ajax.request({
	    		   url : basePath + form.updateUrl,
	    		   params : {
	    			   formStore: unescape(Ext.JSON.encode(r)),
	    			   clobtext:clobtext
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   form.setLoading(false);
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   showMessage('提示', '保存成功!', 1000);
	    				   window.location.reload();
	    			   } else if(localJson.exceptionInfo){
	    				   var str = localJson.exceptionInfo;
	    				   if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	    					   str = str.replace('AFTERSUCCESS', '');
	    					   showMessage('提示', '保存成功!', 1000);
	    					   window.location.reload();
	    					   showError(str);
	    				   } else {
	    					   showError(str);
	    					   return;
	    				   }
	    			   } else{
	    				   saveFailure();
	    			   }
	    		   }

	    	   });
	       },
	       isHaveFlow:function(caller){
	    	   var bool=false;
	    	   Ext.Ajax.request({
	    		   url : basePath + 'common/checkFieldData.action',
	    		   async: false,
	    		   params: {
	    			   caller: 'JprocessDeploy',
	    			   condition: 'jd_caller=\'' + caller + '\''
	    		   },
	    		   method : 'post',
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   } else if(r.success && r.data){
	    				   bool=r.data;	
	    			   }
	    		   }
	    	   });
	    	   return bool;
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   if(record.data['sd_statuscode']=='AUDITED'){
	    		   var turnMake=Ext.getCmp('turnmake');
	    		   if(turnMake){
	    			   turnMake.setDisabled(false);
	    		   }
	    	   }
	    	   if(!selModel.ownerCt.readOnly){
	    		   this.GridUtil.onGridItemClick(selModel, record);
	    	   }

	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       }
});