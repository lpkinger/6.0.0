Ext.QuickTips.init();
Ext.define('erp.controller.common.JProcessDeal', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','common.JProcess.Viewport','core.grid.Panel','core.button.Add','core.button.Submit','core.button.Audit',
    		'core.button.Save','core.button.Close','core.button.Print','core.button.Upload','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField','core.form.FileField'
    	],
    init:function(){
    	var me = this;
    	/*formCondition = this.BaseUtil.getUrlParam('formCondition');
    	console.log(formCondition);*/
    	
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.saveEmployee(btn);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.FormUtil.onUpdate(me.getForm(btn), 'hr/employee/update.action', []); // 后台未写……
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				var params = {
				   			//pr_id: Number(Ext.getCmp('pr_id').value)
			   		};
    				me.FormUtil.onUpdate('hr/employee/delete.action', params);  // 后台未写……
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addEmployee', '新增员工', 'jsps/hr/employee/employee.jsp');
    			},
    			afterrender:function(btn){
    				alert('sdsd');
    			}
    		},
    		/*'textareatrigger':{
    			 onTriggerClick: function() {
				    						
				   },
    		},*/
    		'erpCloseButton': {
    			click: function(btn){
    				var s = me.FormUtil.checkFormDirty(me.getForm(btn));
    				if(s == ''){
    					me.FormUtil.onClose();
    				} else {
    					if(!formCondition){//单据新增界面哦
    						//关闭前保存新增的数据
    						Ext.MessageBox.show({//关闭前保存修改的数据
       					     	title:'保存新添加的数据?',
       					     	msg: '详细:<br/>' + s + '<br/>离开前要保存吗？',
       					     	buttons: Ext.Msg.YESNOCANCEL,
       					     	icon: Ext.Msg.WARNING,
       					     	fn: function(btn){
       					     		if(btn == 'yes'){
       					     			me.saveProduct(btn);//什么情况，没有saveProduct啊   2013-3-6 20:03:17 wuw
       			    				} else if(btn == 'no'){
       			    					me.FormUtil.onClose();
       			    				} else {
       			    					return;
       			    				}
       					     	}
    						});
    					} else {//单据查看界面哦
    						Ext.MessageBox.show({
       					     title:'保存修改?',
       					     msg: '该单据已被修改:<br/>' + s + '<br/>离开前要保存吗？',
       					     buttons: Ext.Msg.YESNOCANCEL,
       					     icon: Ext.Msg.WARNING,
       					     fn: function(btn){
       					    	 if(btn == 'yes'){
       					    		 me.FormUtil.onUpdate(me.getForm(btn), 'scm/product/update.action', []);
       			    				} else if(btn == 'no'){
       			    					me.FormUtil.onClose();
       			    				} else {
       			    					return;
       			    				}
       					     }
       					});
    					}
    				}
    			},
    			afterrender:function(btn){
    				var form=me.getForm(btn);
    				Ext.Array.each(form.items.items,function(item){
    					item.setReadOnly(true);
    				});
    				var status=Ext.getCmp('jp_status');
    				if(status&&status.getValue()=='未通过'){
    				var toolbar=Ext.getCmp('container5');
    				Ext.Array.each(toolbar.items.items,function(item){
    					if(item.xtype=='button'){
    						item.setDisabled(true);
    					}
    				});
    				var assign=Ext.getCmp('alterAssignee');
    				assign.setDisabled(true);
    			}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	saveEmployee: function(btn){
		var me = this;
		if(Ext.getCmp('em_code').value == null || Ext.getCmp('em_code').value == ''){
			Ext.getCmp('em_code').setValue(me.BaseUtil.getRandomNumber(em_uu));
		}
		me.FormUtil.onSave(me.getForm(btn), 'hr/employee/save.action', []);
	}                                        
});