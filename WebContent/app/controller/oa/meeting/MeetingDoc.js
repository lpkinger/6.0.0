Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.MeetingDoc', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.meeting.MeetingDoc','core.form.Panel','core.form.FileField','core.button.MeetingSignIn',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.button.Upload','core.button.DownLoad','core.form.HrOrgSelectField','core.form.ConDateHourMinuteField'
    			
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addMeetingDoc', '新增会议纪要', 'jsps/oa/meeting/meetingDoc.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('md_id').value);
    			}
    		},
      		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('md_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('md_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('md_id').value);
    			}
    		},
    		'erpPrintButton': {
                click: function(btn) {
                	 var id = Ext.getCmp('md_id').value;
                     me.FormUtil.onwindowsPrint2(id, "", "");
                }
            },
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('md_id').value);
    			}
    		},
    		'htmleditor[name=md_contents]': {
    			initialize:function(f){
    			   var iframe=document.getElementById('md_contents').getElementsByTagName("iframe")[0];
    			   if(iframe.contentWindow.document.body.childNodes.length>0){
    				   iframe.scrolling="yes";
           			   var body=iframe.contentWindow.document.body;
       				   var child=body.childNodes;
       				   var h=0;
       				   for(var i=0;i<child.length;i++){
       					   if(child[i].offsetHeight){
       						h+=child[i].offsetHeight;
       						if(child[i].nodeName!='SPAN'){
       							h+=14;
       						}
       					   }else if(child[i].nodeName!='BR'){
       						   h+=20;
       					   }
       				   }   
       				   h+=100;
       				   if(h<500){
       					 f.setHeight(520);
       				   }else{
       					//f.setHeight(h);
       				   	f.setHeight(520);
       				   }
    			   }else{
    				   f.setHeight(520);
    			   }
    			}
    			/*afterrender: function(f){
    				
    				//f.setHeight(500);
    				
    			}*/
    		},
    	/*	'dbfindtrigger[name=md_mtname]': {
    			beforetrigger: function(f){
    				warnMsg("确定要重新载入吗?", function(btn){
    					console.log('a');
    					if(btn != 'yes'){
    						console.log('b');
    						return;
    					}
    				});
    			}
    		},*/
    		'dbfindtrigger[name=md_meetingcode]': {
    			aftertrigger: function(f){
    				me.FormUtil._getFieldValues('MeetingDetail left join Meeting on md_meid=me_id', 
    						'md_participants', "md_isconfirmed=-1 AND me_code='" + f.value + "'", 'md_meetingparticipants');
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});