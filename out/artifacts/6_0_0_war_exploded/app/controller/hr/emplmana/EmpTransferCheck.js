Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.EmpTransferCheck', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'hr.emplmana.EmpTransferCheck','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
   		'core.button.Update','core.button.Delete','core.form.YnField','core.button.EmpTransferCheck',
   		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'
   	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			//itemclick: this.onGridItemClick
    		},
    		'erpEmpTransferCheckButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp('ec_statuscode');
					if((status && status.value != 'ENTERING')){
						btn.hide();
					}
    			},
    			click:function(btn){
    				var check=Ext.getCmp('ec_check').value;
    				if(check){
    					warnMsg("此张单据已检测，重新检测会清除明细数据，是否重新检测?", function(btn){
		    					if(btn == 'yes'){
		    						me.check();
		    					}
		    			});
    				}else{
    					me.check();
    				}
    				
    			}
    		},
    		'field[name=ec_codevalue]': {
	 			   afterrender:function(f){
	 					 f.setFieldStyle({
		   					 'color': 'blue'
		   				  });
		   				  f.focusCls = 'mail-attach';
		   				   var c = Ext.Function.bind(me.openRelative, me);
		   				   Ext.EventManager.on(f.inputEl, {
		   					   mousedown : c,
		   					   scope: f,
		   					   buffer : 100
		   				   });
	 			   }
	   		},
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
    		'erpUpdateButton': {
    			afterrender:function(btn){
    				if(Ext.getCmp('ec_check').value==0){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ec_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('EmpTransferCheck', '新增员工异动工作交接', 'jsps/hr/emplmana/employee/empTransferCheck.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ec_statuscode');
					if((status && status.value != 'ENTERING')||Ext.getCmp('ec_check').value==0){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ec_id').value,true);
					/*var grid = Ext.getCmp('grid');
					var check=Ext.getCmp('ec_check').value;
					var flag=true;
					var param=grid.GridUtil.getAllGridStore(grid);*/
					/*if((param == null || param == '')&&check!=0){
						me.FormUtil.onSubmit(Ext.getCmp('ec_id').value,true);
					}else{
						Ext.Array.each(grid.store.data.items, function(item){
	    					if(item.data['ecd_change']==0){
	    						flag=false;
	    						return false;
	    					}
	    				});
	    				if(flag){
	    					me.FormUtil.onSubmit(Ext.getCmp('ec_id').value);
	    				}else{
	    					showError('明细中仍存在未变更数据，不能提交');
	    				}
					}*/
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ec_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ec_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ec_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ec_id').value);
				}
			}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	check:function(){
		var me=this;
		var id=Ext.getCmp('ec_id').value;
    	var form =Ext.getCmp('form');
    	form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'hr/emplmana/check.action?caller=EmpTransferCheck',
			 params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					window.location.href = window.location.href + '&formCondition=ec_idIS' +id 
					+ '&gridCondition=ecd_ecidIS' + id;
				}
			}
		});
	},
	openRelative:function(e, el, obj){
			if(Ext.getCmp('ec_caller').value==''||Ext.getCmp('ec_keyvalue').value=='')return;
			var ec_caller=Ext.getCmp('ec_caller').value;
			var ec_keyvalue=Ext.getCmp('ec_keyvalue').value;
			var url='',name='',title='';
			if(ec_caller && (ec_caller=='Turnposition' || ec_caller.indexOf('Turnposition!') == 0)){
				name='Turnposition';
				title='员工调动申请';
				url='jsps/hr/emplmana/employee/turnposition.jsp?formCondition=tp_idIS'+ec_keyvalue+'' +
						'&gridCondition=td_tpidIS' +ec_keyvalue;
				this.FormUtil.onAdd(name, title, url);
			}else if( ec_caller && ( ec_caller=='Turnover' || ec_caller.indexOf('Turnover!') == 0)){
				name='Turnover';
				title='人员离职申请';
				url='jsps/hr/emplmana/employee/turnover.jsp?formCondition=to_idIS'+ec_keyvalue+'' +
						'&gridCondition=td_tpidIS'+ec_keyvalue;
				this.FormUtil.onAdd(name, title, url);
			}
		}
});