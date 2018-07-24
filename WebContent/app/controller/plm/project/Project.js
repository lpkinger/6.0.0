Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.Project', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.project.Project','core.form.Panel','plm.project.ProjectForm','core.form.CheckBoxGroup','core.form.HrefField',
	       'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Update','core.button.Delete','core.button.ResAudit','core.form.FileField','core.form.MultiField',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.TurnProjectReview','core.button.UpdateRemark','core.grid.ItemGrid','core.trigger.MultiDbfindTrigger'
	       ],
	       init:function(){
	    	   var me = this;
	    	   me.attachcount = 0;
	    	   this.control({ 
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   if(Ext.getCmp('prj_start')&&Ext.getCmp('prj_end')){
	    					   var start=Ext.getCmp('prj_start').value,
	    					   end=Ext.getCmp('prj_end').value,
	    					   organigerdate=Ext.getCmp('prj_organigerdate').value;
	    					   if(end.getTime()<start.getTime()){
	    						   showError('开始日期不能大于完成日期!');
	    						   return;
	    					   } else if(start.getTime()<organigerdate.getTime()){
	    						   showError('发起日期不能大于开始时间');
	    						   return;
	    					   }
	    				   }
	    				   this.save(btn);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   }
	    		   },
	    		   'mfilefield':{
	       			beforerender:function(f){
	       				f.readOnly=false;
	       			}
	       		},
	       		'erpUpdateRemarkButton':{
	       			beforerender:function(btn){
	       				btn.setText('更新机种型号');
	       				var f = Ext.getCmp('prj_sptext70');
	       				var status = Ext.getCmp('prj_statuscode');
	    				if(f&&status && status.value == 'AUDITED'){
	    					f.setReadOnly(false);
	    				}
	    				if(status && status.value != 'AUDITED'){
	    					btn.hide();
	    				}
	    				btn.formBind=false;
	       			},
	       			click:function(btn){
	    				 var prj_sptext70 = Ext.getCmp('prj_sptext70').value;
	    				 var id = Ext.getCmp('prj_id').value;
	    				 Ext.Ajax.request({
	    					   url : basePath + 'plm/project/updateProjectjzxh.action',
	    					   params: {
	    						   id: id,
	    						   prj_sptext70:prj_sptext70,
	    						   caller:caller
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){
	    						   var res = new Ext.decode(response.responseText);
	    						   if(res.exceptionInfo) {
	    							   showError(res.exceptionInfo);
	    						   } else {
	    							   showMessage('提示', '更新机种型号成功!');
	    							   window.location.reload();
	    						   }
	    					   }
	    				   });
	    			 }
	       		},
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('prj_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('prj_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.onDelete(Ext.getCmp('prj_id').value);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addProject', '创建项目', 'jsps/plm/project/project.jsp');
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('prj_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('prj_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('prj_statuscode');
	    				   if(status && status.value !='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('prj_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('prj_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('prj_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton':{
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('prj_statuscode');
	    				   if(status && status.value !='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('prj_id').value);
	    			   }
	    		   },
	    		   'field[name=prj_sourcecode]': {
	      			   afterrender:function(f){
	     				   f.setFieldStyle({
	     					   'color': 'blue'
	     				   });
	     				   f.focusCls = 'mail-attach';
	     				   var c = Ext.Function.bind(me.openSaleProject, me);
	     				   Ext.EventManager.on(f.inputEl, {
	     					   mousedown : c,
	     					   scope: f,
	     					   buffer : 100
	     				   });

	      			   }
	        		},
	        		'textfield[name=prj_customercode]':{
	    				beforerender: function(field){
	    					if(Ext.getCmp('prj_sourcecode') && !Ext.isEmpty(Ext.getCmp('prj_sourcecode').value)){
    							field.setReadOnly(true);
    						}
	    				}
	    			},
	    		   'filefield[id=attach]': {
	    			   change: function(field){
	    				   if(field.value != null){
	    					   var container = Ext.create('Ext.form.FieldContainer', {
	    						   layout: 'hbox',
	    						   fieldLabel: "附件" + (me.attachcount + 1),
	    						   items: [{
	    							   xtype: 'textfield',
	    							   id: 'attach' + me.attachcount,
	    							   flex: 1
	    						   }, {
	    							   xtype: 'progressbar'
	    						   }, {
	    							   xtype: 'button',
	    							   text: '上传',
	    							   id: 'upload' + me.attachcount,
	    							   handler: function(btn){
	    								   var form = btn.ownerCt.ownerCt;
	    								   var f = Ext.getCmp(btn.id.replace('upload', 'attach'));
	    								   if(f.value != null && f.value != ''){
	    									   //field.value = f.value;
	    									   form.getForm().submit({
	    										   url: basePath + 'common/upload.action?em_code=' + em_code,
	    										   waitMsg: "正在上传:" + f.value,
	    										   success: function(fp, o){
	    											   if(o.result.error){
	    												   showError(o.result.error);
	    											   } else {
	    												   Ext.Msg.alert("恭喜", f.value + " 上传成功!");
	    												   btn.setText("上传成功(" + Ext.util.Format.fileSize(o.result.size) + ")");
	    												   btn.disable(true);
	    												   //field.button.disable(false);
	    												   me.files[Number(btn.id.replace('upload', ''))] = o.result.filepath;
	    											   }
	    										   }
	    									   });
	    								   }
	    							   },
	    							   flex: 1
	    						   }, {
	    							   xtype: 'button',
	    							   text: '删除',
	    							   id: 'delete' + me.attachcount,
	    							   handler: function(btn){
	    								   var f = Ext.getCmp(btn.id.replace('delete', 'attach'));
	    								   if(f.value != null && f.value != ''){
	    									   me.files[Number(btn.id.replace('delete', ''))] = '';
	    								   }
	    								   btn.ownerCt.destroy(true);
	    								   me.attachcount--;
	    							   },
	    							   flex: 1
	    						   }]
	    					   });
	    					   if(me.FormUtil.contains(field.value, "\\", true)){
	    						   Ext.getCmp('attach' + me.attachcount).setValue(field.value.substring(field.value.lastIndexOf('\\') + 1));
	    					   } else {
	    						   Ext.getCmp('attach' + me.attachcount).setValue(field.value.substring(field.value.lastIndexOf('/') + 1));
	    					   }
	    					   Ext.getCmp('attachform').insert(3, container);
	    					   me.attachcount++;
	    					   //field.reset();
	    					   //field.button.disable(true);
	    					   field.button.setText("继续...");
	    				   }
	    			   }
	    		   },
	    		   'erpTurnProjectReviewButton':{
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prj_statuscode').getValue();
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var form=me.getForm(btn);
	    				   var id=Ext.getCmp('prj_id').getValue();
	    				   Ext.Ajax.request({
	    					   method:'POST',
	    					   url:basePath+form.turnReviewItemUrl,
	    					   params:{
	    						   id:id
	    					   },
	    					   callback : function(options,success,response){
	    						   var rs = new Ext.decode(response.responseText);
	    						   if(rs.exceptionInfo){
	    							   showError(rs.exceptionInfo);return;
	    						   }
	    						   if(rs.success){
	    							   Ext.Msg.alert('提示','转评审成功!单号为:'+rs.code,function(){
	    								   window.location.reload();
	    							   });
	    						   }
	    					   }
	    				   });
	    			   }
	    		   },

	    		   /**'datefield[name=prj_start]': {
    			change: function(field){
    				var start=Ext.getCmp('prj_start').value,
    				    end=Ext.getCmp('prj_end').value,
    				    organigerdate=Ext.getCmp('prj_organigerdate').value;
    				    if(end!=null||end!=''){
    				      if(end.getTime()<start.getTime()||end.getTime()==start.getTime()){
    				      showError('开始日期不能大于完成日期!');
    				      Ext.getCmp('prj_start').reset();
    				      return
    				      }
    				    }
    				    if(organigerdate!=null||organigerdate!=''){
    				      if(organigerdate.getTime()>start.getTime()){
    				      showError('发起日期不能大于开始日期!');
    				      Ext.getCmp('prj_start').reset();
    				      return
    				      }

    				    }

    			}
    		},
    		'datefield[name=prj_organigerdate]': {
    			change: function(field){
    				var start=Ext.getCmp('prj_start').value,
    				    end=Ext.getCmp('prj_end').value,
    				    organigerdate=Ext.getCmp('prj_organigerdate').value;
    				    if(start!=null||start!=''){
    				      if(end.getTime()<start.getTime()||end.getTime()==start.getTime()){
    				      showError('发起日期不能大于开始日期');
    				      Ext.getCmp('prj_organigerdate').reset();
    				      return
    				      }
    			   }

    			}
    		},
    		'datefield[name=prj_end]': {
    			change: function(field){
    				var start=Ext.getCmp('prj_start').value,
    				    end=Ext.getCmp('prj_end').value,
    				    organigerdate=Ext.getCmp('prj_organigerdate').value;
    				    if(start!=null||start!=''){
    				      if(end.getTime()<start.getTime()||end.getTime()==start.getTime()){
    				      showError('完成日期不能小于开始日期');
    				      Ext.getCmp('prj_end').reset();
    				      }
    			   }

    			}
    		},**/

	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){
	    	   var me = this;
	    	   var form = me.getForm(btn);
	    	   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    		   me.BaseUtil.getRandomNumber();//自动添加编号
	    	   }
	    	   me.FormUtil.beforeSave(me);
	       },
	       openSaleProject: function(e, el, obj) {
		   		var f = obj.scope, form = f.ownerCt,
		   			i = form.down('#sp_id');
		   		if(i && i.value) {
		   			url = 'jsps/scm/sale/saleProject.jsp?formCondition=sp_idIS' + i.value + '&whoami=SaleProject';
		   			openUrl(url);
		   		}
	   	},
});