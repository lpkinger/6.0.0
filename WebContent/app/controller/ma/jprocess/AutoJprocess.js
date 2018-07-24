Ext.QuickTips.init();
Ext.define('erp.controller.ma.jprocess.AutoJprocess', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','ma.jprocess.AutoJprocess','core.toolbar.Toolbar','core.form.FileField','erp.view.ma.jprocess.MultiField',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.UpdateProcessInfo',
	       'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.form.HrOrgSelectField',
	       'core.trigger.AddDbfindTrigger','ma.jprocess.JNodeManSetField',
	       'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.FileField'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'erpFormPanel' :{
	    			   afterload:function(form){
	    				   var status = Ext.getCmp('ap_statuscode');
	    				   me.optButtons(form);
	    				   if(type!=1){
	    					   me.getFieldsData(form);
	    				   }
	    				   var nodecounts=Ext.getCmp('ap_nodecounts');	    			
	    				   if(nodecounts.value!=0 && status.value == 'ENTERING'){
	    					   nodecounts.setReadOnly(false);
	    				   }
	    			   }  
	    		   },
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   this.save(btn);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete({id: Number(Ext.getCmp('ap_id').value)});
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   this.update(btn);
	    			   }
	    		   },
	    		   'htmleditor':{
	    			   beforerender: function(field){
	    				   if(!field.autoHeight) field.height=window.innerHeight*0.88;
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addJprocessTemplate', '自定流程模板', 'jsps/ma/jprocess/JprocessTemplateSet.jsp');
	    			   }
	    		   },
	    		   'hidden[name=ap_type]':{
	    			   change:function(field){	  
	    				   if(field.value=='commonuse'){
	    					   var counts=Ext.getCmp('ap_nodecounts');
	    					   counts.setReadOnly(false);    					
	    					   counts.setValue('');
	    				   }
	    			   }
	    		   },
	    		   'combo[name=ap_nodecounts]':{
	    			   change:function(field,newvalue,oldvalue){
	    				   if(newvalue!=0){
	    					   var nodeset=Ext.getCmp('ap_nodeman');
	    					   nodeset.show();
	    					   nodeset.addItem(newvalue);
	    				   }
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },   	
	    		   'erpSubmitButton': {
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('ap_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('ap_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('ap_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('ap_id').value);
	    			   }
	    		   }, 
	    		   '#clobtext':{
	    			   activate:function(tab){
	    				   if(!tab.items.items[0].value){
	    					   var value=Ext.getCmp('ap_text').value;
	    					   tab.items.items[0].setValue(value);
	    				   }
	    			   }
	    		   },
	    		   '#workflow':{
	    			   activate:function (tab){
	    			   	 if(tab.items.length==0){
	    				   var caller=Ext.getCmp('ap_caller').value;
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
	    						   html:'<h2>你还没有设置个人导航流程或系统没有定义相应的导航图</br><a class="x-btn-link" onclick="openTable('+'\''+title+'\',\''+url+'\''+ ');">快去看看吧!</a></h2>'
	    					   });
	    				   }
	    				   tab.add(items);
	    			   }}
	    		   }
	    	   });
	       }, 
	       getFieldsData : function(form) {
	    	   var me = this;
	    	   var queryFields= 'pt_name,pt_kind,pt_feedbackman,pt_canupdatefeedbackman,'
	    		   +'pt_flowobjects,pt_flowobjectsid,pt_readobjects,pt_readobjectsid,pt_text,pt_caller,pt_type';
	    	   Ext.Ajax.request({
	    		   url : basePath + 'common/getFieldsData.action',
	    		   async:false,
	    		   params: {
	    			   caller: 'JprocessTemplate',
	    			   fields:queryFields,
	    			   condition:formCondition
	    		   },
	    		   method : 'post',
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   }
	    			   if(r.success && r.data){
	    				   var keys = Ext.Object.getKeys(r.data);
	    				   var o=new Object();
	    				   Ext.each(keys, function(k){
	    					   o[k.replace('pt_','ap_')]=r.data[k];
	    				   });
	    				   form.getForm().setValues(o);
	    			   }
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
	    	   var form = me.getForm(btn);
	    	   if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {	    		   
	    		   me.BaseUtil.getRandomNumber(CodeCaller); //自动添加编号
	    	   }
	    	   var r=form.getValues();
	    	   Ext.each(Ext.Object.getKeys(r), function(k){
	    		   if(contains(k, 'ext-', true)){
	    			   delete r[k];
	    		   }
	    	   });
	    	   var clobtext=Ext.getCmp('clobtext').items.items[0].getValue();
	    	   delete r['ap_text'];
	    	   me.checkNodeManValue(r);
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
	    				   showMessage('提示', '保存成功!', 1000);
	    				   var value = r[form.keyField];
	    				   var formCondition = form.keyField + "IS" + value ;
	    				   var gridCondition = '';
	    				   var grid = Ext.getCmp('grid');
	    				   if(grid && grid.mainField){
	    					   gridCondition = grid.mainField + "IS" + value;
	    				   }
	    				   window.location.href = basePath+'/jsps/ma/jprocess/AutoJprocess.jsp?type=1&formCondition=' + 
	    				   formCondition + '&gridCondition=' + gridCondition+'&datalistId='+datalistId;

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
	    	   me.checkNodeManValue(r);
	    	   var clobtext=Ext.getCmp('clobtext').items.items[0].getValue();
	    	   delete r['ap_text'];
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
	       checkNodeManValue:function(r){
	    	   var codes=r['nodedealmancode'];
	    	   var names=r['nodedealman'];
	    	   var str='';
	    	   if(Ext.isArray(codes)){
	    		   Ext.Array.each(codes,function(code,index){
	    			   if(code==''){
	    				   return '请填写相关的节点处理人!';
	    			   } 
	    			   str+=code+"#"+names[index]+";";
	    		   });
	    	   }else str=codes+"#"+names+";";
	    	   delete r['nodedealmancode'];
	    	   delete r['nodedealman'];
	    	   r['ap_nodeman']=str.substring(0,str.lastIndexOf(';'));
	    	   return  true;
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
	       addItems:function(newvalue,oldvalue){
	    	   var form = Ext.getCmp('form');
	    	   var con=null;
	    	   var containers=form.query('fieldcontainer');
	    	   if(containers.length>0){
	    		   con=containers[0];
	    		   con.removeAll();
	    	   }else {
	    		   con={
	    				   xtype : 'fieldcontainer',
	    				   fieldLabel : '处理人',
	    				   name : 'resourcename',
	    				   columnWidth : 1,
	    				   layout : 'hbox',
	    				   defaults : {
	    					   margin : '0 2 0 2'
	    				   }	
	    		   };

	    	   }
	    	   var arr=new Array();
	    	   for(var i=1;i<newvalue+1;i++){
	    		   arr.push({
	    			   xtype : 'dbfindtrigger',
	    			   name : 'ma_recorder',
	    			   isFormField : false,
	    			   margin : '0 2 0 4',
	    			   labelWidth : 40,
	    			   fieldLabel : '节点'+i,
	    			   listeners : {
	    				   aftertrigger : function(t, r) {
	    					   t.setValue(r.get('em_name'));
	    				   }
	    			   }
	    		   });
	    	   }
	    	   con.add(arr);
	    	   form.add(con);
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
	    	   return Ext.getCmp('form');
	       },
	       optButtons:function(form){
	    	   var toolbar=form.ownerCt.ownerCt.dockedItems.items[0];
	    	   var status = Ext.getCmp('ap_statuscode');
	    	   if(type==1){
	    		   if(status && status.value == 'ENTERING'){
	    			   toolbar.getComponent('update').show();
	    			   toolbar.getComponent('delete').show();
	    			   toolbar.getComponent('submit').show();
	    		   }else if(status && status.value == 'COMMITED'){
	    			   toolbar.getComponent('resSubmit').show();
	    			   toolbar.getComponent('audit').show();
	    		   }else if(status && status.value == 'AUDITED'){
	    			   toolbar.getComponent('resAudit').show();
	    		   }
	    	   }
	       }
});