Ext.QuickTips.init();
Ext.define('erp.controller.opensys.FeedBack', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	currentManfield:null,
	views:[
	       'core.form.Panel2','core.form.MultiField','core.button.Reply',
	       'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ForBidden',
	       'core.form.FileField','opensys.ModifyWindow','opensys.feedback.feedbackbar','core.button.Query',
	       'core.trigger.TextAreaTrigger','core.form.YnField','core.button.Back','core.button.Confirm',
	       ],     
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'erpFormPanel2': {
	    			   afterrender:function(form){
	    				   Ext.defer(function(){
	    					   var fbid=form.down('#fb_id');
	    					   if(fbid && !fbid.getValue()){
	    						   me.setDefaultValue(form);
	    					   }

	    				   },200);
	    			   }
	    		   },			
	    		   'erpSaveButton': {
	    			   click: function(btn){		
	    				   var form = me.getForm(btn);
	    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){								
	    					  console.log(form.codeField);
	    					   
	    					   me.BaseUtil.getRandomNumber();//自动添加编号
	    				   }		
	    				   this.FormUtil.beforeSave(this);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete(Ext.getCmp('fb_id').value);
	    			   }
	    		   },	  
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('fb_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('fb_id').value);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addFeedback', '新增反馈', 'jsps/opensys/FeedBack.jsp?caller=Feedback!Customer');
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },
	    		   'erpQueryButton':{
	    			    click:function(btn){
	    			    	var fbid=Ext.getCmp('fb_id').getValue(),data=me.loadNewStore(fbid);
	    			    	if(data==null || data.length<1){
	    			    		alert('暂无进展!');
	    			    		//return;
	    			    	}else {
	    			    		 var win =  Ext.widget('detailwindow',{
		    			    		   title:'查看进展',
		    			    		   bodyStyle:'background:white;',
		    			    		   layout:'anchor',
		    			    		   width:800,
		    			    		   height:'80%',
		    			    		   autoScroll:true,
		    			    		   items:[Ext.widget('processview',{
		    			    			   anchor:'90% 100%',
		    			    			   showdata:data
		    			    		   })],
		    			    		   buttonAlign:'center',
		    			    		   buttons:[{
		    			    			   text:'关闭',
		    			    			   handler:function(btn){
		    			    				   btn.ownerCt.ownerCt.close();
		    			    			   }
		    			    		   }]
		    			    	   });
		    			    	   win.showRelyBtn(win,btn);
	    			    	}	
	    			    	
	    			    },
	    			    beforerender:function(btn){
	    			    	Ext.apply(btn,{
	    			    		width:80,
	    			    	    text:'查看进展'
	    			    	});
	    			    }
	    		   },
	    		   'erpReplyButton': {
	    			   afterrender: function(btn){				
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   var form= Ext.getCmp('form');					
	    				   Ext.Ajax.request({
	    					   url : basePath + form.replyUrl,
	    					   params: {
	    						   id: Ext.getCmp('fb_id').value,
	    						   comment:Ext.getCmp('fb_uasdetail').getValue(),
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   alert('回复成功');
	    							   window.location.href = basePath + "jsps/sys/Feedback.jsp?caller=UASFeedback&&formCondition=fb_idIS" + Ext.getCmp('ts_id').value;
	    						   }
	    					   }

	    				   });					   
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('fb_id').value);
	    			   }
	    		   },
	    		   'erpBackButton':{
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('fb_statuscode').getValue(),currentposition=Ext.getCmp('fb_position').getValue(),relativefield=null,form=btn.ownerCt;
	    				   if(status!='AUDITED' || currentposition=='End' ) btn.hide();
	    				   if(status=='AUDITED' && currentposition !='End'){
	    					   relativefield=me.RelativeFields[currentposition].plandateField;
	    					   if(!relativefield || (relativefield && Ext.getCmp(relativefield).value)){
	    						   btn.hide();
	    					   }
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var data=me.getDirtyValues(),allowsave=false;
	    				   Ext.each(Ext.Object.getKeys(data), function(k){
	    					   if(me.FormUtil.contains(k, 'ext-', true)){
	    						   delete r[k];
	    					   }
	    					   if(me.FormUtil.contains(k, 'fb_plandate', true)){
	    						   allowsave=true;
	    					   }
	    				   });
	    				   if(!allowsave){
	    					   showError('回复需设置预计完成时间!'); return;
	    				   } 
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/backPlan.action',
	    					   params: {
	    						   data:unescape(escape(Ext.JSON.encode(data)))
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   showMessage('提示','回复成功!',1000);
	    							   window.location.reload();
	    						   }
	    					   }

	    				   });	
	    			   }
	    		   },
	    		   'erpConfirmButton':{
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('fb_statuscode').getValue();
	    				   if(status!='AUDITED') btn.hide();
	    			   },
	    			   click:function(btn){
	    				   var data=me.getDirtyValues();
	    				   Ext.each(Ext.Object.getKeys(data), function(k){
	    					   if(me.FormUtil.contains(k, 'ext-', true)){
	    						   delete r[k];
	    					   }
	    				   });
	    				  /* var position=Ext.getCmp('fb_position').getValue();
	    				   if(position!='End'){
	    					   warnMsg('确认当前问题已处理了吗?',function(b){
	    						   if(b == 'yes'){
	    							   me.confirm(btn);
	    						   } else {
	    							   return;
	    						   }
	    					   });
	    				   }else*/ me.confirm(btn);

	    			   }
	    		   }
	    	   });   
	       },
	       confirm:function(btn){
	    	   var win =  Ext.widget('detailwindow',{
	    		   title:'确认问题',
	    		   items:[Ext.widget('form',{	    			 
	    			   bodyStyle : 'background:#f9f9f9;padding:15px 15px 0',
	    			   fieldDefaults : {
	    				   msgTarget: 'none',
	    				   blankText : $I18N.common.form.blankText,
	    				   cls:'single-field'
	    				   
	    				   /*labelStyle: 'color:green;background:red;padding-left:4px'*/
	    			   },
	    			   items:[{
	    				   fieldLabel:'已处理',
	    				   name:'fb_backresult',
	    				   xtype:'checkbox',
	    				   style:'padding-left:5px',
	    				   inputValue:-1,
	    				   checked:true
	    			   },{
	    				   xtype: 'radiogroup',
	    				   fieldLabel: '满意度',				  
	    				   items: [{boxLabel: '非常满意', name: 'fb_backlevel', inputValue: 1},
	    				           {boxLabel: '满意', name: 'fb_backlevel', inputValue: 2, checked: true},
	    				           {boxLabel: '不满意', name: 'fb_backlevel', inputValue: 3}]
	    			   },{
	    				   xtype:'textarea',
	    				   fieldLabel:'备注信息',
	    				   width:480,
	    				   maxLength:300,
	    				   maxLengthText:'输入太长了!',
	    				   name:'fb_backdescription',
	    				   emptyText:'针对该问题处理过程提出建议',
	    				   fieldStyle:'background:white repeat-x 0 0;border-width: 1px;border-style: solid;'
	    			   }]								
	    		   })],
	    		   buttonAlign:'center',
	    		   buttons:[{
	    			   text:'确认',
	    			   handler:function(btn){
	    				   var f=btn.ownerCt.ownerCt.down('form'),vals=f.getValues();
	    				   vals['fb_position']=Ext.getCmp('fb_position').getValue();
	    				   vals['fb_kind']=Ext.getCmp('fb_kind').getValue();
	    				   vals['fb_id']=Ext.getCmp('fb_id').getValue();
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/confirm.action',
	    					   params: {
	    						   data:unescape(escape(Ext.JSON.encode(vals))),
	    						   _customer:1,
	    						   _process:1
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   showMessage('提示','确认成功!',1000);
	    							   window.location.reload();
	    						   }
	    					   }

	    	    	    });	
	    			   }
	    		   },{
	    			   text:'取消',
	    			   handler:function(btn){
	    				   btn.ownerCt.ownerCt.close();
	    			   }
	    		   }]
	    	   });
	    	   win.showRelyBtn(win,btn);
	    	  
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },	
	       setDefaultValue: function(form) {
	    	   var result = false;
	    	   Ext.Ajax.request({
	    		   url : basePath + 'common/getFieldsData.action',
	    		   params: {
	    			   caller: 'Customer',
	    			   fields: 'cu_code,cu_name,cu_contact,cu_tel,cu_mobile,cu_tel,cu_email',
	    			   condition: 'cu_uu='+enUU
	    		   },
	    		   method : 'post',
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   } else if(r.success && r.data){
	    				   var record=new Object();
	    				   form.down("#fb_custcode").setValue(r.data.cu_code?r.data.cu_code:null);
	    				   form.down("#fb_custname").setValue(r.data.cu_name?r.data.cu_name:null);
	    				   form.down("#fb_linkman").setValue(r.data.cu_contact?r.data.cu_contact:null);
	    				   form.down("#fb_tel").setValue((r.data.cu_tel||r.data.cu_mobile) ?(r.data.cu_tel||r.data.cu_mobile):null);
	    				   form.down("#fb_email").setValue(r.data.cu_email?r.data.cu_email:null);
	    			   }
	    		   }
	    	   });
	       },
	       getDirtyValues:function(){
	    	   var form=Ext.getCmp('form'),values=this.getEditValues(form);		
	    	   values.fb_id=Ext.getCmp('fb_id').getValue();
	    	   values.fb_position=Ext.getCmp('fb_position').getValue();
	    	   values.fb_kind=Ext.getCmp('fb_kind').getValue();
	    	   return values;
	       },
	       getEditValues:function(form){
	    	   var values=new Object(),formvalues=form.getForm().getValues();
	    	   Ext.Array.each(form.items.items,function(item){
	    		   if(!item.readOnly && item.name && item.value){
	    			   values[item.name]=formvalues[item.name];
	    		   }
	    	   });
	    	   return values;
	       },
	       loadNewStore: function(id){
	    	   var data=null;
	    	   Ext.Ajax.request({//拿到grid的columns
	    		   url : basePath + "common/loadNewGridStore.action",
	    		   params: {
	    			   caller:'Feedback',
	    			   condition:'fl_fbid='+id +' order by fl_date desc'
	    		   },
	    		   async:false,
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   var res = new Ext.decode(response.responseText);
	    			   if(res.exceptionInfo){
	    				   showError(res.exceptionInfo);return;
	    			   }
	    			   data = res.data;

	    		   }
	    	   });
	    	   return data;
	       }

});