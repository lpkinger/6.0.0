Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Employee', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.Employee','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Sync','core.button.ResAudit','core.button.Submit',
  			'core.button.Audit','core.button.Update','core.button.Delete','core.form.YnField', 'core.trigger.MasterTrigger',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.PhotoField','core.button.ResSubmit'
    	],
    refs : [ {
    	ref : 'grid',
    	selector : 'gridpanel'
    }],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpFormPanel textfield[name=em_code]': {
    			change: function(field){
    				if(field.value != em_code){//只能看见自己的密码
    					if(Ext.getCmp('em_type').value != 'admin'){
    						Ext.getCmp('em_password').hide();
    					}
    				}
    			}
    		},
    		'field[name=em_password]': {
    			afterrender: function(f){
    				f.hide();
    				//f.el.dom.getElementsByTagName('input')[0].type = "password";
    			}
    		},
    		'field[name=em_mailpassword]': {
    			afterrender: function(f){
    				f.el.dom.getElementsByTagName('input')[0].type = "password";
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				var em_code = form.down("#em_code").value;
    				var em_name = form.down("#em_name").value;
    				var bool = me.checkEmcode(em_code,em_name);
    				if(bool || bool=="true"){
    					Ext.Msg.confirm('提示','其他账套存在员工编号相同姓名不同的人员资料，是否将姓名覆盖到其他账套',function(btn){
    						if(btn=='yes'){
    							me.FormUtil.beforeSave(me);
    						}
    					});
    				}else{
    					me.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var em_code = Ext.getCmp("em_code").value;
    				var em_name = Ext.getCmp("em_name").value;
    				var bool = me.checkEmcode(em_code,em_name);
    				if(bool || bool=="true"){
    					Ext.Msg.confirm('提示','其他账套存在员工编号相同姓名不同的人员资料，是否将姓名覆盖到其他账套',function(btn){
    						if(btn=='yes'){
    							me.FormUtil.onUpdate(this, true, {
    		    					dirtyOnly: true
    		    				});
    						}
    					});
    				}else{
    					me.FormUtil.onUpdate(this, true, {
	    					dirtyOnly: true
	    				});
    				}
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('em_id').value, true);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addEmployee', '新增员工', 'jsps/hr/emplmana/employee/employee.jsp');
    			}
    		},
    		'erpSyncButton':{
    			beforerender:function(btn){
    				btn.sync=function() {
    					var masters = btn.getCheckData(), form = Ext.getCmp('form'), w = this.win, me = btn,
    					datas = btn.syncdatas, cal;
    				if(!datas && form && form.keyField && Ext.getCmp(form.keyField) 
    						&& Ext.getCmp(form.keyField).value > 0) {
    					datas = Ext.getCmp(form.keyField).value;
    				}
    				if(cal == null)
    					cal = caller + '!Post';
    				if (!Ext.isEmpty(masters)) {
    					w.setLoading(true);
    					Ext.Ajax.request({
    						url: basePath + 'hr/emplmana/vastPost.action',
    						params: {
    							caller: cal,
    							datas: datas,
    							to: masters
    						},
    						callback: function(opt, s, r) {
    							w.setLoading(false);
    							var rs = new Ext.decode(r.responseText);
    				   			if(rs.exceptionInfo){
    				   				showError(rs.exceptionInfo);
    				   			}
    			    			if(rs.success){
    			    				alert('同步成功!');  								
    			   					w.hide();
    			   					if(me.autoClearCache) {
    			   						me.clearCache();
    			   					}
    			   					me.fireEvent('aftersync', me, cal, datas, masters);    							
    				   			}
    						}
    					});
    				}
    			};
    			}
    		},
    		'#em_id': {
    			afterrender: function(field) {
    				var value = field.getValue();
    				if(value && value > 0) {
    					Ext.defer(function(){
    						me.getJobs(value);
    					}, 200);
    				}
    			} 
    		},
    		'gridpanel': {
    			itemclick: this.onGridItemClick
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getJobs: function(empId) {
		var me = this;
		me.FormUtil.getFieldsValues("job left join empsjobs on job_id=jo_id left join hrorg on org_id=or_id",
				'jo_orgname,jo_name,jo_description,jo_id,or_id,or_name', 'emp_id=' + empId, [], function(data){
			var grid = me.getGrid();
			var datas = Ext.JSON.decode(data), _datas = [];
			if(datas.length > 0) {
				var keys = Ext.Object.getKeys(datas[0]);
				Ext.Array.each(datas, function(d){
					var obj = {};
					Ext.Array.each(keys, function(key){
						obj[key.toLowerCase()] = d[key];
					});
					_datas.push(obj);
				});
			}
			_datas.length > 0 && grid.store.loadData(_datas);
			grid.store.each(function(){
				this.dirty = true;
			});
		});
	},
	onGridItemClick: function(selModel, record) {
        this.GridUtil.onGridItemClick(selModel, record);
    },
    checkEmcode : function(em_code,em_name){
    	var bool = false;
    	Ext.Ajax.request({
    		url: basePath + 'hr/emplmana/checkEmcode.action',
    		async : false,
			params: {
				caller: caller,
				emcode: em_code,
				emname : em_name
			},
			callback : function(o,s,r){
				var rs = new Ext.decode(r.responseText);
				if(rs.log==true || rs.log=="true"){
					bool = true;
				}
			}
    	});
    	return bool;
    }
});